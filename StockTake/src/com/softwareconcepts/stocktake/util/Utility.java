package com.softwareconcepts.stocktake.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

public class Utility {
	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public static String surroundWithParentheses(String value) {
		return "(" + value + ")";
	}

	public static boolean isEmpty(List<?> list) {
		boolean isEmpty = false;
		if (list == null || list.size() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

	public static String getUniqueToken() {
		return UUID.randomUUID().toString();
	}

	public static boolean isNull(String string) {
		return (string == null || string.length() == 0);
	}
}
