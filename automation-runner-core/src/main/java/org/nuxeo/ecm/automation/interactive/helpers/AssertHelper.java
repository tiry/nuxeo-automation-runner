package org.nuxeo.ecm.automation.interactive.helpers;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.context.ContextHelper;

public class AssertHelper implements ContextHelper {
	
	protected static ThreadLocal<List<AssertEntry>> memoryLog = new ThreadLocal<>();	
	
	public static List<AssertEntry> initMemoryLog() {
		memoryLog.set(new ArrayList<>());
		return getMemoryLog();
	}

	public static void cleanMemoryLog() {
		memoryLog.remove();
	}

	public static List<AssertEntry> getMemoryLog() {
		return new ArrayList(memoryLog.get());
	}
	
	protected boolean record(boolean result, String title) {
		List<AssertEntry> msgs = memoryLog.get();
		if (msgs==null) {
			msgs = initMemoryLog();
		}		
		msgs.add(new AssertEntry(result, title));
		
		return result;
	}
	
	public boolean assertEquals(Object expected, Object actual, String title) {
		return record(expected.equals(actual),title); 
	}

	public boolean assertTrue(boolean condition) {
		int idx = 0;
		if(memoryLog.get()!=null) {
			idx = memoryLog.get().size();
		}
		return assertTrue(condition, "Assertion #" + (idx+1));
	}

	public boolean assertTrue(boolean condition, String title) {
		return record(condition, title);
	}	
}
