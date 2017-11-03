package com.kindustry.etl.constant;


public enum LoadMode {

  REPLACE { 
    @Override
    public String getValue() {
      return "R";
    }
    
    @Override
    public String getComment() {
      return "全量加载";
    }
  }, 
  MERGE { 
    @Override
    public String getValue() {
      return "M";
    }
    
    @Override
    public String getComment() {
      return "变量加载";
    }
  },
  INSERT {
    @Override
    public String getValue() {
      return "I";
    }
    
    @Override
    public String getComment() {
      return "增量加载";
    }
  };

  public abstract String getComment();

  public abstract String getValue();


  public static LoadMode getStatus(String statusType) {
    for (LoadMode ctype : values()) {
      if (ctype.getValue().equals(statusType)) {
        return ctype;
      }
    }
    return null;
  }


}
