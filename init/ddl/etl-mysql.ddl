
/**
 * JOB 日志表
 */
CREATE TABLE ETL.LOG_EXEC (
    PRCNAME                        VARCHAR(100)                  COMMENT '名称'     ,
    PRCTYPE                        VARCHAR(1)                    COMMENT '类型 0-存储过程 1-函数 2-DataStage 3-其它' ,
    TIMEID                         VARCHAR(20)                   COMMENT '统计日期'  ,
    TIMESTART                      TIMESTAMP                     COMMENT '程序开始运行时间'  ,
    TIMELOG                        TIMESTAMP                     COMMENT '日志记录时间'  ,
    RESULTLEVEL                    VARCHAR(1)                    COMMENT '告警级别：0-成功 1-异常 2-无影响异常 3-警告'   ,
    SSQLCODE                       INTEGER                       COMMENT 'SQLCODE返回码'  ,
    RECORDNUM                      INTEGER                       COMMENT '提交记录数'    ,
    SREMARK                        VARCHAR(500)                  COMMENT '日志说明'    
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT = '基础层作业调度表'  ;

/**
 * 系统参数表
 */
-- DROP TABLE ETL.SYSTEMPARA ;
Create table ETL.SYSTEMPARA (
    CURDATE                        DATE                NOT NULL  COMMENT '当前处理日期'   ,
    PROCSTEP                       VARCHAR(10)         Default 'WAITING' Comment '所处阶段(WAITING/PROCESSING/PAUSE)' ,
    BUFDAYS                        INTEGER             Default 1  Comment '缓冲层数据保存天数' ,
    IPDDAYS                        INTEGER             Default 7  Comment '基础层数据保存天数' ,
    SUMDAYS                        INTEGER             Default 30 Comment '汇总层数据保存天数' ,
    RPTDAYS                        INTEGER             Default 30 Comment '报表层数据保存天数' ,
    LOGDAYS                        INTEGER             Default 7  Comment '调度日志保存天数'
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='系统参数表' ;    


/**
 * 源系统登记表
 */
-- DROP TABLE ETL.SOURCESYS ;
CREATE TABLE ETL.SOURCESYS (
    SYSCODE                        CHAR(4)             NOT NULL   Default ' ' Comment '源系统代码' ,
    SYSENM                         VARCHAR(50)         NOT NULL   Default ' ' COMMENT '源系统英文名称' ,
    SYSCNM                         VARCHAR(100)        NOT NULL   Default ' ' COMMENT '源系统中文名称' ,
    INUSE                          VARCHAR(3)          NOT NULL   Default 'NO' COMMENT '源系统是否在用' ,
    PACKTYPE                       VARCHAR(5)          NOT NULL   Default 'TAR' COMMENT '源提供数方式' ,
    NOTE                           VARCHAR(50)                    Default ''  COMMENT '备注'
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='源系统登记表' ;    


/**
 * EDW源系统文件列表
 */
-- DROP TABLE ETL.BUF_METADATA ;
CREATE TABLE ETL.BUF_METADATA (
    SYSCODE         CHAR(4)                       COMMENT '源系统代码' ,
    SCHEMATA        VARCHAR(15)    NOT NULL       COMMENT '源系统表SCHEMA',
    STBNAME         VARCHAR(100)   NOT NULL       COMMENT '源系统英文表名' ,
    STB_NAME_CN     VARCHAR(200)                  COMMENT '源系统中文表名',
    DELNAME         VARCHAR(100)                  COMMENT '源系统文件全名',
    SPLIT           CHAR(4)                       COMMENT '分隔符'  ,
    JOB_NM          VARCHAR(30)                   COMMENT '存储过程名'  ,
    RUNTYPE         VARCHAR(1)                    COMMENT '运行标志(T是F否)' ,
    RUN_DT          VARCHAR(20)                   COMMENT '作业运行时点类型。/D每天/W每周/M每月/Y每年' ,
    PTYPE           VARCHAR(10)                   COMMENT '处理方式： 全量、增量、变量' ,
    P_PRIO          INTEGER        Default 1000   COMMENT '处理优先级,数字越小， 优先级越高越先处理' ,
    P_WKRES         INTEGER        Default 3      COMMENT '处理资源占用量，可调整、估算' ,
    TABSPS          VARCHAR(30)                   COMMENT '数据表空间'                 ,
    INDSPS          VARCHAR(30)                   COMMENT '索引表空间'                 ,
    REMARK          VARCHAR(256)                  COMMENT '备注'                 ,
    PRIMARY KEY(SCHEMATA,STBNAME) 
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT = 'EDW源系统文件列表'  ;

/**
 * 缓冲层调度登记表
 */
-- DROP TABLE ETL.BUF_SCHE ;
CREATE TABLE ETL.BUF_SCHE (
    SRC_DT             DATE             NOT NULL  COMMENT 'EDW应用处理数据日期' ,
    SYSCODE            CHAR (4)                   COMMENT '源系统代码' ,
    SCHEMATA         VARCHAR (15)     NOT NULL  COMMENT '源系统表SCHEMA' ,
    STBNAME            VARCHAR (70)     NOT NULL  COMMENT '源系统英文表名' ,
    STB_NAME_CN        VARCHAR (200)              COMMENT '源系统中文表名' ,
    DELNAME            VARCHAR (100)    NOT NULL  COMMENT '源系统带后缀文本名' ,
    SPLIT              CHAR (4)                   COMMENT '数据分隔符' ,
    JOB_NM             VARCHAR (30)               COMMENT '作业：存储过程名' ,
    P_PRIO             INTEGER                    COMMENT '处理优先级，数字越大优先级越高' ,
    P_WKRES            INTEGER                    COMMENT '处理资源占用量，可调整、估算' ,
    P_STATUS           VARCHAR (20)               COMMENT '处理状态(WAITING/PROPRESS/DONE/EDDLERROR/XSQLERROR/LOADERROR)' ,
    BEGIN_TM           TIMESTAMP                  COMMENT '处理运行开始时间'             ,
    END_TM             TIMESTAMP                  COMMENT '处理运行结束时间'             ,
    TABSPS             VARCHAR (30)               COMMENT '数据表空间' ,
    INDSPS             VARCHAR (30)               COMMENT '索引表空间' ,
    PRIMARY KEY (SRC_DT,SCHEMATA,STBNAME)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT = '缓冲层调度登记表' ;

--  初始化  样例
INSERT INTO edw_tabdellist 
( 
  SYSCODE ,STBNAME,STB_NAME_CN,DELNAME,SCHEMATA,RUNTYPE,RUN_DT,P_PRIO,P_WKRES
)
VALUES ("1001","MPS_FACCT_REG","关联账户信息表","P_063_MPS_FACCT_REG","EDW","T","D",1011,3)
  ,("1001","MPS_FCHARGE_REC","费流水表","P_063_MPS_FCHARGE_REC","EDW","T","D",1011,3)

/**
 * 作业配置表
 */
CREATE TABLE ETL.JOB_METADATA (
    SYSCODE     CHAR(4)         NOT NULL  Default 'EDW'  COMMENT '系统代号' ,
    JOB_NM      VARCHAR(128)    NOT NULL                 COMMENT '作业英文名称,START/END是两个自定义作业，代表调度的起点和终点' ,
    JOB_CNM     VARCHAR(128)                             COMMENT '作业中文名' ,
    JOB_TYPE    VARCHAR(8)                Default 'SP'   COMMENT '作业类型(NODE/DS/SHDB/SHELL/SP)'   ,
    RES_TYPE    VARCHAR(20)                              COMMENT '作业资源类型'   ,
    RUNTYPE     VARCHAR(1)                Default 'T'    COMMENT '运行标志(T是F否)' ,
    RUN_DT      VARCHAR(20)                              COMMENT '作业运行时点。/D每天/W每周/T每旬/M每月/每季/Y每年' ,
    JOB_CMD     VARCHAR(200)                             COMMENT '作业命令'  ,
    JOB_PAR     VARCHAR(200)                             COMMENT '作业参数' ,
    JOB_PRIO    INTEGER                   Default 1000   COMMENT '作业优先级，数字越大，优先级越高' ,
    JOB_WKRES   INTEGER                   Default 2      COMMENT '作业资源占用量，可调整、估算' ,
    REMARK      VARCHAR(256)                             COMMENT '备注信息' ,
    PRIMARY KEY(SYSCODE,JOB_NM)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT = '作业配置表' ;


/**
 * CREATE TABLE ETL.JOB_SEQ
 */
CREATE TABLE ETL.JOB_SEQ (
    JOB_NM                         VARCHAR(128)        NOT NULL COMMENT '作业英文名称'   ,
    PRE_JOB                        VARCHAR(128)        NOT NULL   Default ' ' COMMENT '前置作业名'
) COMMENT = '基础层作业依赖关系登记表' ENGINE=INNODB DEFAULT CHARSET=UTF8  ;


/**
 * CREATE TABLE ETL.JOB_SCHE
 */
CREATE TABLE ETL.JOB_SCHE (
    SRC_DT         DATE                NOT NULL                COMMENT '应用处理数据日期'  ,
    SYSCODE        CHAR(4)             NOT NULL   DEFAULT ''   COMMENT '系统代号' ,
    JOB_NM         VARCHAR(128)        NOT NULL   DEFAULT ''   COMMENT '作业英文名称' ,
    JOB_CMD        VARCHAR(200)                                COMMENT '作业命令'  ,
    JOB_PAR        VARCHAR(200)                                COMMENT '作业参数' ,
    JOB_STATUS     VARCHAR(10)         NULL                    COMMENT '作业状态(PRE/WAITING/RUNNING/DONE/ERROR)' ,
    JOB_PRIO       INTEGER             NULL                    COMMENT '作业优先级，数字越大，优先级越高'  ,
    BEGIN_TM       TIMESTAMP           NULL                    COMMENT '作业运行开始时间'          ,
    END_TM         TIMESTAMP           NULL                    COMMENT '作业运行结束时间'          ,
    PRIMARY KEY(SRC_DT,SYSCODE,JOB_NM)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT = '基础层作业调度表'  ;



/**
 * CREATE TABLE ETL.EDW_SS_STS
 */
CREATE TABLE ETL.EDW_SS_STS (
    SYSCODE                        CHAR(4)             NOT NULL   Default ' ' COMMENT '源系统代码' ,
    SYSENM                         VARCHAR(50)         NOT NULL   Default ' ' COMMENT '源系统英文简称' ,
    EDW_DATE                       DATE                NOT NULL   COMMENT 'EDW处理日期' ,
    PACKTYPE                       VARCHAR(5)          NOT NULL   Default ' ' COMMENT '源提供数据打包方式(DFT TAR COMP等)' ,
    PRC_STS                        VARCHAR(30)         NOT NULL   Default ' ' COMMENT '当前状态(WAITING/CHECKING/PRE/PROPRESS/DONE/ERROR/PERROR/UNTARERROR/NOTARERROR/HALFERROR等)' ,
    UNPACK_STS                     VARCHAR(3)          NOT NULL   Default ' ' COMMENT '如果是打包文件，则标解包状态'
) COMMENT = 'EDW源系统状态表' ENGINE=INNODB DEFAULT CHARSET=UTF8  ;


--------------------------------------------------
-- CREATE TABLE ETL.JOB_RES_CTRL
--------------------------------------------------
CREATE TABLE ETL.JOB_RES_CTRL (
    JOB_RES_TYPE                   VARCHAR(20)         NOT NULL    ,
    JOB_RES_NM                     VARCHAR(200)                   Default ''  ,
    JOB_RES_MAX                    INTEGER             NOT NULL    ,
    JOB_RES_IDLE                   INTEGER             NOT NULL    ,
    REMARK                         VARCHAR(256)                   Default ''  ) 
  ;
COMMENT on Table ETL.JOB_RES_CTRL is '作业资源控制登记表';
COMMENT on Column ETL.JOB_RES_CTRL.JOB_RES_TYPE  is '作业资源类型';
COMMENT on Column ETL.JOB_RES_CTRL.JOB_RES_NM    is '资源名称';
COMMENT on Column ETL.JOB_RES_CTRL.JOB_RES_MAX   is '最大资源量';
COMMENT on Column ETL.JOB_RES_CTRL.JOB_RES_IDLE  is '可用资源量';
COMMENT on Column ETL.JOB_RES_CTRL.REMARK        is '备注';


--------------------------------------------------
-- CREATE TABLE ETL.PM_MONITOR
--------------------------------------------------
CREATE TABLE ETL.PM_MONITOR (
    EDW_DATE                       DATE                NOT NULL    ,
    MONITORITEM                    VARCHAR(10)         NOT NULL    ,
    PMSTEP                         VARCHAR(10)         NOT NULL   Default 'WAITING'  ) 
  ;
COMMENT on Table ETL.PM_MONITOR is 'BUF层调度监控表';
COMMENT on Column ETL.PM_MONITOR.EDW_DATE        is '当前处理日期';
COMMENT on Column ETL.PM_MONITOR.MONITORITEM     is '监控项(CHECKPM/SCHEPM/GLOBPM)';
COMMENT on Column ETL.PM_MONITOR.PMSTEP          is '所处阶段（WAITING/PROCESSING/PAUSE/DONE）';


--------------------------------------------------
-- CREATE TABLE ETL.EXP_METADATA
--------------------------------------------------
CREATE TABLE ETL.EXP_METADATA (
    EXP_NM                         VARCHAR(128)        NOT NULL    ,
    EXP_CNM                        VARCHAR(128)                   Default ''  ,
    EXP_RUN_TYPE                   VARCHAR(6)          NOT NULL   Default 'D'  ,
    EXP_RES_TYPE                   VARCHAR(20)         NOT NULL   Default 'EXP'  ,
    EXP_RUN_DT                     VARCHAR(20)                    Default ''  ,
    EXP_FIELD                      VARCHAR(5000)       NOT NULL   Default '*'  ,
    TAB_SCHEMA                     VARCHAR(100)        NOT NULL    ,
    EXP_TAB                        VARCHAR(200)        NOT NULL    ,
    EXP_WHERE                      VARCHAR(1000)                  Default ' '  ,
    SQLTYPE                        VARCHAR(2)          NOT NULL   Default 'S'  ,
    ZOQ                            VARCHAR(4)          NOT NULL   Default ' '  ,
    COMPLEXSQL                     VARCHAR(5000)                   ,
    EXP_PRIO                       INTEGER             NOT NULL   Default 0  ,
    EXP_WKRES                      INTEGER             NOT NULL   Default 1  ,
    SYSCODE                        VARCHAR(4)                    With Default ''  ,
    DELFILE_NM                     VARCHAR(128)        NOT NULL    ,
    BELO_MODE                      VARCHAR(30)         NOT NULL    ) 
  ;
COMMENT on Table ETL.EXP_METADATA is '卸数元数据登记表';
COMMENT on Column ETL.EXP_METADATA.EXP_NM        is '作业英文名称';
COMMENT on Column ETL.EXP_METADATA.EXP_CNM       is '作业中文名';
COMMENT on Column ETL.EXP_METADATA.EXP_RUN_TYPE  is '作业运行类型。/D每天/W每周/M每月/Y每年/N月底/Z每年年底';
COMMENT on Column ETL.EXP_METADATA.EXP_RES_TYPE  is '作业资源类型';
COMMENT on Column ETL.EXP_METADATA.EXP_RUN_DT    is '作业运行时点。同EXP_RUN_TYPE共同作用';
COMMENT on Column ETL.EXP_METADATA.EXP_FIELD     is '字段列表，默认为（*），多个字段用逗号分隔';
COMMENT on Column ETL.EXP_METADATA.TAB_SCHEMA    is '卸数表模式名';
COMMENT on Column ETL.EXP_METADATA.EXP_TAB       is '卸数表名';
COMMENT on Column ETL.EXP_METADATA.EXP_WHERE     is '取数WHERE条件';
COMMENT on Column ETL.EXP_METADATA.SQLTYPE       is 'SQL类型：默认S；复杂取数sql直接写在COMPLEXSQL字段';
COMMENT on Column ETL.EXP_METADATA.ZOQ           is '卸数方式 Z:增量 Q:全量';
COMMENT on Column ETL.EXP_METADATA.COMPLEXSQL    is '交叉表复杂SQL';
COMMENT on Column ETL.EXP_METADATA.EXP_PRIO      is '作业优先级，数字越大，优先级越高';
COMMENT on Column ETL.EXP_METADATA.EXP_WKRES     is '作业资源占用量，可调整、估算';
COMMENT on Column ETL.EXP_METADATA.SYSCODE       is '系统代号';
COMMENT on Column ETL.EXP_METADATA.DELFILE_NM    is '卸数DEL本文名';
COMMENT on Column ETL.EXP_METADATA.BELO_MODE     is '隶属业务层层(IPDDATA、SUMDATA、RPTDATA)';

--------------------------------------------------
-- CREATE TABLE ETL.EXP_MONITOR
--------------------------------------------------
CREATE TABLE ETL.EXP_MONITOR (
    EXP_DATE                       DATE                NOT NULL    ,
    MONITORITEM                    VARCHAR(10)         NOT NULL    ,
    PROCSTEP                       VARCHAR(10)         NOT NULL    ,
    NOTE                           VARCHAR(255)                   Default ''  ) 
  ;
COMMENT on Table ETL.EXP_MONITOR is 'EXP监控表';
COMMENT on Column ETL.EXP_MONITOR.EXP_DATE       is '当前卸数日期';
COMMENT on Column ETL.EXP_MONITOR.MONITORITEM    is '监控项';
COMMENT on Column ETL.EXP_MONITOR.PROCSTEP       is '所处阶段';
COMMENT on Column ETL.EXP_MONITOR.NOTE           is '备注';

--------------------------------------------------
-- CREATE TABLE ETL.EXP_SCHE
--------------------------------------------------
CREATE TABLE ETL.EXP_SCHE (
    EXP_SEQ_ID                     INTEGER             NOT NULL    
	generated by default as identity (start with 1  increment by 1  cache 20),
    EXP_NM                         VARCHAR(128)        NOT NULL    ,
    TAB_SCHEMA                     VARCHAR(100)        NOT NULL    ,
    EXP_TAB                        VARCHAR(200)        NOT NULL    ,
    EXP_STATUS                     VARCHAR(10)                Default ''  ,
    EXP_RUN_TYPE                   VARCHAR(6)          NOT NULL    ,
    EXP_RES_TYPE                   VARCHAR(20)         NOT NULL    ,
    SQLTYPE                        VARCHAR(2)          NOT NULL   Default 'S'  ,
    ZOQ                            VARCHAR(4)                      ,
    EXP_PRIO                       INTEGER             NOT NULL    ,
    EXP_WKRES                      INTEGER             NOT NULL    ,
    EXP_SCHE_DATE                  DATE                NOT NULL    ,
    SYSCODE                        VARCHAR(5)                     Default ''  ,
    DELFILE_NM                     VARCHAR(128)        NOT NULL    ,
    EXP_BEGIN_DT                   DATE                            ,
    EXP_BEGIN_TM                   TIME                            ,
    EXP_END_DT                     DATE                            ,
    EXP_END_TM                     TIME                            ,
    BELO_MODE                      VARCHAR(30)         NOT NULL    ) 
  ;
COMMENT on Table ETL.EXP_SCHE is 'EXP调度登记表';
COMMENT on Column ETL.EXP_SCHE.EXP_SEQ_ID        is '作业ID序号';
COMMENT on Column ETL.EXP_SCHE.EXP_NM            is '作业英文名称';
COMMENT on Column ETL.EXP_SCHE.TAB_SCHEMA        is '卸数表模式名称';
COMMENT on Column ETL.EXP_SCHE.EXP_TAB           is '卸数表名称';
COMMENT on Column ETL.EXP_SCHE.EXP_STATUS        is '作业状态（WAITING/RUNNING/DONE/EXPERROR/BZIP2ERR）';
COMMENT on Column ETL.EXP_SCHE.EXP_RUN_TYPE      is '作业运行类型';
COMMENT on Column ETL.EXP_SCHE.EXP_RES_TYPE      is '作业资源类型';
COMMENT on Column ETL.EXP_SCHE.SQLTYPE           is 'SQL类型：默认S；复杂SQL直接写在COMPLEXSQL字段';
COMMENT on Column ETL.EXP_SCHE.ZOQ               is 'Z:增量 Q:全量';
COMMENT on Column ETL.EXP_SCHE.EXP_PRIO          is '作业优先级，数字越大，优先级越高';
COMMENT on Column ETL.EXP_SCHE.EXP_WKRES         is '作业资源占用量，可调整、估算';
COMMENT on Column ETL.EXP_SCHE.EXP_SCHE_DATE     is '处理数据日期';
COMMENT on Column ETL.EXP_SCHE.SYSCODE           is '系统代号';
COMMENT on Column ETL.EXP_SCHE.DELFILE_NM        is 'DEL文本名称';
COMMENT on Column ETL.EXP_SCHE.EXP_BEGIN_DT      is '作业运行开始日期';
COMMENT on Column ETL.EXP_SCHE.EXP_BEGIN_TM      is '作业运行开始时间';
COMMENT on Column ETL.EXP_SCHE.EXP_END_DT        is '作业运行结束日期';
COMMENT on Column ETL.EXP_SCHE.EXP_END_TM        is '作业运行结束时间';
COMMENT on Column ETL.EXP_SCHE.BELO_MODE         is '隶属业务层(IPDDATA、SUMDATA、RPTDATA)';


--------------------------------------------------
-- Create Trigger ETL.SYNCETLDATE
--------------------------------------------------
CREATE TRIGGER ETL.SyncETLDate AFTER UPDATE OF CURDATE ON ETL.SYSTEMPARA REFERENCING OLD AS o NEW AS n FOR EACH ROW MODE DB2SQL 
BEGIN ATOMIC 
 declare v_curw char(1);  
 declare v_cury char(4);  
 declare v_curm char(2);  
 declare v_curd char(2);  
 declare v_emonth char(1);
 declare v_eyear char(1); 
 declare v_D1,v_D2 date;  
 SET v_curw = char(DAYOFWEEK_ISO(n.CURDATE));
 set v_D1 = n.CURDATE;
 set v_D2 = date(days(n.CURDATE)+1);
 set v_cury = char(year(n.CURDATE)) ;
 set v_curm = char(month(n.CURDATE));
 set v_curm=case when length(trim(v_curm))=1 then '0'||v_curm else v_curm end;
 set v_curd = char(day(n.CURDATE)) ;
 set v_curd=case when length(trim(v_curd))=1 then '0'||v_curd else v_curd end;
 set v_emonth = 'N';
 set v_eyear = 'N';
 IF month(v_D1) <> month(v_D2) THEN 
 	  set v_emonth = 'Y';
 END IF;
 IF year(v_D1) <> year(v_D2) THEN 
 	  set v_eyear = 'Y';
 END IF;
 update ETL.SYSTEMPARA SET CURW = v_curw , cury = v_cury , curm = v_curm , curd = v_curd;
 update ETL.systempara set EMONTH = v_emonth , EYEAR = v_eyear, PROCSTEP = 'WAITING';
 
 UPDATE ETL.PM_MONITOR SET EDW_DATE = n.CURDATE, PMSTEP = 'WAITING';
 
END;


--------------------------------------------------
-- Create procedure ETL.PRC_MAKE_CUR_EXP
--------------------------------------------------
CREATE PROCEDURE ETL.PRC_MAKE_CUR_EXP ()
       SPECIFIC  ETL.PRC_MAKE_CUR_EXP
       LANGUAGE SQL
----------------------------------------------------------------
-- PL/SQL名称: ETL.PRC_MAKE_CUR_EXP
-- 功 能: EXP调度表初始化，生成数据日期所需要的作业列表
-- 影响库表：ETL.EXP_SCHE
-- 作 者: Gao Chong
-- 编码日期：2012.11.20
-- 修 改 ：  NULL
----------------------------------------------------------------
P1: BEGIN
        
        DECLARE current_dt_temp date;
        DECLARE v_sts char(10);
        DECLARE RET_CODE INT DEFAULT 0 ;
        DECLARE NOT_FOUND CONDITION FOR SQLSTATE  '02000';
        DECLARE CONTINUE HANDLER FOR NOT_FOUND SET RET_CODE = 1;
        
        ---------------------------------------------------------
        SELECT EXP_DATE INTO current_dt_temp FROM ETL.EXP_MONITOR WHERE MONITORITEM='EXPSCHE' fetch first 1 rows only;
        SELECT PROCSTEP INTO v_sts FROM ETL.EXP_MONITOR WHERE MONITORITEM='EXPSCHE' fetch first 1 rows only;
                    
        IF v_sts = 'WAITING' THEN
                -----------------------------------------------------------
                -- 更新EXP_MONITOR系统状态
                -----------------------------------------------------------
                UPDATE ETL.EXP_MONITOR SET PROCSTEP = 'PROCESSING';
                
                -----------------------------------------------------------
                -- 生成当日JOB队列, 支持重跑
                -----------------------------------------------------------
                DELETE FROM ETL.EXP_SCHE WHERE EXP_SCHE_DATE = current_dt_temp ;
                
                --------------------------------
                -- JOB : 非X 
                -- 1. 生成隶属基础层作业
                --------------------------------
                INSERT INTO ETL.EXP_SCHE (EXP_NM, TAB_SCHEMA, EXP_TAB, EXP_STATUS, EXP_RUN_TYPE, EXP_RES_TYPE, SQLTYPE, ZOQ, EXP_PRIO, EXP_WKRES, EXP_SCHE_DATE, SYSCODE, DELFILE_NM, BELO_MODE) SELECT EXP_NM, TAB_SCHEMA, EXP_TAB, 'WAITING', EXP_RUN_TYPE, EXP_RES_TYPE, SQLTYPE, ZOQ, EXP_PRIO, EXP_WKRES, current_dt_temp, SYSCODE, DELFILE_NM, BELO_MODE FROM ETL.EXP_METADATA WHERE EXP_RUN_TYPE <> 'X' ;
                
                -- 2. 生成隶属汇总层作业(取消2012-11-21)
                ---INSERT INTO ETL.EXP_SCHE (EXP_NM, TAB_SCHEMA, EXP_TAB, EXP_STATUS, EXP_RUN_TYPE, EXP_RES_TYPE, SQLTYPE, ZOQ, EXP_PRIO, EXP_WKRES, EXP_SCHE_DATE, SYSCODE, DELFILE_NM, IPDDATA, SUMDATA, RPTDATA) SELECT EXP_NM, TAB_SCHEMA, EXP_TAB, 'WAITING', EXP_RUN_TYPE, EXP_RES_TYPE, SQLTYPE, ZOQ, EXP_PRIO, EXP_WKRES, current_dt_temp, SYSCODE, DELFILE_NM, '', SUMDATA, '' FROM ETL.EXP_METADATA WHERE EXP_RUN_TYPE <> 'X' AND trim(SUMDATA)='Y';
                
                -- 3. 生成隶属报表层作业(取消2012-11-21)
                ---INSERT INTO ETL.EXP_SCHE (EXP_NM, TAB_SCHEMA, EXP_TAB, EXP_STATUS, EXP_RUN_TYPE, EXP_RES_TYPE, SQLTYPE, ZOQ, EXP_PRIO, EXP_WKRES, EXP_SCHE_DATE, SYSCODE, DELFILE_NM, IPDDATA, SUMDATA, RPTDATA) SELECT EXP_NM, TAB_SCHEMA, EXP_TAB, 'WAITING', EXP_RUN_TYPE, EXP_RES_TYPE, SQLTYPE, ZOQ, EXP_PRIO, EXP_WKRES, current_dt_temp, SYSCODE, DELFILE_NM, '', '', RPTDATA FROM ETL.EXP_METADATA WHERE EXP_RUN_TYPE <> 'X' AND trim(RPTDATA)='Y';
                --------------------------------
        ELSEIF v_sts = 'PAUSE' THEN
               UPDATE ETL.EXP_MONITOR SET PROCSTEP='PROCESSING';
        END IF;
        IF RET_CODE = 1 THEN 
               RETURN ;
        END IF;
END P1;

--------------------------------------------------
-- Create procedure ETL.PRC_MAKE_CUR_JOB
--------------------------------------------------
CREATE PROCEDURE ETL.PRC_MAKE_CUR_JOB (  ) 
        SPECIFIC ETL.PRC_MAKE_CUR_JOB
        LANGUAGE SQL 
----------------------------------------------------------------
-- PL/SQL: PRC_MAKE_CUR_JOB
-- 功 能 ：调度JOB初始化，生成数据日期所需要的作业列表
-- 影响表：ETL.JOB_SCHE
-- 作 者 : gaochong
-- 编写日: 2012-10-22
-- 修 改 ：暂无
-- 原 因 ：
-----------------------------------------------------------------
P1: BEGIN
        
        DECLARE current_dt_temp date;
        DECLARE v_eyear char(1);
        DECLARE v_emonth char(1);
        DECLARE v_curw char(1);
        DECLARE v_curd char(2);
        DECLARE v_curm char(2);
        DECLARE v_curmmdd char(4);
        DECLARE v_sts char(10);
        DECLARE RET_CODE INT DEFAULT 0 ;
        DECLARE NOT_FOUND CONDITION FOR SQLSTATE  '02000';
        DECLARE CONTINUE HANDLER FOR NOT_FOUND SET RET_CODE = 1;
	      
	--------------------------------------
        SELECT PROCSTEP INTO v_sts FROM ETL.SYSTEMPARA fetch first 1 rows only;
		    
        IF v_sts = 'WAITING' THEN
                -----------------------------------------------------------
                -- 更新系统状态
                -----------------------------------------------------------	
                UPDATE ETL.SYSTEMPARA SET PROCSTEP = 'PROCESSING';
                
                ---------------------------------------
                -- 日期、时间类临时变量赋值
                ---------------------------------------
                SELECT curdate, curm, curd, curw, emonth, eyear, curm||curd into current_dt_temp, v_curm, v_curd, v_curw, v_emonth, v_eyear, v_curmmdd FROM ETL.SYSTEMPARA fetch first 1 rows only;                
                
                -----------------------------------------------------------
                -- 生成当日JOB队列, 支持重跑
                -----------------------------------------------------------	
                DELETE FROM ETL.JOB_SCHE WHERE JOB_SCHE_DATE = current_dt_temp ;
                
                --------------------------------
                -- JOB : D ：每日需要执行的JOB
                --------------------------------
                INSERT INTO ETL.JOB_SCHE (JOB_NM, JOB_STATUS, JOB_PRIO, JOB_SCHE_DATE, SYSCODE, DELFILE, FLAG ) SELECT a.JOB_NM, 'PRE', a.JOB_PRIO, current_dt_temp, a.SYSCODE, a.DELFILE, a.FLAG FROM ETL.JOB_METADATA a WHERE a.job_run_type='D';
                                
                --------------------------------
                -- JOB : W ：每周需要执行的JOB
                --------------------------------
                INSERT INTO ETL.JOB_SCHE (JOB_NM, JOB_STATUS, JOB_PRIO, JOB_SCHE_DATE, SYSCODE, DELFILE, FLAG ) SELECT JOB_NM, 'PRE', JOB_PRIO, current_dt_temp, SYSCODE, DELFILE, FLAG FROM ETL.JOB_METADATA  WHERE job_run_type='W' AND job_run_dt=v_curw;
                
                --------------------------------
                -- JOB : M ：每月需要执行的JOB
                --------------------------------
                INSERT INTO ETL.JOB_SCHE (JOB_NM, JOB_STATUS, JOB_PRIO,JOB_SCHE_DATE, SYSCODE, DELFILE, FLAG ) SELECT JOB_NM, 'PRE', JOB_PRIO, current_dt_temp, SYSCODE, DELFILE, FLAG FROM ETL.JOB_METADATA  WHERE job_run_type='M' AND job_run_dt=v_curd;
                
                --------------------------------
                -- JOB :Y :每年需要执行的JOB
                --------------------------------
                INSERT INTO ETL.JOB_SCHE (JOB_NM, JOB_STATUS, JOB_PRIO,JOB_SCHE_DATE, SYSCODE, DELFILE, FLAG ) SELECT JOB_NM, 'PRE', JOB_PRIO, current_dt_temp, SYSCODE, DELFILE, FLAG FROM ETL.JOB_METADATA  WHERE job_run_type='Y' AND job_run_dt=v_curmmdd;
                
                
                ----------------------------------
                -- JOB : Z :年底需要执行的JOB
                ----------------------------------
                IF v_eyear = 'Y' then
                   INSERT INTO ETL.JOB_SCHE (JOB_NM, JOB_STATUS, JOB_PRIO, JOB_SCHE_DATE, SYSCODE, DELFILE, FLAG ) SELECT a.JOB_NM, 'PRE', a.JOB_PRIO, current_dt_temp, a.SYSCODE, a.DELFILE, a.FLAG FROM ETL.JOB_METADATA a WHERE a.job_run_type='Z';
                end if;
                
                ----------------------------------
                -- JOB : N :月底需要执行的JOB
                ----------------------------------
                IF v_emonth = 'Y' then
                   INSERT INTO ETL.JOB_SCHE (JOB_NM, JOB_STATUS, JOB_PRIO, JOB_SCHE_DATE, SYSCODE, DELFILE, FLAG ) SELECT a.JOB_NM, 'PRE', a.JOB_PRIO, current_dt_temp, a.SYSCODE, a.DELFILE, a.FLAG FROM ETL.JOB_METADATA a WHERE a.job_run_type='N';
                end if;
                
        ELSEIF v_sts = 'PAUSE' THEN
	             UPDATE ETL.SYSTEMPARA SET PROCSTEP='PROCESSING';
	      END IF;
        IF RET_CODE = 1 THEN 
	         RETURN ;
	      END IF;
END P1;

--------------------------------------------------
-- Create procedure ETL.PRC_SET_CUR_JOB_STS
--------------------------------------------------
CREATE PROCEDURE ETL.PRC_SET_CUR_JOB_STS (  ) 
       SPECIFIC  ETL.PRC_SET_CUR_JOB_STS
       LANGUAGE  SQL
---------------------------------------------------------------
-- PL/SQL: ETL.PRC_SET_CUR_JOB_STS
-- 功 能 ：作业状态转换, 将满足条件的'PRE'作业转为'WAITING'作业
-- 影响表：ETL.JOB_SCHE
-- 作 者 : gaochong
-- 编写日：2012-10-22
-- 修 改 : 暂无
-- 原 因 ：
---------------------------------------------------------------
P1: BEGIN 
        DECLARE v_curdate date;
        DECLARE v_num integer;
        DECLARE RET_CODE INT DEFAULT 0 ;
        DECLARE NOT_FOUND CONDITION FOR SQLSTATE '02000';
        DECLARE CONTINUE HANDLER FOR NOT_FOUND SET RET_CODE = 1;
        
        SET RET_CODE = 0 ;
                        
        SELECT CURDATE INTO v_curdate FROM ETL.SYSTEMPARA fetch first 1 rows only;
        SELECT count(*) INTO v_num FROM ETL.JOB_SCHE WHERE JOB_SCHE_DATE=v_curdate;
        
        IF v_num > 0 THEN
           UPDATE ETL.JOB_SCHE SET JOB_STATUS='WAITING' 
           WHERE JOB_SCHE_DATE=v_curdate AND JOB_STATUS='PRE' AND 
                 JOB_SEQ_ID IN (SELECT JOB_SEQ_ID FROM ETL.JOB_SCHE WHERE JOB_SCHE_DATE=v_curdate AND JOB_STATUS='PRE' AND JOB_SEQ_ID NOT IN 
                                      (SELECT JOB_SEQ_ID 
                                              FROM  ( SELECT a.JOB_SEQ_ID, a.JOB_NM, b.PRE_JOB, c.JOB_STATUS PRE_STS 
                                                      FROM ETL.JOB_SCHE a, 
                                                           ETL.JOB_SEQ b,  
                                                           ETL.JOB_SCHE c  
                                                      WHERE a.JOB_SCHE_DATE=v_curdate AND a.JOB_STATUS='PRE' AND a.JOB_NM=b.JOB_NM AND b.PRE_JOB=c.JOB_NM AND c.JOB_SCHE_DATE=v_curdate 
                                                      UNION 
                                                      SELECT a.JOB_SEQ_ID, a.JOB_NM, ' ' PRE_JOB, 'DONE' PRE_STS 
                                                      FROM ETL.JOB_SCHE a 
                                                      WHERE a.JOB_SCHE_DATE=v_curdate AND a.JOB_STATUS='PRE' AND a.JOB_NM NOT IN 
                                                      ( SELECT DISTINCT JOB_NM FROM ETL.JOB_SEQ ) 
                                              ) AS aa 
                                              WHERE PRE_STS <> 'DONE' 
                                              GROUP BY JOB_SEQ_ID 
                                      ) 
                             ); 
        END IF; 
        
	      IF RET_CODE = 1 THEN 
	         RETURN ;
	      END IF;
END P1 
;