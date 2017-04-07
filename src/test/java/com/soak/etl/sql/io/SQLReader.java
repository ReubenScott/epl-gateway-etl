package com.soak.etl.sql.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取SQL文件
 * */
public class SQLReader {
	
  protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private File f;
	
	private BufferedReader reader;
	
	private StringBuffer buf;
	/***/
	public SQLReader (String path){
		f = new File(path);
		if (!f.exists()){
			throw new RuntimeException("File <"+f.getAbsolutePath()+"> not exist");
		}
		if (f.isDirectory()){
			throw new RuntimeException("File <"+f.getAbsolutePath()+"> is a directory");
		}
		
		try {
			reader = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		log.debug("file init finished");
	}
	/**加载*/
	public void load (){
		buf = new StringBuffer();
		String temp;
		try {
			while ( (temp=reader.readLine())!=null){
				buf.append(temp).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("file load success");
		//log.debug(buf);
	}
	
	/**得到SQL内容*/
	public String getSQLContent(){
		return buf.toString();
	}
}
