package com.kindustry.etl.sql.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kindustry.etl.sql.bean.TableBean;


/**
 * 匹配规则,用于得到解析用的单个表SQL
 * */
public class MatchRule {
	
  protected final Logger log = LoggerFactory.getLogger(this.getClass());


	private static String startTag = "create";
	
	private static char commentTag = '-';
	
	private static String endTag = ")";
	
	private StringBuffer buf;
	
	private boolean readFlag;
	
	
	public MatchRule() {
		buf = new StringBuffer();
	}
	
	/**
	 * 读取下一个字符，如果不需要再读入，则返回false
	 * 如果需要继续读入，返回true
	 * 读取结束后可以处理
	 * */
	public boolean read(String line){
		//注释行
		if (isCommentLine(line)){
			log.debug("comment line,skip");
			return true;
		}
		//开始行
		if (!readFlag && isStartLine(line)){
			log.debug("begin read");
			buf.append(line);
			readFlag = true;
			return true;
		}
		//结束行
		if (readFlag && isEndLine(line)){
			log.debug("read end");
			buf.append(line);
			readFlag = false;
			return false;
		}
		//进入读入状态后
		if (readFlag){
			//log.debug("read success");
			buf.append(line);
		}
		
		return true;
	}
	
	
	public TableBean process(){
		return new TableAnalysis(buf.toString()).generate();
	}

	/**重置*/
	public void reset() {
		buf.delete(0, buf.length());
		readFlag = false;
	}
	
	/**是否注释行*/
	private boolean isCommentLine(String line){
		if (line.startsWith("-")){
			//注释行
			return true;
		}
		return false;
	}
	
	/**是否起始行*/
	private boolean isStartLine(String line){
		
		if (line.toLowerCase().startsWith(startTag)){
			return true;
		}
		return false;
	}
	
	/**是否结束行*/
	private boolean isEndLine (String line){
		if (line.trim().endsWith(endTag)){
			return true;
		}
		
		return false;
	}
}
