package com.kindustry.etl.job;



import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.kindustry.common.date.DateUtil;
import com.kindustry.etl.constant.JobStatus;
import com.kindustry.etl.job.EtlJobImpl;
import com.kindustry.etl.model.BufSche;
import com.kindustry.framework.jdbc.Condition;
import com.kindustry.framework.jdbc.Restrictions;


public class EtlJobTest {

  EtlJobImpl etlJobImpl;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    etlJobImpl = new EtlJobImpl();
  }

  @After
  public void tearDown() throws Exception {
  }
  
  
  @Test
  public void testLoadBuffData() {
//    "CBOD_CICIFCIF"  "CBOD_ECCIFIDI"  P_063_CBOD_ECCIFIDI  CMIS_IND_INFO
    BufSche bufSche = new BufSche();
    bufSche.setSchema("EDW");
    bufSche.setSplit(29);
    bufSche.setTableName("CMIS_IND_INFO");
    bufSche.setDelName("P_063_CMIS_IND_INFO");
//    etlJobImpl.loadBuffData(bufSche, "E:\ftpdata",  new java.sql.Date(DateUtil.parseShortDate("2017-04-13").getTime()));
    etlJobImpl.loadBuffData(bufSche, "E:/ftpdata/", DateUtil.parseShortDate("2017-05-11"));
  }
  
//  @Test
  public void testLoadCVS() {
//    etlJobImpl.downloadDataFile();
  }
  

//  @Test
  public void testLoadExcelFile() {
//    Date date1 = new Date();//获取当前时间
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    String str = sdf.format(date1);//时间存储为字符串
//    System.out.println(str);
//    Timestamp.valueOf(str);//转换时间字符串为Timestamp
//    System.out.println(Timestamp.valueOf(str));//输出结果
//    etlJobImpl.loadExcelFile("atnd_punch_record", "E:/考勤/2016年03-1.xls");
//    etlJobImpl.loadExcelFile("atnd_punch_record", "E:/考勤/301603-2.xls");
  }



}
