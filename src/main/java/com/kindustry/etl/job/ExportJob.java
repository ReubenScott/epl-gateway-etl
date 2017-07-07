package com.kindustry.etl.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

import com.kindustry.etl.job.EtlJob;
import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.xml.XmlSqlMapper;


/**
 * <p>
 * 测试工作，输出当前时间
 * </p>
 */
public class ExportJob implements EtlJob {

  public void work() {
    System.out.printf("JOB OUTPUT: %tF %<tT%n", System.currentTimeMillis());

//    String sql = "select * from edw.CBOD_ECCMRAMR";

    String sql =  XmlSqlMapper.getInstance().getPreparedSQL("高管活期积数, 余额");
    sql = sql.replaceAll("@startDate", "2015-11-21");
    sql = sql.replaceAll("@endDate", "2015-11-30");
    
    System.out.println(sql);
    
    Workbook workbook = JdbcTemplate.getInstance().exportNamelessWorkbook(sql);

    FileOutputStream fos;
    try {
      fos = new FileOutputStream(new File("D:/workspace/test1.xlsx"));
      workbook.write(fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  public void exportExcel() {

  }

}
