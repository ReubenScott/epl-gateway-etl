package com.soak.etl.constant;

/**
 * 
 * 运行时间 RUN_DT
 */
public enum RunPeriod {

  DAY {
    @Override
    public String getName() {
      return "每天";
    }

    @Override
    public String getValue() {
      return "D";
    }
  },
  WEEK {
    @Override
    public String getName() {
      return "每周";
    }

    @Override
    public String getValue() {
      return "W";
    }
  },
  TENDAYS {
    @Override
    public String getName() {
      return "每旬";
    }

    @Override
    public String getValue() {
      return "T";
    }
  },
  MONTH {
    @Override
    public String getName() {
      return "每月";
    }

    @Override
    public String getValue() {
      return "M";
    }
  },
  QUARTER {
    @Override
    public String getName() {
      return "每季";
    }

    @Override
    public String getValue() {
      return "Q";
    }
  },
  YEAR {
    @Override
    public String getName() {
      return "每年";
    }

    @Override
    public String getValue() {
      return "Y";
    }
  };

  public abstract String getName();

  public abstract String getValue();

  public static RunPeriod getPeriod(String statusType) {
    for (RunPeriod period : values()) {
      if (period.getValue().equals(statusType)) {
        return period;
      }
    }
    return null;
  }

}