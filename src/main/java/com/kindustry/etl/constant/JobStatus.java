package com.kindustry.etl.constant;


/**
 * 
 * 作业状态
 */
public enum JobStatus {
  
  PRE { 
    @Override
    public String getName() {
      return "初始化";
    }

    @Override
    public String getValue() {
      return "PRE";
    }
  }, 
  WAITING { 
    @Override
    public String getName() {
      return "等待";
    }

    @Override
    public String getValue() {
      return "WAITING";
    }
  },
  PROCESSING {
    @Override
    public String getName() {
      return "正在处理中";
    }

    @Override
    public String getValue() {
      return "PROCESSING";
    }
  },
  DONE { 
    @Override
    public String getName() {
      return "处理完成";
    }

    @Override
    public String getValue() {
      return "DONE";
    }
  },
  ERROR { 
    @Override
    public String getName() {
      return "处理失败";
    }

    @Override
    public String getValue() {
      return "ERROR";
    }
  };

  public abstract String getName();

  public abstract String getValue();


  public static JobStatus getStatus(String statusType) {
    for (JobStatus ctype : values()) {
      if (ctype.getValue().equals(statusType)) {
        return ctype;
      }
    }
    return null;
  }

}