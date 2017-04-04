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
ALTER TABLE etl.buf_metadata ADD PRIMARY KEY (schemata, stbname);
COMMENT ON TABLE etl.buf_metadata  IS 'EDW源系统文件列表';


