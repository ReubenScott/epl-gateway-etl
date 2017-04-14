/**
 * 系统参数表
 */
Create table ETL.SYSTEMPARA (
    CURDATE                        DATE                NOT NULL    ,
    PROCSTEP                       VARCHAR(10)         Default 'WAITING'  ,
    BUFDAYS                        INTEGER             Default 1   ,
    IPDDAYS                        INTEGER             Default 7   ,
    SUMDAYS                        INTEGER             Default 30  ,
    RPTDAYS                        INTEGER             Default 30  ,
    LOGDAYS                        INTEGER             Default 7  
) ;    

Comment on Table  ETL.SYSTEMPARA             is '系统参数表' ;

/**
 * 系统参数表
 */
CREATE TABLE ETL.BUF_METADATA (
   SYSCODE char(4) default NULL ,
   SCHEMATA varchar(15) NOT NULL ,
   STBNAME varchar(100) NOT NULL ,
   STB_NAME_CN varchar(200) default NULL ,
   DELNAME varchar(100) default NULL ,
   SPLIT char(4) default NULL ,
   JOB_NM varchar(30) default NULL ,
   RUNTYPE varchar(2) default NULL ,
   RUN_DT varchar(20) default NULL ,
   PTYPE varchar(10) default NULL ,
   P_PRIO integer default '1000' ,
   P_WKRES integer default '3' ,
   TABSPS varchar(30) default NULL ,
   INDSPS varchar(30) default NULL ,
   REMARK varchar(256) default NULL ,
   PRIMARY KEY(SCHEMATA,STBNAME)
 ) ;

COMMENT ON TABLE etl.buf_metadata  IS 'EDW源系统文件列表';


--------------------------------------------------
-- 缓冲层调度登记表
--------------------------------------------------
Create table SCHE.BUF_SCHE (
    SRC_DT           DATE                NOT NULL    ,
    SYSCODE          CHAR(4)                         ,
    SCHEMATA         VARCHAR(15)         NOT NULL    ,
    STBNAME          VARCHAR(70)         NOT NULL    ,
    STB_NAME_CN      VARCHAR(200)                    ,
    DELNAME          VARCHAR(100)                    ,
    SPLIT            CHAR(4)                         ,
    JOB_NM           VARCHAR(30)                     ,
    P_PRIO           INTEGER                         ,
    P_WKRES          INTEGER                         ,
    P_STATUS         VARCHAR(20)                     ,
    BEGIN_TM         TIMESTAMP                       ,
    END_TM           TIMESTAMP                       ,
    TABSPS           VARCHAR(30)                     ,
    INDSPS           VARCHAR(30)                     ,
    PRIMARY KEY (SRC_DT,SCHEMATA,STBNAME)  ) ;

Comment on Table SCHE.BUF_SCHE                    is '缓冲层调度登记表';
Comment on Column SCHE.BUF_SCHE.SRC_DT            is 'EDW应用处理数据日期';
Comment on Column SCHE.BUF_SCHE.SYSCODE           is '源系统代码';
Comment on Column SCHE.BUF_SCHE.SCHEMATA          is '源系统表SCHEMA';
Comment on Column SCHE.BUF_SCHE.STBNAME           is '源系统英文表名';
Comment on Column SCHE.BUF_SCHE.DELNAME           is '源系统带后缀文本名';
Comment on Column SCHE.BUF_SCHE.STB_NAME_CN       is '源系统中文表名';
Comment on Column SCHE.BUF_SCHE.JOB_NM            is '下一步JOB: 存储过程名';
Comment on Column SCHE.BUF_SCHE.P_PRIO            is '处理优先级，数字越大优先级越高';
Comment on Column SCHE.BUF_SCHE.P_WKRES           is '处理资源占用量，可调整、估算';
Comment on Column SCHE.BUF_SCHE.P_STATUS          is '处理状态(PRE/WAITING/PROPRESS/DONE/ERROR)';
Comment on Column SCHE.BUF_SCHE.BEGIN_TM          is '处理运行开始时间';
Comment on Column SCHE.BUF_SCHE.END_TM            is '处理运行结束时间';
 


--------------------------------------------------
-- 作业元数据登记表
--------------------------------------------------
Create table SCHE.JOB_METADATA (
    SYSCODE          CHAR(4)             NOT NULL    ,
    JOB_NM           VARCHAR(128)        NOT NULL    ,
    JOB_CNM          VARCHAR(128)        ,
    JOB_TYPE         VARCHAR(8)           ,
    RES_TYPE         VARCHAR(20)         ,
    RUNTYPE          VARCHAR(1)          ,
    RUN_DT           VARCHAR(20)         ,
    JOB_CMD          VARCHAR(200)        ,
    JOB_PAR          VARCHAR(200)        ,
    JOB_PRIO         INTEGER              ,
    JOB_WKRES        INTEGER               ,
    REMARK           VARCHAR(256)        ,
    PRIMARY KEY(SYSCODE,JOB_NM) 
) ;


Comment on Table ETL.JOB_METADATA is '基础层作业元数据登记表';
Comment on Column ETL.JOB_METADATA.JOB_NM        is '作业英文名称,START/END是两个自定义作业，代表调度的起点和终点';
Comment on Column ETL.JOB_METADATA.JOB_CNM       is '作业中文名';
Comment on Column ETL.JOB_METADATA.RES_TYPE      is '作业资源类型';
Comment on Column ETL.JOB_METADATA.RUNTYPE       is '作业运行时点类型。/D每天/W每周/T每旬/M每月/Q每季/Y每年';
Comment on Column ETL.JOB_METADATA.RUN_DT        is '作业运行时点。同JOB_RUN_TYPE共同作用';
Comment on Column ETL.JOB_METADATA.JOB_TYPE      is '作业类型(NODE/DS/SHDB/SHELL/SP)';
Comment on Column ETL.JOB_METADATA.JOB_CMD       is '作业命令';
Comment on Column ETL.JOB_METADATA.JOB_PAR       is '作业参数';
Comment on Column ETL.JOB_METADATA.JOB_PRIO      is '作业优先级，数字越大，优先级越高';
Comment on Column ETL.JOB_METADATA.JOB_WKRES     is '作业资源占用量，可调整、估算';
Comment on Column ETL.JOB_METADATA.SYSCODE       is '系统代号';
Comment on Column ETL.JOB_METADATA.REMARK        is '备注信息';

--------------------------------------------------
-- Create Table ETL.JOB_SCHE
--------------------------------------------------
Create table SCHE.JOB_SCHE (
    SID                            VARCHAR(32)         NOT NULL    ,
    SRC_DT                         DATE                NOT NULL    ,
    SYSCODE                        CHAR(4)                       ,
    JOB_NM                         VARCHAR(128)        NOT NULL   ,    
    JOB_STATUS                     VARCHAR(8)                      ,
    JOB_PRIO                       INTEGER             NOT NULL    ,
    DELFILE                        VARCHAR(50)                    ,
    BEGIN_TM                       TIMESTAMP                       ,
    END_TM                         TIMESTAMP                       
   ) ;

Comment on Table SCHE.JOB_SCHE is '基础层作业调度表';
Comment on Column SCHE.JOB_SCHE.SID        is '作业序号';
Comment on Column SCHE.JOB_SCHE.JOB_NM            is '作业英文名称';
Comment on Column SCHE.JOB_SCHE.JOB_STATUS        is '作业状态(PRE/WAITING/RUNNING/DONE/ERROR)';
Comment on Column SCHE.JOB_SCHE.JOB_PRIO          is '作业优先级，数字越大，优先级越高';
Comment on Column SCHE.JOB_SCHE.SRC_DT            is '源系统数据日期';
Comment on Column SCHE.JOB_SCHE.SYSCODE           is '系统代号';
Comment on Column SCHE.JOB_SCHE.DELFILE           is '原文件名（DEL文件名）';
Comment on Column SCHE.JOB_SCHE.BEGIN_TM          is '作业运行开始时间';
Comment on Column SCHE.JOB_SCHE.END_TM            is '作业运行结束时间';

