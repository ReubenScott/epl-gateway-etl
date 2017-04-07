package com.soak.etl.sql.analysis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.etl.sql.bean.TableBean;


/**
 * 解析从文件中读取的SQL
 * */
public class SQLAnalysis {
	
  protected final Logger log = LoggerFactory.getLogger(this.getClass());


	/**源*/
	private List<String> contentList;
	
	private String content;
	
	private int currentIndex;
	
	private int totalIndex;
	
	private MatchRule rule;
	
	private List<TableBean> tabList;
	
	
	public SQLAnalysis(String src) {
		content = src;
		totalIndex = content.length();
		tabList = new ArrayList<TableBean>();
		rule = new MatchRule();
		//log.debug(content);
		init();
	}
	/**
	 * 
	 * */
	private void init() {
		StringBuffer temp = new StringBuffer();
		contentList = new ArrayList<String>();
		char c;
		for (;currentIndex<totalIndex;currentIndex++){
			c = content.charAt(currentIndex);
			if (c=='\n'){
				contentList.add(temp.toString());
				temp.delete(0, temp.length());
			}else{
				temp.append(c);
			}
		}
		
		//log.debug(contentList);
	}

	/**解析*/
	public void analysis() {

		for (String line : contentList) {

			if (!rule.read(line)) {
				tabList.add(rule.process());
				rule.reset();
			}

		}

	}
	/**得到解析结果*/
	public List<TableBean> getResult(){
		return tabList;
	}
	
}
