package com.soak.etl.job;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.common.io.FileUtil;
import com.soak.common.json.JsonUtil;
import com.soak.common.metic.UUIDGenerator;
import com.soak.common.terminal.SecureCRT;
import com.soak.common.terminal.UserAuthInfo;
import com.soak.etl.constant.JobStatus;
import com.soak.etl.constant.RunPeriod;
import com.soak.etl.model.BufSche;
import com.soak.etl.model.JobMetadata;
import com.soak.etl.model.JobSche;
import com.soak.etl.model.SystemPara;
import com.soak.etl.model.BufferMetadata;
import com.soak.framework.date.DateStyle;
import com.soak.framework.date.DateUtil;
import com.soak.framework.jdbc.Condition;
import com.soak.framework.jdbc.JdbcHandler;
import com.soak.framework.jdbc.Restrictions;
import com.soak.framework.scheduler.SchedulerManager;
import com.soak.framework.thread.ThreadPool;
import com.soak.framework.util.BeanUtil;
import com.soak.framework.util.StringUtil;

/**
 * <p>
 * 测试工作，输出当前时间
 * </p>
 */
public class EtlJobImpl implements EtlJob, Delayed {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private JdbcHandler jdbc = JdbcHandler.getInstance();

  private ThreadPool threadPool = ThreadPool.getInstance();

  private Properties properties;

  public EtlJobImpl() {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("etl.properties");
    this.properties = new Properties();
    try {
      properties.load(inputStream);
      inputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("读取 etl.properties 失败", e);
      return;
    }
  }

  /***
   * 延迟 多少时间
   */
  public long getDelay(TimeUnit unit) {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.add(Calendar.MINUTE, 1);
    // 这个任务的 nextTime 为下一分钟
    long nextTime = c.getTimeInMillis();
    return unit.convert(nextTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
  }

  /***
   * 
   */
  public int compareTo(Delayed o) {
    return (int) (o.getDelay(TimeUnit.MILLISECONDS) - getDelay(TimeUnit.MILLISECONDS));
  }

  /**
   * 检查系统状态
   * 
   * @return
   */
  public synchronized SystemPara checkSystemStatus() {
    // 读取 系统处理日期 ETL.SYSTEMPARA 表
    SystemPara systemPara = jdbc.findOneByAnnotatedSample(null, new SystemPara());
    logger.debug("ETL Check System Status ETLDATE : {} , Step : {} ", systemPara.getSrcDt() , systemPara.getProcStep() );
    return systemPara;
  }

  /**
   * 初始化跑批
   */
  public boolean init(final Date srcDt) {
    logger.debug("ETL INIT DATE: %tF %<tT%n", System.currentTimeMillis());
    // jdbc.loadExcelFile("etl","job_metadata", "D:/abc.xlsx"); // 初始数据入库

    BufferMetadata loadDelList = new BufferMetadata();
    loadDelList.setRuntype("T");
    // loadDelList.setTableName("CBOD_CMEMPEMP");

    // Del 入库文件列表 BUF
    final List<BufferMetadata> tabdellist = jdbc.findByAnnotatedSample(null, loadDelList);

    // 2. buf_sche 初始化设置 处理状态 PRE (PRE/WAITING/RUNNING/DONE/ERROR)
    List<BufSche> bufSches = new ArrayList<BufSche>();
    for (final BufferMetadata table : tabdellist) {
      BufSche bufSche = new BufSche();
      BeanUtil.copyProperties(table, bufSche); // 初始化
//      bufSche.setSid(UUIDGenerator.generate());
      bufSche.setSrcDt(new java.sql.Date(srcDt.getTime()));
      bufSche.setStatus(JobStatus.PRE.getValue());
      bufSches.add(bufSche);
    }
    jdbc.truncateTable(null, "ETL",  "BUF_SCHE"); // 清空 缓冲层调度登记表

    boolean flag = jdbc.saveAnnotatedBean(null, bufSches);
    if (!flag) {
      // TODO 缓冲层 初始化失败 异常情况
      logger.error("ETL init Buf job Failed ! DATE: {}" , srcDt);
    }

    // 初始化　作业层
    jdbc.truncateTable(null, "ETL",  "JOB_SCHE"); // 清空 基础层作业调度表
    

    // 需要执行的 JOB
    JobMetadata jobSample = new JobMetadata();
    jobSample.setRunType("T");
    jobSample.setRunDt(RunPeriod.DAY.getValue());  // 每天执行的
    
    List<JobMetadata> jobMetadatas = jdbc.findByAnnotatedSample(null, jobSample);

    List<JobSche> jobSches = new ArrayList<JobSche>();
    for (JobMetadata item : jobMetadatas) {
      JobSche jobSche = new JobSche();
      BeanUtil.copyProperties(item, jobSche); // 初始化
//      jobSche.setSid(UUIDGenerator.generate());
      jobSche.setSrcDt(new java.sql.Date(srcDt.getTime()));
      jobSche.setStatus(JobStatus.PRE.getValue());
      jobSches.add(jobSche);
    }

    flag = jdbc.saveAnnotatedBean(null, jobSches);
    
    //任务调度管理器
    SchedulerManager scheduler = SchedulerManager.getInstance();
    // 数据导入
    scheduler.putSchedule(new Thread() {
      public void run() {
        loadWork(srcDt);
      }
    });
    // JOB 作业
    scheduler.putSchedule(new Thread() {
      public void run() {
        jobWork(srcDt);
      }
    });

    return false;
  }

  /***
   * ETL 处理
   * 
   */
  public synchronized void work() {
    String backDir = properties.getProperty("BACKDIR").trim() + "/"; // 备份目录
    SystemPara systemPara = checkSystemStatus();

    Date srcDt = systemPara.getSrcDt();

    // 如果处理到最新 退出    while
    if(DateUtil.isBefore(srcDt, DateUtil.getCurrentShortDate())) { // 检查是否处理成功
      if(JobStatus.WAITING.getValue().equals(systemPara.getProcStep())) {
        // 初始化跑批作业
        init(srcDt);
      }

//      // 休息1小时
//      try {
//        Thread.sleep(1000 * 60 * 60); // 休息1小时
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      
//      //重新获取日期
//      srcDt = checkSystemStatus().getSrcDt();
    }
  }
  
  
  

  /***
   * ETL 数据导入处理　
   * 
   */
  public void loadWork(final Date curEtlDate) {
    // //TODO 读取 系统处理日期 ETL.SYSTEMPARA 表
    // SystemPara systemPara = jdbc.findOneByAnnotatedSample(null, new SystemPara());
    final String receFileDir = properties.getProperty("RECEFILEDIR").trim() + "/";
    String fileDate = DateUtil.formatDate(curEtlDate, DateStyle.YYYYMMDD); // 文件时间名

    System.out.println("receFileDir :" + receFileDir);

    // 处理 状态为
    BufSche preBufSche = new BufSche();
    // preBufSche.setTableName("MPS_FCHARGE_REC");
    Restrictions preRestrictions = new Restrictions();
    preRestrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
    preRestrictions.addCondition(Condition.Equal, "P_STATUS", JobStatus.PRE.getValue());

    // Del 入库文件列表
    List<BufSche> tabdellist = jdbc.findByAnnotatedSample(null, preBufSche, preRestrictions);

    while (tabdellist.size() > 0) {
      // 开始调度 BUF
      for (final BufSche table : tabdellist) {
        String delname = table.getDelName();
        // 下发成功标识文件
        String markerfile = receFileDir + delname + "_" + fileDate + ".OK";
        String sqlfile = receFileDir + delname + "_" + fileDate + ".SQL";
        String delfile = receFileDir + delname + "_" + fileDate + ".del";

        List<BufSche> receivedList = new ArrayList<BufSche>();

        // 2 TODO 监控 下发文件
        if (FileUtil.isFileExits(markerfile) && FileUtil.isFileExits(sqlfile) && FileUtil.isFileExits(delfile)) {
          // 文件存在
          receivedList.add(table);

          // 设置处理 状态为 WAITING
          BufSche bufSche = new BufSche();
          bufSche.setStatus(JobStatus.WAITING.getValue());
          Restrictions restrictions = new Restrictions();
          restrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
          restrictions.addCondition(Condition.Equal, "SCHEMATA", table.getSchema());
          restrictions.addCondition(Condition.Equal, "STBNAME", table.getTableName());

          // Update ETL.BUF_SCHE Set P_STATUS='WAITING' where SRC_DT='2015-12-28' AND SCHEMATA='EDW' AND STBNAME='MPS_FCHARGE_REC'
          boolean updateFlag = jdbc.updateAnnotatedEntity(null, bufSche, restrictions);
//          logger.debug("Update BUF_SCHE Set P_STATUS='WAITING' where SRC_DT={} AND SCHEMATA={} AND STBNAME={}" , updateFlag);
        } else {
           // 下发DEL 文件未到
          System.out.println("cannot find table : " + delname + " file ");
        }
        
        // 开启处理线程
        for (final BufSche received : receivedList) {
          Thread bufloadThread = new Thread(received.getTableName()) {
            public void run() {
              loadBuffData(received, receFileDir, curEtlDate);
            }
          };
          bufloadThread.setPriority(Thread.MAX_PRIORITY); // 设置优先级
          threadPool.push(bufloadThread);
        }
        
      }

      // 休息5分钟
      try {
        Thread.sleep(1000 * 60 * 5); // 休息5分钟
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      tabdellist = jdbc.findByAnnotatedSample(null, preBufSche, preRestrictions);
    }

  }

  /***
   * ETL JOB 处理
   */
  public void jobWork(final Date curEtlDate) {
    // 处理 状态 不为　DONE
    JobSche unDoneJobSche = new JobSche();
    // preBufSche.setTableName("MPS_FCHARGE_REC");
    Restrictions preRestrictions = new Restrictions();
    preRestrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
    preRestrictions.addCondition(Condition.UnEqual, "JOB_STATUS", JobStatus.DONE.getValue());

    // 未完成作业列表
    List<JobSche> jobList = jdbc.findByAnnotatedSample(null, unDoneJobSche, preRestrictions);

    // 开始作业
    while (jobList.size() > 0) {
      for (final JobSche jobSche : jobList) {
        if (JobStatus.WAITING.getValue().equals(jobSche.getStatus())) { // 待处理作业
          // 设置处理 状态为 PROCESSING
          jobSche.setStatus(JobStatus.PROCESSING.getValue());
          jobSche.setStartTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
          Restrictions restrictions = new Restrictions();
          restrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
          restrictions.addCondition(Condition.Equal, "JOB_NM", jobSche.getJobName());

          boolean updateFlag = jdbc.updateAnnotatedEntity(null, jobSche, restrictions);
          if (!updateFlag) {
            logger.error("update ETL Step : JOB [" + jobSche.getJobName() + "] status [PROCESSING]  ERROR !");
            return;
          } else {
            logger.debug("update ETL Step : JOB [" + jobSche.getJobName() + "] status [PROCESSING]  Success !");
          }

          // 开启处理线程
          Thread jobThread = new Thread(jobSche.getJobName()) {
            public void run() {
              // TODO 开启存储过程
              logger.debug("ETL Step : JOB [" + jobSche.getJobName() + "] 111111111111111111111111111111111111111111111 !");
              runJobSche(jobSche.getJobName(),jobSche.getJobCmd(), curEtlDate);
            }
          };
          jobThread.setPriority(Thread.MAX_PRIORITY); // 设置优先级
          threadPool.push(jobThread);

        }

      }

      // 重复间隔
      try {
        Thread.sleep(1000 * 30); // 休息30秒
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      //TODO  检测　PRE_JOB 是否完成    若已完成　设置 JOB　状态为　WAITING

      jobList = jdbc.findByAnnotatedSample(null, unDoneJobSche, preRestrictions);
    }

    // TODO JOB 　1.跑批完成检测　　2.处理结束 切换到下一天  　
    

  }

  /***
   * LOAD BUFF 加载缓冲层 表数据
   * 
   */
  public void loadBuffData(BufSche table, String dir, Date curEtlDate) {
    String tablename = table.getTableName();
    String schema = table.getSchema();
    String delname = table.getDelName();
    Integer split = table.getSplit();

    // 数据导入 分割符号 0X1D 29 dataSplit
    char dataSplit = (char) split.intValue();

    String backDir = properties.getProperty("BACKDIR").trim() + "/"; // 备份目录
    String fileDate = DateUtil.formatDate(curEtlDate, DateStyle.YYYYMMDD); // 文件时间名
    // 下发文件
    String markerfile = dir + delname + "_" + fileDate + ".OK";
    String sqlfile = dir + delname + "_" + fileDate + ".SQL";
    String delfile = dir + delname + "_" + fileDate + ".del";

    // 设置处理 状态为 PROCESSING
    BufSche bufSche = new BufSche();
    bufSche.setStatus(JobStatus.PROCESSING.getValue());
    bufSche.setStartTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
    Restrictions restrictions = new Restrictions();
    restrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
    restrictions.addCondition(Condition.Equal, "SCHEMATA", schema);
    restrictions.addCondition(Condition.Equal, "STBNAME", tablename);

    boolean updateFlag = jdbc.updateAnnotatedEntity(null, bufSche, restrictions);
    if (!updateFlag) {
      logger.error("update ETL Step : LOAD [" + schema + "." + tablename + "] status [PROCESSING]  ERROR !");
      return;
    } else {
      logger.debug("update ETL Step : LOAD [" + schema + "." + tablename + "] status [PROCESSING]  Success !");
    }

    // 1. DDLFile 建表语句 createTableDDL
    StringBuffer tableddl = new StringBuffer();

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(sqlfile));
      String tempString = null;
      // 显示行号
      int lineNumber = 1;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        if (StringUtil.isEmpty(tempString)) {
          continue;
        }
        tempString = tempString.trim();

        // 首行 替换 数据库表名
        if (lineNumber == 1) {
          String[] arrayStr = tempString.split(" ");
          StringBuffer tableStr = new StringBuffer();
          for (int i = 0; i < arrayStr.length; i++) {
            String tmpStr = arrayStr[i];
            if (i == 2) {
              // 替换 数据库表名
              tmpStr = schema + "." + tablename;
            }
            tableStr.append(tmpStr + " ");
          }
          tempString = tableStr.toString();
        }

        tableddl.append(tempString + "\r\n");
        // tableddl.append(tempString);
        lineNumber++;
      }

      // TODO 注意 数据源
      switch (jdbc.getDBProductType(null)) {
      case DB2:
        // DB2 添加表空间
        String tableSpace = table.getTableSpace();
        String indexSpace = table.getIndexSpace();
        if (!StringUtil.isEmpty(tableSpace)) {
          tableddl.append(" IN " + tableSpace);
        }
        if (!StringUtil.isEmpty(indexSpace)) {
          tableddl.append(" INDEX IN " + indexSpace);
        }
        tableddl.append(" Compress Yes "); // 表压缩
        break;
      case MYSQL:
        break;
      default:
        break;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    // 比较表结构 重建表
    if (jdbc.isTableExits(null, schema, tablename)) {
      // 表结构改变的话 表删除重建
      if (jdbc.dropTable(null, schema, tablename)) {
        jdbc.execute(null, tableddl.toString());
      }
    } else {
      jdbc.execute(null, tableddl.toString());
    }

    // 数据导入
    if (jdbc.isTableExits(null, schema, tablename)) {
      if (FileUtil.isFileExits(delfile)) {
        // 清空表
        if (jdbc.truncateTable(null, schema, tablename)) {
          if (jdbc.loadDelFile(null, schema, tablename, delfile, dataSplit)) {
            // 导入 数据成功
            logger.info("load [" + schema + "." + tablename + "] success !");
            // 设置处理 状态为 DONE
            bufSche.setStatus(JobStatus.DONE.getValue());

            // 更改JOB 作业状态为 WAITING　存储过程
            JobSche jobSche = new JobSche();
            jobSche.setStatus(JobStatus.WAITING.getValue());
            Restrictions jobRestrictions = new Restrictions();
            jobRestrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
            jobRestrictions.addCondition(Condition.Equal, "JOB_NM", table.getJobName());

            boolean jobUpdateFlag = jdbc.updateAnnotatedEntity(null, jobSche, jobRestrictions);
            while (!jobUpdateFlag) {
              logger.error("update ETL Step : JOB [" + schema + "." + table.getJobName() + "] status [WAITING]  ERROR !");
            }

            // 处理完成移动文件
            FileUtil.moveFile(markerfile, backDir);
            FileUtil.moveFile(sqlfile, backDir);
            FileUtil.moveFile(delfile, backDir);

          } else {
            logger.error("load [" + schema + "." + tablename + "] Fail !");
            // del 数据文件不存在 设置处理 状态为 ERROR
            bufSche.setStatus(JobStatus.ERROR.getValue());
          }
        } else {
          logger.error("truncate table [" + schema + "." + tablename + "] Fail !");
          // del 数据文件不存在 设置处理 状态为 ERROR
          bufSche.setStatus(JobStatus.ERROR.getValue());
        }
      } else {
        logger.info("table [" + schema + "." + tablename + "] DEL File not exits !");
        // del 数据文件不存在 设置处理 状态为 ERROR
        bufSche.setStatus(JobStatus.ERROR.getValue());
      }
    } else {
      logger.info("table [" + schema + "." + tablename + "] not exits !");
      // 目标表不存在 设置处理 状态为 ERROR
      bufSche.setStatus(JobStatus.ERROR.getValue());
    }

    // 设置处理状态
    bufSche.setEndTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
    updateFlag = jdbc.updateAnnotatedEntity(null, bufSche, restrictions);
    if (!updateFlag) {
      logger.error("update table [" + bufSche.getSchema() + "." + bufSche.getTableName() + "]  at  etltable : [" + schema + "  " + tablename + "] stats [" + bufSche.getStatus()
          + "]  ERROR !");
      return;
    }

  }

  /**
   * 运行 JOB　储存过程 runJobSche(received, receFileDir, curEtlDate);
   * 
   * @param filePath
   */
  public void runJobSche(String jobName, String jobCmd, Date date) {
    // TODO 调用存储过程 提取出来
    // List result = jdbc.callProcedure(null, "EDW.SP_F_CHN_SJ_FCHARGE_REC", new String[] { "2015-12-28" }, Types.INTEGER);
    List result = jdbc.callProcedure(null, jobCmd, new Object[] { date }, Types.INTEGER);
    

    // 设置处理 状态为 DONE
    JobSche jobSche = new JobSche();
    jobSche.setEndTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
    Restrictions restrictions = new Restrictions();
    restrictions.addCondition(Condition.Equal, "SRC_DT", date);
    restrictions.addCondition(Condition.Equal, "JOB_NM", jobName);
    restrictions.addCondition(Condition.Equal, "JOB_CMD", jobCmd);

    if(result == null){  //  存储过程 不存在 或   执行 存储过程失败
      jobSche.setStatus(JobStatus.ERROR.getValue());
    } else {
      for (Object obj : result) {
        System.out.println(obj);
        Integer res = (Integer) obj;
        if (res == 0) {
          // 执行存储过程成功
          // 设置处理 状态为 DONE
          jobSche.setStatus(JobStatus.DONE.getValue());
        }
      }
    }
    
    boolean updateFlag = jdbc.updateAnnotatedEntity(null, jobSche, restrictions);
    if (!updateFlag) {
      logger.error("update ETL Step : JOB [" + jobName + "] status ["+jobSche.getStatus()+"]  ERROR !");
      return;
    } else {
      logger.debug("update ETL Step : JOB [" + jobName + "] status ["+jobSche.getStatus()+"]  Success !");
    }

  }

  /***
   * 
   * CVS（默认以逗号分割的） 文件入库
   * 
   * @param tablename
   *          入库表名
   * 
   * @param filePath
   *          文件路径
   * 
   * @param split
   *          字段分隔符
   * 
   */
  public void downloadDataFile() {
    UserAuthInfo userAuthInfo92 = new UserAuthInfo("32.137.32.92", 22, "etl", "etl123");
    UserAuthInfo userAuthInfo170 = new UserAuthInfo("32.137.126.170", 22, "etl", "etl123");

    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    Date startDate = DateUtil.parseShortDate("2015-12-28");
    Long lstartTime = startDate.getTime();
    Long oneDay = 1000 * 60 * 60 * 24l;
    Long endTime = DateUtil.getLastDayOfMonth(startDate).getTime();
    Long time = lstartTime;
    while (time <= endTime) {
      Date eachDate = new Date(time);
      time += oneDay;
      String like = "*" + df.format(eachDate) + ".*";
      SecureCRT.syncDirectory(userAuthInfo92, userAuthInfo170, "/home/etl/data/ftpdata", "/home/etl/data/ftpdata", like);
      SecureCRT.delete(userAuthInfo92, "/home/etl/data/ftpdata", like);
      break;
    }
  }

  // public void loadDelFile(String tablename, String filePath, char split) {
  // JdbcHandler jdbc = JdbcHandler.getInstance();
  // jdbc.loadDelFile(null, "EDW", tablename, filePath, split);
  // }

}