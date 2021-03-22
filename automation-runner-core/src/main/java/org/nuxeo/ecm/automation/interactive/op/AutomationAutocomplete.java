package org.nuxeo.ecm.automation.interactive.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

import jdk.nashorn.api.tree.CompilationUnitTree;
import jdk.nashorn.api.tree.ExpressionTree;
import jdk.nashorn.api.tree.FunctionDeclarationTree;
import jdk.nashorn.api.tree.IdentifierTree;
import jdk.nashorn.api.tree.Parser;
import jdk.nashorn.api.tree.Tree;
import jdk.nashorn.api.tree.VariableTree;

/**
import jdk.nashorn.internal.ir.BlockStatement;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.Statement;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;
**/

/**
 *
 */
@SuppressWarnings({ "removal", "deprecation" })
@Operation(id = AutomationAutocomplete.ID, category = Constants.CAT_DOCUMENT, label = "Automation.KernelAutocomplete", description = "Describe here what your operation does.")
public class AutomationAutocomplete {

	public static final String ID = "Automation.Autocomplete";

	protected static final boolean addSignature = true;

	protected static List<String> jsReservedKeyWords = Arrays.asList("break", "case", "catch", "class", "const",
			"continue", "debugger", "default", "delete", "do", "else", "export", "extends", "finally", "for",
			"function", "if", "import", "in", "instanceof", "new", "return", "super", "switch", "this", "throw", "try",
			"typeof", "var", "void", "while", "with", "yield");

	@Context
	protected CoreSession session;

	@Context
	protected OperationContext ctx;

	@Param(name = "prefix", required = true)
	protected String prefix;

	protected List<String> parseJS(String code) {

		List<String> names = new ArrayList<>();
		jdk.nashorn.api.tree.Parser parser = Parser.create(new String[] { "-strict", "--language=es6" });

		CompilationUnitTree unit = parser.parse("some.js", code, null);

		List<? extends Tree> elements = unit.getSourceElements();
		for (Tree tree : elements) {
			if (tree instanceof FunctionDeclarationTree) {
				FunctionDeclarationTree func = (FunctionDeclarationTree) tree;
				names.add(func.getName().getName());
			}
			if (tree instanceof VariableTree) {
				VariableTree variable = (VariableTree) tree;
				ExpressionTree exp = variable.getBinding();
				if (exp instanceof IdentifierTree) {
					IdentifierTree id = (IdentifierTree) exp;
					names.add(id.getName());
				}
			}
		}

		return names;
	}

	protected String prepopulateSignature(OperationType ot) throws Exception {

		StringBuffer sb = new StringBuffer("(");

		String input = ot.getInputType();
		if ("document".equals(input)) {
			sb.append("doc");
		} else if ("documents".equals(input)) {
			sb.append("docs");
		} else if ("blob".equals(input)) {
			sb.append("blob");
		} else if ("blobs".equals(input)) {
			sb.append("blobs");
		} else if ("void".equals(input) || input == null) {
			sb.append("null");
		}

		sb.append(",{");

		for (org.nuxeo.ecm.automation.OperationDocumentation.Param param : ot.getDocumentation().getParams()) {
			sb.append("'" + param.getName() + "' : null,");
		}

		sb.append("})");
		return sb.toString();
	}

	protected List<String> getOperationNames() throws Exception {

		List<String> names = new ArrayList<>();
		AutomationService service = Framework.getService(AutomationService.class);
		for (OperationType ot : service.getOperations()) {
			if (addSignature) {
				String signature = prepopulateSignature(ot);
				names.add(ot.getId() + signature);
				for (String alias : ot.getAliases()) {
					names.add(alias + signature);
				}
			} else {
				names.add(ot.getId());
				for (String alias : ot.getAliases()) {
					names.add(alias);
				}
			}
		}
		Collections.sort(names);
		return names;
	}

	@OperationMethod
	public String run(String content) throws Exception {

		StringJoiner suggestions = new StringJoiner("\n");

		// keywords
		for (String kw : jsReservedKeyWords) {
			if (kw.startsWith(prefix)) {
				suggestions.add(kw);
			}
		}

		// js parsing
		for (String kw : parseJS(content)) {
			if (kw.startsWith(prefix)) {
				suggestions.add(kw);
			}
		}

		// automation names
		for (String op : getOperationNames()) {
			if (op.startsWith(prefix)) {
				suggestions.add(op);
			}
		}

		return suggestions.toString();
	}

}
