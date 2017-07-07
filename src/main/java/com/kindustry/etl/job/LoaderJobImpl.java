package com.kindustry.etl.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kindustry.common.constant.DateStyle;
import com.kindustry.common.date.DateUtil;
import com.kindustry.common.terminal.FtpZilla;
import com.kindustry.common.terminal.SecureCRT;
import com.kindustry.common.terminal.UserAuthInfo;
import com.kindustry.common.util.BeanUtil;
import com.kindustry.common.util.StringUtil;
import com.kindustry.etl.constant.JobStatus;
import com.kindustry.etl.constant.RunPeriod;
import com.kindustry.etl.job.EtlJob;
import com.kindustry.etl.model.BufSche;
import com.kindustry.etl.model.BufferMetadata;
import com.kindustry.etl.model.JobMetadata;
import com.kindustry.etl.model.JobSche;
import com.kindustry.etl.model.SystemPara;
import com.kindustry.framework.jdbc.Condition;
import com.kindustry.framework.jdbc.Restrictions;
import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.scheduler.SchedulerManager;
import com.kindustry.framework.thread.ThreadPool;

/**
 * <p>
 * 测试工作，输出当前时间
 * </p>
 */
public class LoaderJobImpl implements EtlJob {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private JdbcTemplate jdbc = JdbcTemplate.getInstance();

  private ThreadPool threadPool = ThreadPool.getInstance();

  private Properties properties;

  public LoaderJobImpl() {
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
    SystemPara systemPara = jdbc.findOneByAnnotatedSample(new SystemPara());
    logger.debug("ETL Check System Status ETLDATE : {} , Step : {} ", systemPara.getSrcDt(), systemPara.getProcStep());
    return systemPara;
  }

  /**
   * 初始化跑批
   */
  public boolean init(final Date srcDt) {
    logger.debug("ETL INIT DATE: {} %tF %<tT%n", System.currentTimeMillis());
    // jdbc.loadExcelFile("etl","job_metadata", "D:/abc.xlsx"); // 初始数据入库

    BufferMetadata loadDelList = new BufferMetadata();
    loadDelList.setRuntype("T");
    // loadDelList.setTableName("CBOD_CMEMPEMP");

    // Del 入库文件列表 BUF
    final List<BufferMetadata> tabdellist = jdbc.findByAnnotatedSample(loadDelList);

    // 2. buf_sche 初始化设置 处理状态 PRE (PRE/WAITING/RUNNING/DONE/ERROR)
    List<BufSche> bufSches = new ArrayList<BufSche>();
    for (final BufferMetadata table : tabdellist) {
      BufSche bufSche = new BufSche();
      BeanUtil.copyProperties(table, bufSche); // 初始化
      // bufSche.setSid(UUIDGenerator.generate());
      bufSche.setSrcDt(new java.sql.Date(srcDt.getTime()));
      bufSche.setStatus(JobStatus.PRE.getValue());
      bufSches.add(bufSche);
    }

    // TODO
    // jdbc.truncateTable("SCHE", "BUF_SCHE"); // 清空 缓冲层调度登记表
    jdbc.execute("delete from sche.BUF_SCHE where src_dt = ? ", srcDt);
    // jdbc.truncateTable("ETL", "BUF_SCHE"); // 清空 缓冲层调度登记表

    boolean flag = jdbc.saveAnnotatedBean(bufSches);
    if (!flag) {
      // TODO 缓冲层 初始化失败 异常情况
      logger.error("ETL init Buf job Failed ! DATE: {}", srcDt);
    }

    // 初始化　作业层
    jdbc.truncateTable("SCHE", "JOB_SCHE"); // 清空 基础层作业调度表

    // 需要执行的 JOB
    JobMetadata jobSample = new JobMetadata();
    jobSample.setRunType("T");
    jobSample.setRunDt(RunPeriod.DAY.getValue()); // 每天执行的

    List<JobMetadata> jobMetadatas = jdbc.findByAnnotatedSample(jobSample);

    List<JobSche> jobSches = new ArrayList<JobSche>();
    for (JobMetadata item : jobMetadatas) {
      JobSche jobSche = new JobSche();
      BeanUtil.copyProperties(item, jobSche); // 初始化
      // jobSche.setSid(UUIDGenerator.generate());
      jobSche.setSrcDt(new java.sql.Date(srcDt.getTime()));
      jobSche.setStatus(JobStatus.PRE.getValue());
      jobSches.add(jobSche);
    }
     
    //TODO JOB 作业 暂时不测
//    flag = jdbc.saveAnnotatedBean(jobSches);

    // 任务调度管理器
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
   * ETL 批量处理
   * 
   */
  public synchronized void work() {
    // String backDir = properties.getProperty("BACKDIR").trim() + "/"; // 备份目录
    SystemPara systemPara = checkSystemStatus();

    Date srcDt = systemPara.getSrcDt();

    // 如果处理到最新 退出 while
    if (DateUtil.isBefore(srcDt, DateUtil.getCurrentShortDate())) { // 检查是否处理成功
      if (JobStatus.WAITING.getValue().equals(systemPara.getProcStep())) {
        // 初始化跑批作业
        init(srcDt);
      }
      //      
      // //重新获取日期
      // srcDt = checkSystemStatus().getSrcDt();
    }

    while (true) {
      // 休息1小时
      try {
        ThreadPoolExecutor threadPoolExecutor = threadPool.getThreadPoolExecutor();
        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
        System.out.println(threadPoolExecutor.getActiveCount() + "  " + threadPoolExecutor.getTaskCount() + " " + queue.size());
        Iterator<Runnable> it = queue.iterator();
        while (it.hasNext()) {
          Runnable runnable = it.next();
          // 线程执行状态
          State state = new Thread(runnable).getState();
          switch (state) {
            case NEW: // 新增加的
              break;
            case RUNNABLE:
              System.out.println("RUNNABLE");
              break;
            case BLOCKED:
              System.out.println("BLOCKED");
              break;
            case WAITING:
              System.out.println("WAITING");
              break;
            case TIMED_WAITING:
              System.out.println("TIMED_WAITING");
              break;
            case TERMINATED: // 线程执行完成
              System.out.println("TERMINATED");
              break;
          }
        }

        Thread.sleep(1000 * 60 * 1); // 休息1小时
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /***
   * ETL 数据导入处理　
   * 
   */
  public void loadWork(final Date curEtlDate) {
    // 读取 系统处理日期 ETL.SYSTEMPARA 表
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
    List<BufSche> tabdellist = jdbc.findByAnnotatedSample(preBufSche, preRestrictions);

    while (tabdellist.size() > 0) {
      // 开始调度 BUF
      for (final BufSche table : tabdellist) {
        String delname = table.getDelName();
        List<BufSche> receivedList = new ArrayList<BufSche>();

        UserAuthInfo userAuthinfo = new UserAuthInfo("32.137.32.79", 21, "sjxf", "sjxf");
        String sourceDay = new SimpleDateFormat("yyyyMMdd").format(curEtlDate); // new Date()为获取当前系统时间
        String dir = "/home/sjxf/odsdata/" + sourceDay + "/";
        List<String> allfiles = FtpZilla.listDirFiles(userAuthinfo, dir);
        
        // 2 TODO 监控 下发文件 FTP下载文件
        if (fileDataCheck(allfiles, curEtlDate, delname)) {
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
          boolean updateFlag = jdbc.updateAnnotatedEntity(bufSche, restrictions);
          // logger.debug("Update BUF_SCHE Set P_STATUS='WAITING' where SRC_DT={} AND SCHEMATA={} AND STBNAME={}" , updateFlag);
        } else {
          // 下发DEL 文件未到
          System.out.println("cannot find table : " + delname + " file ");
        }

        // 开启处理线程
        for (final BufSche received : receivedList) {
          Thread bufloadThread = new Thread(received.getTableName()) {
            public void run() {
              try {
                long threadId = Thread.currentThread().getId();
                String threadName = Thread.currentThread().getName();
                received.setSid(threadId + "-" + threadName);
                loadBuffData(received, receFileDir, curEtlDate);
              } catch (Exception e) {
                e.printStackTrace();
              } finally {

              }
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

      tabdellist = jdbc.findByAnnotatedSample(preBufSche, preRestrictions);
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
    List<JobSche> jobList = jdbc.findByAnnotatedSample(unDoneJobSche, preRestrictions);

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

          boolean updateFlag = jdbc.updateAnnotatedEntity(jobSche, restrictions);
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
              // TODO 执行 存储过程 或 其它 JOB
              // runJobSche(jobSche.getJobName(),jobSche.getJobCmd(), curEtlDate);
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

      // TODO 检测　PRE_JOB 是否完成 若已完成　设置 JOB　状态为　WAITING

      jobList = jdbc.findByAnnotatedSample(unDoneJobSche, preRestrictions);
    }

    // TODO JOB 　1.跑批完成检测　　2.处理结束 切换到下一天 　

  }

  /***
   * LOAD BUFF 加载缓冲层 表数据
   * 
   */
  public void loadBuffData(BufSche table, String dir, Date curEtlDate) {
    String srcTabName = table.getTableName();
    String schema = table.getSchema();
    String delname = table.getDelName();
    Integer split = table.getSplit();
    String destTabName = table.getDestTabName();  // 基础层表名
    String processMode  = table.getProcessMode();  // 处理方式

    // 数据导入 分割符号 0X1D 29 dataSplit
    char dataSplit = (char) split.intValue();

    String backDir = properties.getProperty("BACKDIR").trim() + "/"; // 备份目录
    String fileDate = DateUtil.formatDate(curEtlDate, DateStyle.YYYYMMDD); // 文件时间名
    // 下发文件
    String markerfile = dir + delname + "_" + fileDate + ".OK";
    String sqlfile = dir + delname + "_" + fileDate + ".SQL";
    // String delfile = dir + delname + "_" + fileDate + ".del";

    // 设置处理 状态为 PROCESSING
    BufSche bufSche = new BufSche();
    bufSche.setSid(table.getSid()); // 设置线程号
    bufSche.setStatus(JobStatus.PROCESSING.getValue());
    bufSche.setStartTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
    Restrictions restrictions = new Restrictions();
    restrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
    restrictions.addCondition(Condition.Equal, "SCHEMATA", schema);
    restrictions.addCondition(Condition.Equal, "STBNAME", srcTabName);

    boolean updateFlag = jdbc.updateAnnotatedEntity(bufSche, restrictions);
    if (!updateFlag) {
      logger.error("update ETL Step : LOAD [" + schema + "." + srcTabName + "] status [PROCESSING]  ERROR !");
      return;
    } else {
      logger.debug("update ETL Step : LOAD [" + schema + "." + srcTabName + "] status [PROCESSING]  Success !");
    }

    // 1. DDLFile 建表语句 createTableDDL
    StringBuffer tableddl = new StringBuffer();

    BufferedReader reader = null;
    try {

      // 2 TODO 监控 下发文件 FTP下载文件
      reader = new BufferedReader(new InputStreamReader(downloadSqlStream(curEtlDate, delname)));
      String tempString = null;
      // 显示行号
      int lineNumber = 1;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        if (StringUtil.isEmpty(tempString)) {
          continue;
        }
        tempString = tempString.trim();

        // 解决建表语句 主键为空的 PRIMARY KEY() 的问题
        if (tempString.startsWith("PRIMARY KEY(")) {
          tableddl.replace(tableddl.lastIndexOf(","), tableddl.lastIndexOf(",") + 1, ")");
          continue;
        }

        // 首行 替换 数据库表名
        if (lineNumber == 1) {
          String[] arrayStr = tempString.split(" ");
          StringBuffer tableStr = new StringBuffer();
          for (int i = 0; i < arrayStr.length; i++) {
            String tmpStr = arrayStr[i];
            if (i == 2) {
              // 替换 数据库表名
              tmpStr = schema + "." + srcTabName;
            }
            tableStr.append(tmpStr + " ");
          }
          tempString = tableStr.toString();
        }

        tableddl.append(tempString + "\r\n");
        // tableddl.append(tempString);
        lineNumber++;
      }

      // 注意 数据源
      switch (jdbc.getDBProductType()) {
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
        case MySQL:
          break;
        default:
          break;
      }
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
    if (jdbc.isTableExits(schema, srcTabName)) {
      // 表结构改变的话 表删除重建
      if (jdbc.dropTable(schema, srcTabName)) {
        jdbc.execute(tableddl.toString());
      }
    } else {
      jdbc.execute(tableddl.toString());
    }

    // 数据导入
    if (jdbc.isTableExits(schema, srcTabName)) {
      // if (FileUtil.isFileExits(delfile)) {

      InputStream in = downloadDelStream(curEtlDate, delname);

      // 清空表
      if (jdbc.truncateTable(schema, srcTabName)) {
        if (jdbc.loadDelStream(schema, srcTabName, in, dataSplit, (char) 0, 0)) {
          // 导入 数据成功
          logger.info("load [" + schema + "." + srcTabName + "] success !");
          
          //TODO 处理 数据入 基础层
          if(processMode.equals("M")){
            // 变量
             jdbc.mergeTable(schema, srcTabName, schema , destTabName) ;
          }
          
          
          
          // 设置处理 状态为 DONE
          bufSche.setStatus(JobStatus.DONE.getValue());

//          // 更改JOB 作业状态为 WAITING　存储过程
//          JobSche jobSche = new JobSche();
//          jobSche.setStatus(JobStatus.WAITING.getValue());
//          Restrictions jobRestrictions = new Restrictions();
//          jobRestrictions.addCondition(Condition.Equal, "SRC_DT", curEtlDate);
//          jobRestrictions.addCondition(Condition.Equal, "JOB_NM", table.getJobName());
//
//          boolean jobUpdateFlag = jdbc.updateAnnotatedEntity(jobSche, jobRestrictions);
//
//          if (!jobUpdateFlag) {
//            logger.error("update ETL Step : JOB [" + schema + "." + table.getJobName() + "] status [WAITING]  ERROR !");
//          }


        } else {
          logger.error("load [" + schema + "." + srcTabName + "] Fail !");
          // del 数据文件不存在 设置处理 状态为 ERROR
          bufSche.setStatus(JobStatus.ERROR.getValue());
        }
      } else {
        logger.error("truncate table [" + schema + "." + srcTabName + "] Fail !");
        // del 数据文件不存在 设置处理 状态为 ERROR
        bufSche.setStatus(JobStatus.ERROR.getValue());
      }
      // } else {
      // logger.info("table [" + schema + "." + tablename + "] DEL File not exits !");
      // // del 数据文件不存在 设置处理 状态为 ERROR
      // bufSche.setStatus(JobStatus.ERROR.getValue());
      // }
    } else {
      logger.info("table [" + schema + "." + srcTabName + "] not exits !");
      // 目标表不存在 设置处理 状态为 ERROR
      bufSche.setStatus(JobStatus.ERROR.getValue());
    }

    // 设置处理状态
    bufSche.setEndTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
    updateFlag = jdbc.updateAnnotatedEntity(bufSche, restrictions);
    if (!updateFlag) {
      logger.error("update table [" + bufSche.getSchema() + "." + bufSche.getTableName() + "]  at  etltable : [" + schema + "  " + srcTabName + "] stats [" + bufSche.getStatus()
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
    // List result = jdbc.callProcedure( "EDW.SP_F_CHN_SJ_FCHARGE_REC", new String[] { "2015-12-28" }, Types.INTEGER);
    List result = jdbc.callProcedure(jobName, new Object[] { date }, Types.INTEGER);

    // 设置处理 状态为 DONE
    JobSche jobSche = new JobSche();
    jobSche.setEndTime(new java.sql.Timestamp(DateUtil.getCurrentDateTime().getTime()));
    Restrictions restrictions = new Restrictions();
    restrictions.addCondition(Condition.Equal, "SRC_DT", date);
    restrictions.addCondition(Condition.Equal, "JOB_NM", jobName);

    if (result == null) { // 存储过程 不存在 或 执行 存储过程失败
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

    boolean updateFlag = jdbc.updateAnnotatedEntity(jobSche, restrictions);
    if (!updateFlag) {
      logger.error("update ETL Step : JOB [" + jobName + "] status [" + jobSche.getStatus() + "]  ERROR !");
      return;
    } else {
      logger.debug("update ETL Step : JOB [" + jobName + "] status [" + jobSche.getStatus() + "]  Success !");
    }

  }

  /**
   * 下载ODS文件
   */
  public InputStream downloadDelStream(Date etlDate, String delname) {
    InputStream is = null;
    UserAuthInfo userAuthinfo = new UserAuthInfo("32.137.32.79", 21, "sjxf", "sjxf");
    String sourceDay = new SimpleDateFormat("yyyyMMdd").format(etlDate); // new Date()为获取当前系统时间
    String dir = "/home/sjxf/odsdata/" + sourceDay + "/";

    String delfile = dir + delname + "_" + sourceDay + ".del";

    is = FtpZilla.retrieveFileStream(userAuthinfo, delfile);
    
    return is;
  }

  /**
   * 下载ODS文件
   */
  public InputStream downloadSqlStream(Date etlDate, String delname) {
    InputStream is = null;
    UserAuthInfo userAuthinfo = new UserAuthInfo("32.137.32.79", 21, "sjxf", "sjxf");
    String sourceDay = new SimpleDateFormat("yyyyMMdd").format(etlDate); // new Date()为获取当前系统时间
    String dir = "/home/sjxf/odsdata/" + sourceDay + "/";

    String sqlfile = dir + delname + "_" + sourceDay + ".SQL";

    is = FtpZilla.retrieveFileStream(userAuthinfo, sqlfile);
    
    return is;
  }

  /**
   * 检查是否文件齐全
   */
  public boolean fileDataCheck(List<String> allfiles , Date etlDate, String delname) {
    String sourceDay = new SimpleDateFormat("yyyyMMdd").format(etlDate); // new Date()为获取当前系统时间

    // 下发成功标识文件
    String markerfile = delname + "_" + sourceDay + ".OK";
    String sqlfile =  delname + "_" + sourceDay + ".SQL";
    String delfile =  delname + "_" + sourceDay + ".del";

    return allfiles.contains(markerfile) && allfiles.contains(sqlfile) && allfiles.contains(delfile) ;
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
  public void syncDataFile() {
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

}