package com.soak.etl.sql;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.IOUtils;

import com.soak.etl.sql.analysis.SQLAnalysis;
import com.soak.etl.sql.bean.TableBean;
import com.soak.etl.sql.io.SQLReader;
import com.soak.framework.io.IOHandler;

public class MyTest {
  
  

  public static byte[] readByteArray() {

    String filename = "D:\\home\\20160318\\YKJD_LN_DUEBILL.del";
    File file = new File(filename);
    
    if(file.isFile()){
      BufferedReader reader = null ;
      try {
        reader = new BufferedReader(new FileReader(file));
        String  strLine =  reader.readLine(); 
          
        
        
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if(reader!=null){
            reader.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      
    }
    
    byte[] bt = null;
    BufferedInputStream stream = null ;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {       
      stream = new BufferedInputStream(new FileInputStream(filename));
      byte[] buffer = new byte[4096];
      int read = 0;
      while ((read = stream.read(buffer)) != -1) {
        baos.write(buffer, 0, read);
      }
      baos.flush();
      bt = baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (baos != null) {
        try {
          baos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return bt;
  }
  

  public void testReadFileByBytes() {
    String filename = "D:\\home\\20160318\\YKJD_LN_DUEBILL.del";
    byte[] bt;
    try {
      bt = IOHandler.readByteArray(new FileInputStream(filename));
      // String encode = IOHandler.getCharSetEncoding(new File(filename));

      List<byte[]> list = IOHandler.splitBytes(bt, (byte) 10);
      List<String[]> result = new ArrayList<String[]>();

      for (byte[] lineByte : list) {
        String line = new String(lineByte, "GBK");
        System.out.println(line);
      }
      // result.add(new String(IOHandler.replace(lineByte, (byte) 29, (byte) 44), encode).split(","));

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    SQLReader reader = new SQLReader("target/classes/com/soak/etl/sql/test.SQL");
    reader.load();

    SQLAnalysis analysis = new SQLAnalysis(reader.getSQLContent());

    analysis.analysis();

    List<TableBean> list = analysis.getResult();

    List<String> dataList = new ArrayList<String>();
    dataList.add("1");
    dataList.add("saintren");
    dataList.add("30");
    dataList.add("测试");

    for (TableBean bean : list) {
      System.out.println(bean.getSchema());
      System.out.println(bean.getTableName());
      System.out.println(bean.getCreateTableSql());
      // System.out.println(bean.getInsertTemplate());
      // System.out.println(bean.getInsertTemplate(dataList));
    }
  }

}
