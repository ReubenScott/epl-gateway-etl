
--------------------------------------------------
-- EDW源系统文件列表
--------------------------------------------------
Create table ETL.BUF_METADATA (
    SYSCODE         CHAR(4)                             ,
    SCHEMATA        VARCHAR(15)     NOT NULL            ,
    STBNAME         VARCHAR(100)    NOT NULL            ,
    STB_NAME_CN     VARCHAR(200)                        ,
    DELNAME         VARCHAR(100)                        ,
    SPLIT           CHAR(4)                             ,
    JOB_NM          VARCHAR(30)                         ,
    RUNTYPE         VARCHAR(1)                          ,
    RUN_DT          VARCHAR(20)                         ,
    PTYPE           VARCHAR(10)                         ,
    P_PRIO          INTEGER         With Default 1000   ,
    P_WKRES         INTEGER         With Default 3      ,
    TABSPS          VARCHAR(30)                         ,
    INDSPS          VARCHAR(30)                         ,
    REMARK          VARCHAR(256)                        ,
    PRIMARY KEY(SCHEMATA,STBNAME)                       ,
    CHECK ( RUNTYPE in ('T','F'))  
    ENFORCED                     --强制执行此约束
    ENABLE  QUERY  OPTIMIZATION  --查询优化期间考虑此约束
    ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  
Compress Yes 
Partitioning Key (SCHEMATA) Using Hashing 
ORGANIZE BY DIMENSIONS (SYSCODE,PTYPE,RUNTYPE ) ;


Comment on Table  ETL.BUF_METADATA             is 'EDW源系统文件列表' ;
Comment on Column ETL.BUF_METADATA.SYSCODE     is '源系统代码' ;
Comment on Column ETL.BUF_METADATA.STBNAME     is '源系统英文表名' ;
Comment on Column ETL.BUF_METADATA.STB_NAME_CN is '源系统中文表名' ;
Comment on Column ETL.BUF_METADATA.DELNAME     is '源系统文件全名' ;
Comment on Column ETL.BUF_METADATA.SCHEMATA    is '源系统表SCHEMA' ;
Comment on Column ETL.BUF_METADATA.JOB_NM      is '下一步JOB: 存储过程名' ;
Comment on Column ETL.BUF_METADATA.SPLIT       is '分隔符' ;
Comment on Column ETL.BUF_METADATA.PTYPE       is '处理方式： 全量、增量、变量' ;
Comment on Column ETL.BUF_METADATA.RUNTYPE     is '运行标志(T是F否)' ;
Comment on Column ETL.BUF_METADATA.RUN_DT      is '作业运行时点类型。/D每天/W每周/M每月/Y每年' ;
Comment on Column ETL.BUF_METADATA.P_PRIO      is '处理优先级,数字越大，优先级越高越先处理' ;
Comment on Column ETL.BUF_METADATA.P_WKRES     is '处理资源占用量，可调整、估算' ;
Comment on Column ETL.BUF_METADATA.TABSPS      is '数据表空间' ;
Comment on Column ETL.BUF_METADATA.INDSPS      is '索引表空间' ;
Comment on Column ETL.BUF_METADATA.REMARK      is '备注' ;


--------------------------------------------------
-- Create Table ETL.BUF_SCHE
--------------------------------------------------
Create table ETL.BUF_SCHE (
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
    PRIMARY KEY (SRC_DT,SCHEMATA,STBNAME)  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  
Compress Yes 
Partitioning Key (SCHEMATA) Using Hashing
ORGANIZE BY DIMENSIONS (SRC_DT,P_STATUS ) ;

Comment on Table ETL.BUF_SCHE                    is '缓冲层调度登记表';
Comment on Column ETL.BUF_SCHE.SRC_DT            is 'EDW应用处理数据日期';
Comment on Column ETL.BUF_SCHE.SYSCODE           is '源系统代码';
Comment on Column ETL.BUF_SCHE.SCHEMATA          is '源系统表SCHEMA';
Comment on Column ETL.BUF_SCHE.STBNAME           is '源系统英文表名';
Comment on Column ETL.BUF_SCHE.DELNAME           is '源系统带后缀文本名';
Comment on Column ETL.BUF_SCHE.STB_NAME_CN       is '源系统中文表名';
Comment on Column ETL.BUF_SCHE.JOB_NM            is '下一步JOB: 存储过程名';
Comment on Column ETL.BUF_SCHE.P_PRIO            is '处理优先级，数字越大优先级越高';
Comment on Column ETL.BUF_SCHE.P_WKRES           is '处理资源占用量，可调整、估算';
Comment on Column ETL.BUF_SCHE.P_STATUS          is '处理状态(PRE/WAITING/PROPRESS/DONE/ERROR)';
Comment on Column ETL.BUF_SCHE.BEGIN_TM          is '处理运行开始时间';
Comment on Column ETL.BUF_SCHE.END_TM            is '处理运行结束时间';

--------------------------------------------------
-- 作业元数据登记表
--------------------------------------------------
Create table ETL.JOB_METADATA (
    SYSCODE          CHAR(4)             NOT NULL  With Default 'EDW'  ,
    JOB_NM           VARCHAR(128)        NOT NULL    ,
    JOB_CNM          VARCHAR(128)        ,
    JOB_TYPE         VARCHAR(8)          With Default 'SP'  ,
    RES_TYPE         VARCHAR(20)         ,
    RUNTYPE          VARCHAR(1)          ,
    RUN_DT           VARCHAR(20)         ,
    JOB_CMD          VARCHAR(200)        ,
    JOB_PAR          VARCHAR(200)        ,
    JOB_PRIO         INTEGER             With Default 7000  ,
    JOB_WKRES        INTEGER             With Default 2  ,
    REMARK           VARCHAR(256)        ,
    PRIMARY KEY(SYSCODE,JOB_NM) ,
    CHECK ( RUNTYPE in ('T','F'))  ENFORCED  ENABLE  QUERY  OPTIMIZATION ,
    CHECK ( RUN_DT in ('D','W','T','M','Q','Y'))  ENFORCED  ENABLE  QUERY  OPTIMIZATION 
    )
in TBS_CFG_DATA Index in TBS_CFG_INDEX  
Compress Yes 
Partitioning Key (JOB_NM) Using Hashing  
ORGANIZE BY DIMENSIONS (SYSCODE,JOB_TYPE,RUNTYPE ) ;


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
Create table ETL.JOB_SCHE (
    SID                            VARCHAR(32)         NOT NULL    ,
    SRC_DT                         DATE                NOT NULL    ,
    SYSCODE                        CHAR(4)                       With Default ''  ,
    JOB_NM                         VARCHAR(128)        NOT NULL  With Default ' '  ,    
    JOB_STATUS                     VARCHAR(8)                    With Default ''  ,
    JOB_PRIO                       INTEGER             NOT NULL    ,
    DELFILE                        VARCHAR(50)                   With Default ''  ,
    BEGIN_TM                       TIMESTAMP                       ,
    END_TM                         TIMESTAMP                       
   ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  
Compress Yes 
Partitioning Key (JOB_NM) Using Hashing;

Comment on Table ETL.JOB_SCHE is '基础层作业调度表';
Comment on Column ETL.JOB_SCHE.SID        is '作业序号';
Comment on Column ETL.JOB_SCHE.JOB_NM            is '作业英文名称';
Comment on Column ETL.JOB_SCHE.JOB_STATUS        is '作业状态(PRE/WAITING/RUNNING/DONE/ERROR)';
Comment on Column ETL.JOB_SCHE.JOB_PRIO          is '作业优先级，数字越大，优先级越高';
Comment on Column ETL.JOB_SCHE.SRC_DT            is '源系统数据日期';
Comment on Column ETL.JOB_SCHE.SYSCODE           is '系统代号';
Comment on Column ETL.JOB_SCHE.DELFILE           is '原文件名（DEL文件名）';
Comment on Column ETL.JOB_SCHE.BEGIN_TM          is '作业运行开始时间';
Comment on Column ETL.JOB_SCHE.END_TM            is '作业运行结束时间';


--------------------------------------------------
-- Create Table ETL.EDW_SS_STS
--------------------------------------------------
Create table ETL.EDW_SS_STS (
    SYSCODE                        CHAR(4)             NOT NULL  With Default ' '  ,
    SYSENM                         VARCHAR(50)         NOT NULL  With Default ' '  ,
    EDW_DATE                       DATE                NOT NULL  With Default CURRENT DATE  ,
    PACKTYPE                       VARCHAR(5)          NOT NULL  With Default ' '  ,
    PRC_STS                        VARCHAR(30)         NOT NULL  With Default ' '  ,
    UNPACK_STS                     VARCHAR(3)          NOT NULL  With Default ' '  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  
Compress Yes ;

Comment on Table ETL.EDW_SS_STS is 'EDW源系统状态表';
Comment on Column ETL.EDW_SS_STS.SYSCODE         is '源系统代码';
Comment on Column ETL.EDW_SS_STS.SYSENM          is '源系统英文简称';
Comment on Column ETL.EDW_SS_STS.EDW_DATE        is 'EDW处理日期';
Comment on Column ETL.EDW_SS_STS.PACKTYPE        is '源提供数据打包方式(DFT TAR COMP等)';
Comment on Column ETL.EDW_SS_STS.PRC_STS         is '当前状态(WAITING/CHECKING/PRE/PROPRESS/DONE/ERROR/PERROR/UNTARERROR/NOTARERROR/HALFERROR等)';
Comment on Column ETL.EDW_SS_STS.UNPACK_STS      is '如果是打包文件，则标解包状态';

--------------------------------------------------
-- Create Table ETL.EXP_METADATA
--------------------------------------------------
Create table ETL.EXP_METADATA (
    EXP_NM                         VARCHAR(128)        NOT NULL    ,
    EXP_CNM                        VARCHAR(128)                  With Default ''  ,
    EXP_RUN_TYPE                   VARCHAR(6)          NOT NULL  With Default 'D'  ,
    EXP_RES_TYPE                   VARCHAR(20)         NOT NULL  With Default 'EXP'  ,
    EXP_RUN_DT                     VARCHAR(20)                   With Default ''  ,
    EXP_FIELD                      VARCHAR(5000)       NOT NULL  With Default '*'  ,
    TAB_SCHEMA                     VARCHAR(100)        NOT NULL    ,
    EXP_TAB                        VARCHAR(200)        NOT NULL    ,
    EXP_WHERE                      VARCHAR(1000)                 With Default ' '  ,
    SQLTYPE                        VARCHAR(2)          NOT NULL  With Default 'S'  ,
    ZOQ                            VARCHAR(4)          NOT NULL  With Default ' '  ,
    COMPLEXSQL                     VARCHAR(5000)                   ,
    EXP_PRIO                       INTEGER             NOT NULL  With Default 0  ,
    EXP_WKRES                      INTEGER             NOT NULL  With Default 1  ,
    SYSCODE                        VARCHAR(4)                    With Default ''  ,
    DELFILE_NM                     VARCHAR(128)        NOT NULL    ,
    BELO_MODE                      VARCHAR(30)         NOT NULL    ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.EXP_METADATA is '卸数元数据登记表';
Comment on Column ETL.EXP_METADATA.EXP_NM        is '作业英文名称';
Comment on Column ETL.EXP_METADATA.EXP_CNM       is '作业中文名';
Comment on Column ETL.EXP_METADATA.EXP_RUN_TYPE  is '作业运行类型。/D每天/W每周/M每月/Y每年/N月底/Z每年年底';
Comment on Column ETL.EXP_METADATA.EXP_RES_TYPE  is '作业资源类型';
Comment on Column ETL.EXP_METADATA.EXP_RUN_DT    is '作业运行时点。同EXP_RUN_TYPE共同作用';
Comment on Column ETL.EXP_METADATA.EXP_FIELD     is '字段列表，默认为（*），多个字段用逗号分隔';
Comment on Column ETL.EXP_METADATA.TAB_SCHEMA    is '卸数表模式名';
Comment on Column ETL.EXP_METADATA.EXP_TAB       is '卸数表名';
Comment on Column ETL.EXP_METADATA.EXP_WHERE     is '取数WHERE条件';
Comment on Column ETL.EXP_METADATA.SQLTYPE       is 'SQL类型：默认S；复杂取数sql直接写在COMPLEXSQL字段';
Comment on Column ETL.EXP_METADATA.ZOQ           is '卸数方式 Z:增量 Q:全量';
Comment on Column ETL.EXP_METADATA.COMPLEXSQL    is '交叉表复杂SQL';
Comment on Column ETL.EXP_METADATA.EXP_PRIO      is '作业优先级，数字越大，优先级越高';
Comment on Column ETL.EXP_METADATA.EXP_WKRES     is '作业资源占用量，可调整、估算';
Comment on Column ETL.EXP_METADATA.SYSCODE       is '系统代号';
Comment on Column ETL.EXP_METADATA.DELFILE_NM    is '卸数DEL本文名';
Comment on Column ETL.EXP_METADATA.BELO_MODE     is '隶属业务层层(IPDDATA、SUMDATA、RPTDATA)';

--------------------------------------------------
-- Create Table ETL.EXP_MONITOR
--------------------------------------------------
Create table ETL.EXP_MONITOR (
    EXP_DATE                       DATE                NOT NULL    ,
    MONITORITEM                    VARCHAR(10)         NOT NULL    ,
    PROCSTEP                       VARCHAR(10)         NOT NULL    ,
    NOTE                           VARCHAR(255)                  With Default ''  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.EXP_MONITOR is 'EXP监控表';
Comment on Column ETL.EXP_MONITOR.EXP_DATE       is '当前卸数日期';
Comment on Column ETL.EXP_MONITOR.MONITORITEM    is '监控项';
Comment on Column ETL.EXP_MONITOR.PROCSTEP       is '所处阶段';
Comment on Column ETL.EXP_MONITOR.NOTE           is '备注';

--------------------------------------------------
-- Create Table ETL.EXP_SCHE
--------------------------------------------------
Create table ETL.EXP_SCHE (
    EXP_SEQ_ID                     INTEGER             NOT NULL    
	generated by default as identity (start with 1  increment by 1  cache 20),
    EXP_NM                         VARCHAR(128)        NOT NULL    ,
    TAB_SCHEMA                     VARCHAR(100)        NOT NULL    ,
    EXP_TAB                        VARCHAR(200)        NOT NULL    ,
    EXP_STATUS                     VARCHAR(10)                   With Default ''  ,
    EXP_RUN_TYPE                   VARCHAR(6)          NOT NULL    ,
    EXP_RES_TYPE                   VARCHAR(20)         NOT NULL    ,
    SQLTYPE                        VARCHAR(2)          NOT NULL  With Default 'S'  ,
    ZOQ                            VARCHAR(4)                      ,
    EXP_PRIO                       INTEGER             NOT NULL    ,
    EXP_WKRES                      INTEGER             NOT NULL    ,
    EXP_SCHE_DATE                  DATE                NOT NULL    ,
    SYSCODE                        VARCHAR(5)                    With Default ''  ,
    DELFILE_NM                     VARCHAR(128)        NOT NULL    ,
    EXP_BEGIN_DT                   DATE                            ,
    EXP_BEGIN_TM                   TIME                            ,
    EXP_END_DT                     DATE                            ,
    EXP_END_TM                     TIME                            ,
    BELO_MODE                      VARCHAR(30)         NOT NULL    ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.EXP_SCHE is 'EXP调度登记表';
Comment on Column ETL.EXP_SCHE.EXP_SEQ_ID        is '作业ID序号';
Comment on Column ETL.EXP_SCHE.EXP_NM            is '作业英文名称';
Comment on Column ETL.EXP_SCHE.TAB_SCHEMA        is '卸数表模式名称';
Comment on Column ETL.EXP_SCHE.EXP_TAB           is '卸数表名称';
Comment on Column ETL.EXP_SCHE.EXP_STATUS        is '作业状态（WAITING/RUNNING/DONE/EXPERROR/BZIP2ERR）';
Comment on Column ETL.EXP_SCHE.EXP_RUN_TYPE      is '作业运行类型';
Comment on Column ETL.EXP_SCHE.EXP_RES_TYPE      is '作业资源类型';
Comment on Column ETL.EXP_SCHE.SQLTYPE           is 'SQL类型：默认S；复杂SQL直接写在COMPLEXSQL字段';
Comment on Column ETL.EXP_SCHE.ZOQ               is 'Z:增量 Q:全量';
Comment on Column ETL.EXP_SCHE.EXP_PRIO          is '作业优先级，数字越大，优先级越高';
Comment on Column ETL.EXP_SCHE.EXP_WKRES         is '作业资源占用量，可调整、估算';
Comment on Column ETL.EXP_SCHE.EXP_SCHE_DATE     is '处理数据日期';
Comment on Column ETL.EXP_SCHE.SYSCODE           is '系统代号';
Comment on Column ETL.EXP_SCHE.DELFILE_NM        is 'DEL文本名称';
Comment on Column ETL.EXP_SCHE.EXP_BEGIN_DT      is '作业运行开始日期';
Comment on Column ETL.EXP_SCHE.EXP_BEGIN_TM      is '作业运行开始时间';
Comment on Column ETL.EXP_SCHE.EXP_END_DT        is '作业运行结束日期';
Comment on Column ETL.EXP_SCHE.EXP_END_TM        is '作业运行结束时间';
Comment on Column ETL.EXP_SCHE.BELO_MODE         is '隶属业务层(IPDDATA、SUMDATA、RPTDATA)';

--------------------------------------------------
-- Create Table ETL.JOB_RES_CTRL
--------------------------------------------------
Create table ETL.JOB_RES_CTRL (
    JOB_RES_TYPE                   VARCHAR(20)         NOT NULL    ,
    JOB_RES_NM                     VARCHAR(200)                  With Default ''  ,
    JOB_RES_MAX                    INTEGER             NOT NULL    ,
    JOB_RES_IDLE                   INTEGER             NOT NULL    ,
    REMARK                         VARCHAR(256)                  With Default ''  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.JOB_RES_CTRL is '作业资源控制登记表';
Comment on Column ETL.JOB_RES_CTRL.JOB_RES_TYPE  is '作业资源类型';
Comment on Column ETL.JOB_RES_CTRL.JOB_RES_NM    is '资源名称';
Comment on Column ETL.JOB_RES_CTRL.JOB_RES_MAX   is '最大资源量';
Comment on Column ETL.JOB_RES_CTRL.JOB_RES_IDLE  is '可用资源量';
Comment on Column ETL.JOB_RES_CTRL.REMARK        is '备注';


--------------------------------------------------
-- Create Table ETL.JOB_SEQ
--------------------------------------------------
Create table ETL.JOB_SEQ (
    JOB_NM                         VARCHAR(128)        NOT NULL    ,
    PRE_JOB                        VARCHAR(128)        NOT NULL  With Default ' '  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.JOB_SEQ is '基础层作业依赖关系登记表';
Comment on Column ETL.JOB_SEQ.JOB_NM             is '作业英文名称';
Comment on Column ETL.JOB_SEQ.PRE_JOB            is '前置作业名';

--------------------------------------------------
-- Create Table ETL.PM_MONITOR
--------------------------------------------------
Create table ETL.PM_MONITOR (
    EDW_DATE                       DATE                NOT NULL    ,
    MONITORITEM                    VARCHAR(10)         NOT NULL    ,
    PMSTEP                         VARCHAR(10)         NOT NULL  With Default 'WAITING'  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.PM_MONITOR is 'BUF层调度监控表';
Comment on Column ETL.PM_MONITOR.EDW_DATE        is '当前处理日期';
Comment on Column ETL.PM_MONITOR.MONITORITEM     is '监控项(CHECKPM/SCHEPM/GLOBPM)';
Comment on Column ETL.PM_MONITOR.PMSTEP          is '所处阶段（WAITING/PROCESSING/PAUSE/DONE）';

--------------------------------------------------
-- Create Table ETL.SOURCESYS
--------------------------------------------------
Create table ETL.SOURCESYS (
    SYSCODE                        CHAR(4)             NOT NULL  With Default ' '  ,
    SYSENM                         VARCHAR(50)         NOT NULL  With Default ' '  ,
    SYSCNM                         VARCHAR(100)        NOT NULL  With Default ' '  ,
    INUSE                          VARCHAR(3)          NOT NULL  With Default 'NO'  ,
    PACKTYPE                       VARCHAR(5)          NOT NULL  With Default 'TAR'  ,
    NOTE                           VARCHAR(50)                   With Default ''  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.SOURCESYS is '源系统登记表';
Comment on Column ETL.SOURCESYS.SYSCODE          is '源系统代码';
Comment on Column ETL.SOURCESYS.SYSENM           is '源系统英文名称';
Comment on Column ETL.SOURCESYS.SYSCNM           is '源系统中文名称';
Comment on Column ETL.SOURCESYS.INUSE            is '源系统是否在用';
Comment on Column ETL.SOURCESYS.PACKTYPE         is '源提供数方式';
Comment on Column ETL.SOURCESYS.NOTE             is '备注';

--------------------------------------------------
-- Create Table ETL.SYSTEMPARA
--------------------------------------------------
Create table ETL.SYSTEMPARA (
    CURDATE                        DATE                NOT NULL    ,
    PROCSTEP                       VARCHAR(10)                   With Default 'WAITING'  ,
    CURY                           CHAR(4)             NOT NULL    ,
    CURM                           CHAR(2)             NOT NULL    ,
    CURD                           CHAR(2)             NOT NULL    ,
    CURW                           CHAR(2)             NOT NULL    ,
    EMONTH                         CHAR(1)                       With Default 'N'  ,
    EYEAR                          CHAR(1)                       With Default 'N'  ,
    BUFDAYS                        INTEGER                       With Default 1  ,
    IPDDAYS                        INTEGER                       With Default 7  ,
    SUMDAYS                        INTEGER                       With Default 30  ,
    RPTDAYS                        INTEGER                       With Default 30  ,
    LOGDAYS                        INTEGER                       With Default 7  ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX  ;
Comment on Table ETL.SYSTEMPARA is '系统参数表';
Comment on Column ETL.SYSTEMPARA.CURDATE         is '当前处理日期';
Comment on Column ETL.SYSTEMPARA.PROCSTEP        is '所处阶段(WAITING/PROCESSING/PAUSE)';
Comment on Column ETL.SYSTEMPARA.CURY            is '当前日期年份';
Comment on Column ETL.SYSTEMPARA.CURM            is '当前日期月份';
Comment on Column ETL.SYSTEMPARA.CURD            is '当前日期日数';
Comment on Column ETL.SYSTEMPARA.CURW            is '星期值：7-周日 6-周六 1-周一';
Comment on Column ETL.SYSTEMPARA.EMONTH          is '是否月底 Y-是 N-不是';
Comment on Column ETL.SYSTEMPARA.EYEAR           is '是否年底 Y-是 N-不是';
Comment on Column ETL.SYSTEMPARA.BUFDAYS         is '缓冲层数据保存天数';
Comment on Column ETL.SYSTEMPARA.IPDDAYS         is '基础层数据保存天数';
Comment on Column ETL.SYSTEMPARA.SUMDAYS         is '汇总层数据保存天数';
Comment on Column ETL.SYSTEMPARA.RPTDAYS         is '报表层数据保存天数';
Comment on Column ETL.SYSTEMPARA.LOGDAYS         is '调度日志保存天数';



--------------------------------------------------
-- Create Table EDW.LOG_EXEC
--------------------------------------------------
Create table ETL.LOG_EXEC (
    PRCNAME                        VARCHAR(100)                    ,
    PRCTYPE                        VARCHAR(1)                    With Default '0'  ,
    TIMEID                         VARCHAR(20)                     ,
    TIMESTART                      TIMESTAMP                       ,
    TIMELOG                        TIMESTAMP                     With Default CURRENT TIMESTAMP  ,
    RESULTLEVEL                    VARCHAR(1)                    With Default '0'  ,
    SSQLCODE                       INTEGER                         ,
    RECORDNUM                      INTEGER                         ,
    SREMARK                        VARCHAR(500)                    ) 
in TBS_CFG_DATA Index in TBS_CFG_INDEX 
Compress Yes ;

Comment on Table ETL.LOG_EXEC is '过程执行日志表';
Comment on Column ETL.LOG_EXEC.PRCNAME           is '名称';
Comment on Column ETL.LOG_EXEC.PRCTYPE           is '类型 0-存储过程 1-函数 2-DataStage 3-其它';
Comment on Column ETL.LOG_EXEC.TIMEID            is '统计日期';
Comment on Column ETL.LOG_EXEC.TIMESTART         is '程序开始运行时间';
Comment on Column ETL.LOG_EXEC.TIMELOG           is '日志记录时间';
Comment on Column ETL.LOG_EXEC.RESULTLEVEL       is '告警级别：0-成功 1-异常 2-无影响异常 3-警告';
Comment on Column ETL.LOG_EXEC.SSQLCODE          is 'SQLCODE返回码';
Comment on Column ETL.LOG_EXEC.RECORDNUM         is '提交记录数';
Comment on Column ETL.LOG_EXEC.SREMARK           is '日志说明';


/**
 * 记录日志存储过程
 */
CREATE PROCEDURE "ETL"."SP_LOG_EXEC"
 (IN "I_PRC_NAME" VARCHAR(100), 
  IN "I_TIME_ID" VARCHAR(20), 
  IN "I_TIME_START" TIMESTAMP, 
  IN "I_RESULT_LEVEL" CHARACTER(1), 
  IN "I_RECORD_NUM" INTEGER, 
  IN "I_SSQLCODE" INTEGER, 
  IN "I_SREMARK" VARCHAR(500), 
  OUT "O_FLAG" INTEGER
  ) 
  SPECIFIC "ETL"."SP_LOG_EXEC"
  LANGUAGE SQL
  NOT DETERMINISTIC
  CALLED ON NULL INPUT
  EXTERNAL ACTION
  OLD SAVEPOINT LEVEL
  MODIFIES SQL DATA
  INHERIT SPECIAL REGISTERS
/***************************************************************************
  过程名：       SP_LOG_EXEC
  过程功能：     写日志表
  传入参数：
  I_PRC_NAME   	 '过程名',
  I_TIME_ID    	 '业务时间',
  I_TIME_START   '过程开始运行时间',
  I_TIME_LOG     '日志记录时间', 
  I_RESULT_LEVEL '告警级别：0-成功 1-异常 2-无影响异常 3-警告',
  I_RECORD_NUM   '提交记录数',			   
  I_SSQLCODE     'SQLCODE 代码',
  I_SREMARK      '日志说明'
  O_FLAG INTEGER  --返回过程运行状态 0-正常结束 -1 异常     
   
***************************************************************************/
BEGIN
  
 A: BEGIN ATOMIC
       DECLARE UNDO HANDLER FOR SQLEXCEPTION 
	  BEGIN
	     SET O_FLAG=99;	   
	  END;
	      
 		INSERT INTO ETL.LOG_EXEC (
 		              PRCNAME, 
									TIMEID,
									TIMESTART,
									TIMELOG,
									RESULTLEVEL,
									RECORDNUM,
									SSQLCODE,
									SREMARK)
    VALUES (
                  I_PRC_NAME,                    
									I_TIME_ID, 
									I_TIME_START,                
									CURRENT TIMESTAMP,                
									I_RESULT_LEVEL,
									I_RECORD_NUM,  
									I_SSQLCODE,                 
									I_SREMARK);			  
  	 
	   SET O_FLAG=0;
	   
  END A;
  COMMIT; 
END



