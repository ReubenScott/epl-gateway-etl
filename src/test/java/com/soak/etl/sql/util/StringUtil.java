package com.soak.etl.sql.util;

public class StringUtil {

	/**找到目标*/
	public static String find (String src ,String start , String end){
		int startIndex = src.indexOf(start)+start.length();
		int endIndex = src.indexOf(end);
		
		return src.substring(startIndex, endIndex).trim();
	}
	
	/**找到目标*/
	public static String findMaxRange (String src ,String start , String end){
		int startIndex = src.indexOf(start)+start.length();
		int endIndex = src.lastIndexOf(end);
		
		return src.substring(startIndex, endIndex).trim();
	}
}
