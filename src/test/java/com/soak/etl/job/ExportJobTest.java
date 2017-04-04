package com.soak.etl.job;


import org.junit.Before;
import org.junit.Test;

public class ExportJobTest {
  ExportJob  exportJob ;

  @Before
  public void setUp() throws Exception {
    exportJob = new ExportJob();
  }

  @Test
  public void testExecute() {
    exportJob.work();
  }

}
