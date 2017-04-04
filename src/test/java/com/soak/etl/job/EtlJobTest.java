package com.soak.etl.job;



import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


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
  public void testETLJob() {
    etlJobImpl.work();
  //  etlJobImpl.loadDelFile("IND_REPORT_DATA", "E:/ftpdata/P_063_CMIS_IND_REPORT_DATA_20150708.del", (char)29 );
  }

//  @Test
  public void testLoadDelFile() {
//    etlJobImpl.loadDelFile("FIM_NFT_ACCOUNT_XQTALLY",  "E:/ftpdata/P_063_FIM_NFT_ACCOUNT_XQTALLY_20160616.del", (char)29 );
//    FIM_NFT_ACCOUNT_XQTALLY
//    CMIS_IND_INFO
//    CMIS_GUARANTY_CONTRACT
//    CMIS_BUSINESS_CONTRACT
//    CMIS_BILL_INFO
//    etlJobImpl.loadDelFile("IND_REPORT_DATA", "E:/ftpdata/P_063_CMIS_IND_REPORT_DATA_20150708.del", (char)29 );
  }
  
//  @Test
  public void testLoadCVS() {
    etlJobImpl.downloadDataFile();
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
