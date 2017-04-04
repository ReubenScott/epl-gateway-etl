package com.soak.etl.utils;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationTest {

  private static String path = "D:/workspace/epl-etl-jar/F_DEP_SHACNACN.SQL";

  /**
   * 运行注解，拼出sql
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    String content = ReadFromFile.readFileByLines(path);
    System.out.println(content);
    Pattern p = Pattern.compile("(create table \\w+\\s*\\(.+\\);)");
//    Pattern p = Pattern.compile("(create table \\w+\\s*\\(.+?\\);)");
    Matcher m = p.matcher(content);
    while (m != null && m.find()) {
      System.out.println(m.group());
    }

  }

  /**
   * 得到type
   * 
   * @param type   * @return
   */
  public static String getColumnType(Type type) {
    String colums = "TEXT";
    if (type == Long.class || (type == Long.TYPE)) {

    } else if (Integer.class == type || (type == Integer.TYPE)) {
      colums = "INTEGER";
    } else if (type == String.class) {

    } else if (type == byte[].class) {
      colums = "BLOB";
    }

    return colums;
  }

}