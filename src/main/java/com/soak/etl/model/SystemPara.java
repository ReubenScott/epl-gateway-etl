package com.soak.etl.model;

import java.sql.Date;

import com.soak.jdbcframe.orm.Column;
import com.soak.jdbcframe.orm.Table;

/**
 * 
 * Comment on Table ETL.SYSTEMPARA is '系统参数表';
 */
//@Table(schema = "ETL", name = "SYSTEMPARA")
@Table(schema = "SCHE", name = "SYSTEMPARA")
public class SystemPara {

  @Column(name = "CURDATE")
  private Date srcDt; // date '当前处理日期';

  @Column(name = "PROCSTEP")
  private String procStep; // VARCHAR(10) 所处阶段(WAITING/PROCESSING/PAUSE) ,

  @Column(name = "LOGDAYS")
  private Integer logDays; // INTEGER 调度日志保存天数

  public Date getSrcDt() {
    return srcDt;
  }

  public void setSrcDt(Date srcDt) {
    this.srcDt = srcDt;
  }

  public String getProcStep() {
    return procStep;
  }

  public void setProcStep(String procStep) {
    this.procStep = procStep;
  }

  public Integer getLogDays() {
    return logDays;
  }

  public void setLogDays(Integer logDays) {
    this.logDays = logDays;
  }

}