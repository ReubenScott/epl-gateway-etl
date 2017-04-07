package com.soak.etl.sql.analysis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.etl.sql.bean.ColumnBean;
import com.soak.etl.sql.bean.TableBean;
import com.soak.etl.sql.util.StringUtil;

/**
 * 单表解析
 * */
public class TableAnalysis {

  protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String sql;
	
	private TableBean bean;
	
	private List<ColumnBean> columnList;
	
	
	public TableAnalysis(String sql) {
		this.sql =sql.toLowerCase();
		bean = new TableBean(sql);
		columnList = new ArrayList<ColumnBean>();
	}
	
	public TableBean generate(){
		parseTableName();
		parseColumn();
		bean.setColumnList(columnList);
		return bean;
	}
	
	private void parseTableName(){
		String tableName = StringUtil.find(sql, "table", "(");
		bean.setTableName(tableName);
	}
	
	private void parseColumn(){
		//此正则表达式只支持逗号前有空格的分割
		String columnSql = StringUtil.findMaxRange(sql, "(", ")");
		columnSql =	columnSql.replaceAll("\\([^)]*\\)"," ");
		columnSql = columnSql.replaceAll(","," ,");
		String[] column = columnSql.split("\\s+,");
		for (String c : column){
			columnList.add(new ColumnBean(c.trim()));
		}
	}
}
