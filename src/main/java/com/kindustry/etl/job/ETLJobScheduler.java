package com.kindustry.etl.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kindustry.common.io.FileUtil;
import com.kindustry.etl.job.ETLJobScheduler;

public class ETLJobScheduler {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static ETLJobScheduler instance;

  private List<Thread> threadList;

  private final static int BUFCONCURRENT = 2;

  /**
   * 使用调度管理器构造
   * 
   * @param manager
   */
  private ETLJobScheduler() {
    threadList = new ArrayList<Thread>();
  }

  public static ETLJobScheduler getInstance() {
    if (instance == null) {
      synchronized (ETLJobScheduler.class) {
        if (instance == null) {
          instance = new ETLJobScheduler();
        }
      }
    }
    return instance;
  }

  /**
   * <p>
   * 启动该调度器 缓存层
   * </p>
   */
  public void work(int targetNumber) {
    int completed = 0 ;
    int currentBufJobs = 0 ;
    boolean isFinished = false;
    while (completed < targetNumber) {
      isFinished = true;
      for (Thread thread : threadList) {
        // 线程执行状态
        State  state =  thread.getState() ; 
        if (State.TERMINATED != state) {
          isFinished = false;
        }
        
        switch(state){
          case NEW:  // 新增加的
            System.out.println("NEW");
            if(currentBufJobs < BUFCONCURRENT ){
              thread.start();
              currentBufJobs++ ;
            }
            break ;
          case RUNNABLE: 
            System.out.println("RUNNABLE");
            break ;
          case BLOCKED: 
            System.out.println("BLOCKED");
            break ;
          case WAITING: 
            System.out.println("WAITING");
            break ;
          case TIMED_WAITING: 
            System.out.println("TIMED_WAITING");
            break ;
          case TERMINATED: // 线程执行完成
            System.out.println("TERMINATED");
            threadList.remove(thread);
            currentBufJobs-- ;
            completed++;
            break ;
        }
        
      }
      
      if(completed == targetNumber) {
        break;
      }
      
      try {
        Thread.sleep(1000); // 暂停1 秒
      } catch (InterruptedException e) {
        logger.error("exception:", e);
      }
    }

  }

  /**
   * 执行任务调度工作
   */
  public long push(Thread thread) {
    threadList.add(thread);
    return thread.getId();
  }

  /**
   * <p>
   * 强制停止该调度器
   * </p>
   */
  public void stop(String threadName) {
    for (Thread thread : threadList) {
      if (thread.getName().equals(threadName)) {
        try {
          thread.interrupt();
          thread.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
          System.out.println("333 " + thread.getId() + "    " + thread.getName() + " " + thread.isInterrupted());
        }
      }
    }
    System.out.println("thread size : " + threadList.size());
  }

}
