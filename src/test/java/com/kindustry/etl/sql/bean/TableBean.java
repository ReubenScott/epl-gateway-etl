package com.kindustry.etl.sql.bean;

import java.util.ArrayList;
import java.util.List;

public class TableBean {

  private String tableSql;

  private String schema;

  private String tableName;

  private List<ColumnBean> columnList;

  public TableBean(String tableSql) {
    this.tableSql = tableSql;
  }

  public String getCreateTableSql() {
    return tableSql;
  }

  public void setTableSql(String tableSql) {
    this.tableSql = tableSql;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    if (tableName.contains(".")) {
      schema = tableName.split("\\.")[0];
      this.tableName = tableName.split("\\.")[1];
    } else {
      this.tableName = tableName;
    }

  }

  public List<ColumnBean> getColumnList() {
    return columnList;
  }

  public void setColumnList(List<ColumnBean> columnList) {
    this.columnList = columnList;
  }

  /** 得到insert的模板 */
  public String getInsertTemplate() {
    String insert = "insert into TABLE (COLUMN) values (PRETAG)";
    insert = insert.replaceFirst("TABLE", tableName);
    insert = insert.replaceFirst("COLUMN", getColumns());
    insert = insert.replaceFirst("PRETAG", getPretag("?"));

    return insert;
  }

  /** 得到insert的模板，替换了参数 */
  public String getInsertTemplate(List<String> values) {
    String insert = "insert into TABLE (COLUMN) values (PRETAG)";
    insert = insert.replaceFirst("TABLE", tableName);
    insert = insert.replaceFirst("COLUMN", getColumns());
    insert = insert.replaceFirst("PRETAG", getPretag(values, "'nodata'"));

    return insert;
  }

  private String getColumns() {

    StringBuffer buf = new StringBuffer();

    for (ColumnBean column : columnList) {
      buf.append(column.getName()).append(",");
    }

    if (buf.length() > 0) {
      buf.deleteCharAt(buf.length() - 1);
    }

    return buf.toString();
  }

  private String getPretag(String tag) {
    return getPretag(new ArrayList<String>(), "?");
  }

  private String getPretag(List<String> tagList, String defaultValue) {

    StringBuffer buf = new StringBuffer();
    for (int i = 0, j = columnList.size(); i < j; i++) {
      if (tagList.size() > i) {
        buf.append("'").append(tagList.get(i)).append("'").append(",");
      } else {
        buf.append(defaultValue).append(",");
      }
    }

    if (buf.length() > 0) {
      buf.deleteCharAt(buf.length() - 1);
    }

    return buf.toString();
  }

}
