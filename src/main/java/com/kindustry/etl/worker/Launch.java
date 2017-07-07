package com.kindustry.etl.worker;

import com.kindustry.etl.job.EtlJob;
import com.kindustry.etl.job.EtlJobImpl;
import com.kindustry.etl.job.LoaderJobImpl;

public class Launch {

  public void testLoadCVS() {
//    etlJobImpl.downloadDataFile();
  }
  
  public static void main(String args[]){
//    EtlJob etlJob = new EtlJobImpl();  
    EtlJob etlJob = new LoaderJobImpl();  
    etlJob.work();
    
//    while(true){
//      // 休息1小时
//      try {
//        Thread.sleep(1000 * 60 * 60); // 休息1小时
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
  }



}
