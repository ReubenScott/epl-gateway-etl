package com.kindustry.etl.model;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * EDW源系统文件列表
 */
@Table(schema = "ETL", name = "BUF_METADATA")
public class BufferMetadata { //  

  @Column(name = "syscode")
  private String syscode; // char(4) 源系统代码

  @Column(name = "SCHEMATA")
  private String schema; // varchar(15)源系统表SCHEMA    TABSCHEMA

  @Column(name = "STBNAME")
  private String tableName; // varchar(100)源系统英文表名

  @Column(name = "DestTabName")
  private String destTabName; // varchar(10) 目标表名 

  @Column(name = "process_mode")
  private String processMode; // String(11)  处理方式  全量   变量  增量

  @Column(name = "STB_NAME_CN")
  private String tableComment; // varchar(200)源系统中文表名
  
  @Column(name = "DELNAME")
  private String delName; // varchar(100) 源系统文件全名

  @Column(name = "SPLIT")
  private Integer split; // char(4) 分隔符    0x1d  , 
  
  @Column(name = "RUNTYPE")
  private String runtype; // varchar(2) 运行标志(T是F否)
  
  @Column(name = "RUN_DT")
  private String runDate; // varchar(20) 作业运行时点类型。/X不跑/D每天/W每周/M每月/Y每年

  @Column(name = "TABSPS")
  private String tableSpace; // VARCHAR(30) 表空间

  @Column(name = "INDSPS")
  private String indexSpace; // VARCHAR(30) 索引空间

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


  public String getDestTabName() {
    return destTabName;
  }

  public void setDestTabName(String destTabName) {
    this.destTabName = destTabName;
  }

  public String getRunDate() {
    return runDate;
  }

  public void setRunDate(String runDate) {
    this.runDate = runDate;
  }

  public String getRuntype() {
    return runtype;
  }

  public void setRuntype(String runtype) {
    this.runtype = runtype;
  }

  public String getProcessMode() {
    return processMode;
  }

  public void setProcessMode(String processMode) {
    this.processMode = processMode;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

}