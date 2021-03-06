package com.kindustry.etl.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 缓冲层调度登记表
 */
@Table(schema = "ETL", name = "buf_sche")
public class BufSche {

  @Column(name = "SID")
  private String sid; // int(11) 作业序列号

  @Column(name = "SRC_DT")
  private Date srcDt; // date 源系统数据日期

  @Column(name = "SYSCODE")
  private String syscode; // char(4) 源系统代码

  @Column(name = "SCHEMATA")
  private String schema; // varchar(15) 源系统表SCHEMA

  @Column(name = "STBNAME")
  private String tableName; // varchar(70) 源系统英文表名

  @Column(name = "DestTabName")
  private String destTabName; // varchar(10) 目标表名

  @Column(name = "process_mode")
  private String processMode; // String(11)  处理方式  全量   变量  增量

  @Column(name = "STB_NAME_CN")
  private String tableComment; // varchar(200)源系统中文表名

  @Column(name = "DELNAME")
  private String delName; // varchar(100)源系统带后缀文本名

  @Column(name = "SPLIT")
  private Integer split; // char(4) 分隔符     ox1d : 29  ;  , : 44

  @Column(name = "P_STATUS")
  private String status; // varchar(20)处理状态(WAITING/PROPRESS/DONE/EDDLERROR/XSQLERROR/LOADERROR)

  @Column(name = "BEGIN_TM")
  private Timestamp startTime; // time处理运行开始时间

  @Column(name = "END_TM")
  private Timestamp endTime; // time处理运行结束时间

  @Column(name = "TABSPS")
  private String tableSpace; // VARCHAR(30) 表空间

  @Column(name = "INDSPS")
  private String indexSpace; // VARCHAR(30) 索引空间
  
  public String getSid() {
    return sid;
  }

  public void setSid(String sid) {
    this.sid = sid;
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

  public String getSyscode() {
    return syscode;
  }

  public void setSyscode(String syscode) {
    this.syscode = syscode;
  }

  public String getProcessMode() {
    return processMode;
  }

  public void setProcessMode(String processMode) {
    this.processMode = processMode;
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

  public String getDestTabName() {
    return destTabName;
  }

  public void setDestTabName(String destTabName) {
    this.destTabName = destTabName;
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

}