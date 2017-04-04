package com.soak.etl.worker;

import com.soak.etl.job.EtlJobImpl;

public class Launch {

  public void testLoadCVS() {
//    etlJobImpl.downloadDataFile();
  }
  
  public static void main(String args[]){
    EtlJobImpl etlJobImpl = new EtlJobImpl();
    etlJobImpl.work();
  }



}
