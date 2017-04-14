package com.soak.etl.worker;

import com.soak.etl.job.EtlJobImpl;

public class Launch {

  public void testLoadCVS() {
//    etlJobImpl.downloadDataFile();
  }
  
  public static void main(String args[]){
    EtlJobImpl etlJobImpl = new EtlJobImpl();
    etlJobImpl.work();
    
    while(true){
      // 休息1小时
      try {
        Thread.sleep(1000 * 60 * 60); // 休息1小时
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }



}
