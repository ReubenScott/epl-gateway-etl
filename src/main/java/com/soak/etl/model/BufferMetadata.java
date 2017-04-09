package com.soak.etl.model;

import com.soak.jdbcframe.orm.Column;
import com.soak.jdbcframe.orm.Table;


/**
 * EDW源系统文件列表
 */
//@Table(schema="ETL" , name = "BUF_METADATA", pk = { "scheduleid" })
@Table(schema="SCHE" , name = "BUF_METADATA", pk = { "scheduleid" })
public class BufferMetadata {  //  

  @Column(name = "syscode")
  private String syscode; // char(4) 源系统代码

  @Column(name = "SCHEMATA")
  private String schema; // varchar(15)源系统表SCHEMA

  @Column(name = "STBNAME")
  private String tableName; // varchar(100)源系统英文表名

  @Column(name = "STB_NAME_CN")
  private String tableComment; // varchar(200)源系统中文表名

  @Column(name = "DELNAME")
  private String delName; // varchar(100) 源系统文件全名

  @Column(name = "SPLIT")
  private Integer split; // char(4)  分隔符
  
  @Column(name = "TABSPS")
  private String tableSpace; //  VARCHAR(30) 表空间
   
  @Column(name = "INDSPS")
  private String indexSpace; //  VARCHAR(30) 索引空间

  @Column(name = "JOB_NM")
  private String jobName; // varchar(10)  下一步JOB: 存储过程名

//  @Column(name = "RUN_DT")
  private String rundt; // varchar(20) 作业运行时点类型。/X不跑/D每天/W每周/M每月/Y每年

  @Column(name = "RUNTYPE")
  private String runtype; // varchar(2) 运行标志(T是F否)

  @Column(name = "P_PRIO")
  private Integer priority; // int(11)处理优先级,数字越小， 优先级越高越先处理

  @Column(name = "P_WKRES")
  private Integer resourceConsumption; // int(11)处理资源占用量，可调整、估算

  @Column(name = "REMARK") 
  private String remark; // varchar(256) 备注

  public String getSyscode() {
    return syscode;
  }

  public void setSyscode(String syscode) {
    this.syscode = syscode;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableComment() {
    return tableComment;
  }

  public void setTableComment(String tableComment) {
    this.tableComment = tableComment;
  }

  public String getDelName() {
    return delName;
  }

  public void setDelName(String delName) {
    this.delName = delName;
  }

  public Integer getSplit() {
    return split;
  }

  public void setSplit(Integer split) {
    this.split = split;
  }
  

  public String getTableSpace() {
    return tableSpace;
  }

  public void setTableSpace(String tableSpace) {
    this.tableSpace = tableSpace;
  }

  public String getIndexSpace() {
    return indexSpace;
  }

  public void setIndexSpace(String indexSpace) {
    this.indexSpace = indexSpace;
  }


  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getRundt() {
    return rundt;
  }

  public void setRundt(String rundt) {
    this.rundt = rundt;
  }

  public String getRuntype() {
    return runtype;
  }

  public void setRuntype(String runtype) {
    this.runtype = runtype;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Integer getResourceConsumption() {
    return resourceConsumption;
  }

  public void setResourceConsumption(Integer resourceConsumption) {
    this.resourceConsumption = resourceConsumption;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

}