package com.soak.etl.model;

import com.soak.framework.orm.Column;
import com.soak.framework.orm.Table;

/**
 * 基础层作业元数据登记表
 */
//@Table(schema = "ETL", name = "JOB_METADATA", pk = { "sid" })
@Table(schema = "SCHE", name = "JOB_METADATA", pk = { "sid" })
public class JobMetadata {

  @Column(name = "SYSCODE")
  private String syscode; // varchar(10) 系统代号
  
  @Column(name = "JOB_NM")
  private String jobName; // int(11)  作业英文名称,START/END是两个自定义作业，代表调度的起点和终点

  @Column(name = "JOB_TYPE")
  private String jobType; // varchar(100) 作业类型(NODE/DS/SHDB/SHELL/SP)

  @Column(name = "RUN_DT")
  private String runDt; // varchar(200) 作业运行时点类型。/X不跑/D每天/W每周/M每月/Y每年/N月底/Z年底  

  @Column(name = "RUNTYPE")
  private String runType; // varchar(70) 运行标志(T是F否)

  @Column(name = "JOB_CMD")
  private String jobCmd; // char(4)  作业命令

  @Column(name = "JOB_PAR")
  private String jobParam; // 作业参数

  @Column(name = "JOB_PRIO")
  private String priority; // VARCHAR(30) 作业优先级，数字越大，优先级越高

  @Column(name = "RES_TYPE")
  private String resType; // varchar(15) 作业资源类型
  
  @Column(name = "JOB_WKRES")
  private String resourceConsumption; // char(4) 作业资源占用量，可调整、估算
  
  @Column(name = "JOB_CNM")
  private String jobCName; // date  作业中文名
  
  @Column(name = "REMARK")
  private String remark; //  备注信息

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

  public String getJobType() {
    return jobType;
  }

  public void setJobType(String jobType) {
    this.jobType = jobType;
  }

  public String getRunDt() {
    return runDt;
  }

  public void setRunDt(String runDt) {
    this.runDt = runDt;
  }

  public String getRunType() {
    return runType;
  }

  public void setRunType(String runType) {
    this.runType = runType;
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

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getResType() {
    return resType;
  }

  public void setResType(String resType) {
    this.resType = resType;
  }

  public String getResourceConsumption() {
    return resourceConsumption;
  }

  public void setResourceConsumption(String resourceConsumption) {
    this.resourceConsumption = resourceConsumption;
  }

  public String getJobCName() {
    return jobCName;
  }

  public void setJobCName(String jobCName) {
    this.jobCName = jobCName;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
  
  
  
  

}