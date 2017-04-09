package com.soak.etl.model;

import java.sql.Date;
import java.sql.Timestamp;

import com.soak.jdbcframe.orm.Column;
import com.soak.jdbcframe.orm.Table;


/**
 * 基础层作业调度表
 */
//@Table(schema = "ETL", name = "job_sche", pk = { "sid" })
@Table(schema = "SCHE", name = "job_sche", pk = { "sid" })
public class JobSche {

  @Column(name = "SYSCODE")
  private String syscode; // char(4) 源系统代码

  @Column(name = "JOB_NM")
  private String jobName; // varchar(10) JOB名称

  @Column(name = "SRC_DT")
  private Date srcDt; // date EDW应用处理数据日期

  // @Column(name = "SCHEMATA")
  private String schema; // varchar(15) 源系统表SCHEMA

  @Column(name = "JOB_CMD")
  private String jobCmd; // char(4) 作业命令

  @Column(name = "JOB_PAR")
  private String jobParam; // 作业参数

  @Column(name = "JOB_PRIO")
  private Integer priority; // int(11)处理优先级,数字越小， 优先级越高越先处理

  @Column(name = "JOB_STATUS")
  private String status; // varchar(20)处理状态(WAITING/PROPRESS/DONE/EDDLERROR/XSQLERROR/LOADERROR)

  @Column(name = "BEGIN_TM")
  private Timestamp startTime; // time处理运行开始时间

  @Column(name = "END_TM")
  private Timestamp endTime; // time处理运行结束时间

  public String getSyscode() {
    return syscode;
  }

  public void setSyscode(String syscode) {
    this.syscode = syscode;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public Date getSrcDt() {
    return srcDt;
  }

  public void setSrcDt(Date srcDt) {
    this.srcDt = srcDt;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
  }

  public Timestamp getEndTime() {
    return endTime;
  }

  public void setEndTime(Timestamp endTime) {
    this.endTime = endTime;
  }

  public String getJobCmd() {
    return jobCmd;
  }

  public void setJobCmd(String jobCmd) {
    this.jobCmd = jobCmd;
  }

  public String getJobParam() {
    return jobParam;
  }

  public void setJobParam(String jobParam) {
    this.jobParam = jobParam;
  }

}