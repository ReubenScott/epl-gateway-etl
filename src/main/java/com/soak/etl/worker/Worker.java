/*package com.soak.etl.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.etl.reader.Reader;

public class Worker<T> {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  private Worker nextWorker;
  private Reader<T> reader;
  private final static int GROUPSIZE = 3500;


  public void setReader(Reader<T> reader) {
    this.reader = reader;
  }

  public void work() {
    List<Thread> threadList = new ArrayList<Thread>();
    List<T> list = reader.readCSVFile();

    // 拆解数据为多个线程执行
    for (int i = 0; i < list.size() / GROUPSIZE + 1; i++) {
      int groupTatal = GROUPSIZE * (i + 1);
      if (groupTatal >= list.size()) {
        groupTatal = list.size();
      }
      List<T> currentList = list.subList(i * GROUPSIZE, groupTatal);
      Thread thread = new Thread();
      thread.start();
      threadList.add(thread);
    }

    logger.info("Thread size is :" + threadList.size());

    logger.info("All etl threads are started");

    boolean isFinished = false;
    while (!isFinished) {
      isFinished = true;
      for (Thread thread : threadList) {
        if (State.TERMINATED != thread.getState()) {
          isFinished = false;
        }
        try {
          Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
          logger.error("exception:", e);
        }
      }
    }

    logger.info("Etl job is completed.");

    if (nextWorker != null) {
      nextWorker.work();
    } else {


//      EtlLoggerFactory.getLogger().sendEmail("ETL Job:" + jobId + " is completed.", content);
//      logger.info("Email is sent out:" + content);
    }
  }

  @SuppressWarnings("rawtypes")
  protected void setNextWorker(Worker worker) {
    this.nextWorker = worker;
  }

  private int getTotalCount(String type) {
    Properties properties = null;
    String path = properties.getProperty("log.path");
//    String jobId = EtlJobFactory.getCurrentJobId();
    String jobId = null ;
    String fileName = jobId + "_" + type + ".CSV";
    File file = new File(path + fileName);
    if (!file.exists()) {
      return 0;
    }
    FileReader reader = null;
    BufferedReader in = null;
    int lineNumber = -1;
    try {
      reader = new FileReader(path + fileName);

      in = new BufferedReader(reader);

      String lineValue;
      while ((lineValue = in.readLine()) != null) {
        if (lineValue.replace("\r", "").replace("\n", "").length() > 0) {
          lineNumber++;
        }
      }
    } catch (Exception e) {
      logger.error("Exception:", e);
    } finally {
      try {
        in.close();
        reader.close();
      } catch (IOException e) {
        logger.error("Exception:", e);
      }
    }
    return lineNumber > 0 ? lineNumber : 0;
  }

}
*/