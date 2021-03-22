package org.nuxeo.ecm;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nuxeo.ecm.automation.interactive.op.AutomationAutocomplete;

public class TestParsing extends AutomationAutocomplete {

	protected String loadScript(String name) throws Exception {
		return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name), "UTF-8");
	}

	@Test
	public void canExtractNamesFromJS() throws Exception {
		String js = loadScript("test.js");
		List<String> names = parseJS(js);

		assertTrue(names.contains("localVar1"));
		assertTrue(names.contains("localVar2"));
		assertTrue(names.contains("function1"));
		assertTrue(names.contains("function2"));
		assertTrue(names.contains("i"));
	}

}
