--------------------------------------------------
-- Create Table EDW.F_DEP_SHACNACN
--------------------------------------------------
Create table EDW.F_DEP_SHACNACN (
    ETLDT                          VARCHAR(10)         NOT NULL    ,
    SHACN_LL                       DECIMAL(4)          NOT NULL    ,
    SH_ACCT_NO                     VARCHAR(28)         NOT NULL    ,
    SHACN_DB_TIMESTAMP             DECIMAL(15)                     ,
    SH_ACCT_CHAR                   VARCHAR(1)                      ,
    SH_PDP_CODE                    VARCHAR(11)                     ,
    SH_AVAL_DT                     VARCHAR(10)                     ,
    SH_CUST_NO                     VARCHAR(20)                     ,
    SH_CUST_NAME                   VARCHAR(122)                    ,
    SH_DRW_TYP                     VARCHAR(4)                      ,
    SH_PRDS_INSTN_DPDW_FLG         VARCHAR(1)                      ,
    SH_DPDW_RANG                   VARCHAR(1)                      ,
    SH_INTP_FLG                    DECIMAL(1)                      ,
    SH_VIP_ACCT_FLG                VARCHAR(1)                      ,
    SH_AVAL_TRN_DT                 VARCHAR(10)                     ,
    SH_DDP_ACCT_STS                VARCHAR(1)                      ,
    SH_DDP_ACCT_CTL_STS            VARCHAR(1)                      ,
    SH_PSBK_NO                     VARCHAR(3)                      ,
    SH_PSBK_DL_DT                  VARCHAR(10)                     ,
    SH_PSBK_PRT_NO                 VARCHAR(13)                     ,
    SH_PSBK_STS                    VARCHAR(1)                      ,
    SH_DET_ITEM_1                  DECIMAL(7)                      ,
    SH_DET_ITEM_2                  DECIMAL(7)                      ,
    SH_DET_ITEM_3                  DECIMAL(7)                      ,
    SH_PGLN_TOTL_1_PG              DECIMAL(2)                      ,
    SH_PGLN_TOTL_1_LN              DECIMAL(2)                      ,
    SH_PGLN_TOTL_2_PG              DECIMAL(2)                      ,
    SH_PGLN_TOTL_2_LN              DECIMAL(2)                      ,
    SH_PGLN_TOTL_3_PG              DECIMAL(2)                      ,
    SH_PGLN_TOTL_3_LN              DECIMAL(2)                      ,
    SH_AENTR_DET_TOTL_1            DECIMAL(7)                      ,
    SH_AENTR_DET_TOTL_2            DECIMAL(7)                      ,
    SH_AENTR_DET_TOTL_3            DECIMAL(7)                      ,
    SH_PRINTED_MAX_NO_1            DECIMAL(7)                      ,
    SH_PRINTED_MAX_NO_2            DECIMAL(7)                      ,
    SH_PRINTED_MAX_NO_3            DECIMAL(7)                      ,
    SH_INT_ACCT_NO                 VARCHAR(28)                     ,
    SH_ZG_LTM_ACCT_BAL             DECIMAL(15,2)                   ,
    SH_ZG_LTM_DDP_PDT              DECIMAL(15,2)                   ,
    SH_TZ_LTM_ACCT_BAL             DECIMAL(15,2)                   ,
    SH_TZ_LTM_DDP_PDT              DECIMAL(15,2)                   ,
    SH_LTM_DDP_DT                  VARCHAR(10)                     ,
    SH_LTM_REC_DT                  VARCHAR(10)                     ,
    SH_PSWD_ERR_TIMES              DECIMAL(1)                      ,
    SH_QPSWD                       VARCHAR(6)                      ,
    SH_PSWD_ERR_DT                 VARCHAR(6)                      ,
    SH_ENC_TYP                     VARCHAR(1)                      ,
    SH_ENCKEY_VER                  VARCHAR(3)                      ,
    SH_PSWD_DL_DT                  VARCHAR(10)                     ,
    SH_PSWD_STS                    VARCHAR(1)                      ,
    SH_LEGAL_INSTN_NO              VARCHAR(9)                      ,
    SH_OPAC_INSTN_NO               VARCHAR(9)                      ,
    SH_OPAC_DT                     VARCHAR(10)                     ,
    SH_OPAC_TLR_NO                 VARCHAR(12)                     ,
    SH_CACCT_TLR_NO                VARCHAR(12)                     ,
    SH_CACCT_INSTN_NO              VARCHAR(9)                      ,
    SH_CACCT_DT                    VARCHAR(10)                     ,
    SH_OPAC_PERM_NO                VARCHAR(15)                     ,
    SH_CERT_TYP                    VARCHAR(1)                      ,
    SH_CERT_ID                     VARCHAR(18)                     ,
    SH_1LVL_BRH_ID                 VARCHAR(3)                      ,
    SH_DB_PART_ID                  VARCHAR(9)                      ,
    SH_CRPT_PIN                    VARCHAR(16)                     ,
    SH_ACN_MEMO                    VARCHAR(44)                     ,
    ETLCYCLEDATE                   VARCHAR(10)         NOT NULL    ) 
in TBS_IPD_DATA Index in TBS_IPD_INDEX  
Partitioning Key (SH_ACCT_NO,SHACN_LL) Using Hashing;
Comment on Table EDW.F_DEP_SHACNACN is '股金主档';
Comment on Column EDW.F_DEP_SHACNACN.ETLDT       is '数据下发日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_ACCT_NO  is '账号';
Comment on Column EDW.F_DEP_SHACNACN.SH_ACCT_CHAR is '股东性质';
Comment on Column EDW.F_DEP_SHACNACN.SH_PDP_CODE is '产品代码';
Comment on Column EDW.F_DEP_SHACNACN.SH_AVAL_DT  is '起用日期(AVAL)';
Comment on Column EDW.F_DEP_SHACNACN.SH_CUST_NO  is '客户编号';
Comment on Column EDW.F_DEP_SHACNACN.SH_CUST_NAME is '客户姓名';
Comment on Column EDW.F_DEP_SHACNACN.SH_DRW_TYP  is '支取方式';
Comment on Column EDW.F_DEP_SHACNACN.SH_PRDS_INSTN_DPDW_FLG is '约定机构存取标志';
Comment on Column EDW.F_DEP_SHACNACN.SH_DPDW_RANG is '通兑范围';
Comment on Column EDW.F_DEP_SHACNACN.SH_INTP_FLG is '红利冻结次数';
Comment on Column EDW.F_DEP_SHACNACN.SH_VIP_ACCT_FLG is '重要股东标志';
Comment on Column EDW.F_DEP_SHACNACN.SH_AVAL_TRN_DT is '起始可转让日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_DDP_ACCT_STS is '账户状态；';
Comment on Column EDW.F_DEP_SHACNACN.SH_DDP_ACCT_CTL_STS is '账户控制状态';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSBK_NO  is '股金存折册号';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSBK_DL_DT is '股金存折挂失日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSBK_PRT_NO is '股金存折印刷号';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSBK_STS is '股金存折状态';
Comment on Column EDW.F_DEP_SHACNACN.SH_DET_ITEM_1 is '资格股交易记录编号';
Comment on Column EDW.F_DEP_SHACNACN.SH_DET_ITEM_2 is '投资股交易记录编号';
Comment on Column EDW.F_DEP_SHACNACN.SH_DET_ITEM_3 is '分红交易记录编号';
Comment on Column EDW.F_DEP_SHACNACN.SH_PGLN_TOTL_1_PG is '资格股页数';
Comment on Column EDW.F_DEP_SHACNACN.SH_PGLN_TOTL_1_LN is '资格股行数';
Comment on Column EDW.F_DEP_SHACNACN.SH_PGLN_TOTL_2_PG is '投资股页数';
Comment on Column EDW.F_DEP_SHACNACN.SH_PGLN_TOTL_2_LN is '投资股行数';
Comment on Column EDW.F_DEP_SHACNACN.SH_PGLN_TOTL_3_PG is '分红记录页数';
Comment on Column EDW.F_DEP_SHACNACN.SH_PGLN_TOTL_3_LN is '分红记录行数';
Comment on Column EDW.F_DEP_SHACNACN.SH_AENTR_DET_TOTL_1 is '资格股未登折明细数';
Comment on Column EDW.F_DEP_SHACNACN.SH_AENTR_DET_TOTL_2 is '投资股未登折明细数';
Comment on Column EDW.F_DEP_SHACNACN.SH_AENTR_DET_TOTL_3 is '分红记录未登折明细数';
Comment on Column EDW.F_DEP_SHACNACN.SH_PRINTED_MAX_NO_1 is '资格股已打印最大序号';
Comment on Column EDW.F_DEP_SHACNACN.SH_PRINTED_MAX_NO_2 is '投资股已打印最大序号';
Comment on Column EDW.F_DEP_SHACNACN.SH_PRINTED_MAX_NO_3 is '分红记录已打印最大序号';
Comment on Column EDW.F_DEP_SHACNACN.SH_INT_ACCT_NO is '股金分红账号';
Comment on Column EDW.F_DEP_SHACNACN.SH_ZG_LTM_ACCT_BAL is '资格股上年度股金股数';
Comment on Column EDW.F_DEP_SHACNACN.SH_ZG_LTM_DDP_PDT is '资格股上年度分红积数';
Comment on Column EDW.F_DEP_SHACNACN.SH_TZ_LTM_ACCT_BAL is '投资股上年度股金股数';
Comment on Column EDW.F_DEP_SHACNACN.SH_TZ_LTM_DDP_PDT is '投资股上年度分红积数';
Comment on Column EDW.F_DEP_SHACNACN.SH_LTM_DDP_DT is '上次分红日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_LTM_REC_DT is '上次股权登记日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSWD_ERR_TIMES is '密码出错次数(9型)';
Comment on Column EDW.F_DEP_SHACNACN.SH_QPSWD    is '查询密码';
Comment on Column EDW.F_DEP_SHACNACN.SH_ENC_TYP  is '密码加密方式';
Comment on Column EDW.F_DEP_SHACNACN.SH_ENCKEY_VER is '密钥版本';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSWD_DL_DT is '密码挂失日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_PSWD_STS is '密码状态';
Comment on Column EDW.F_DEP_SHACNACN.SH_LEGAL_INSTN_NO is '法人机构号';
Comment on Column EDW.F_DEP_SHACNACN.SH_OPAC_INSTN_NO is '开户机构号';
Comment on Column EDW.F_DEP_SHACNACN.SH_OPAC_DT  is '开户日期';
Comment on Column EDW.F_DEP_SHACNACN.SH_OPAC_TLR_NO is '开户柜员号';
Comment on Column EDW.F_DEP_SHACNACN.SH_CACCT_TLR_NO is '销户柜员号';
Comment on Column EDW.F_DEP_SHACNACN.SH_CACCT_INSTN_NO is '销户机构号';
Comment on Column EDW.F_DEP_SHACNACN.SH_CACCT_DT is '销户日期(CACCT)';
Comment on Column EDW.F_DEP_SHACNACN.SH_OPAC_PERM_NO is '开户许可证号';
Comment on Column EDW.F_DEP_SHACNACN.SH_CERT_TYP is '证件种类';
Comment on Column EDW.F_DEP_SHACNACN.SH_CERT_ID  is '证件号';
Comment on Column EDW.F_DEP_SHACNACN.SH_1LVL_BRH_ID is '法人号';
Comment on Column EDW.F_DEP_SHACNACN.SH_DB_PART_ID is '分区键';
Comment on Column EDW.F_DEP_SHACNACN.SH_CRPT_PIN is '密码密文';
Comment on Column EDW.F_DEP_SHACNACN.SH_ACN_MEMO is '扩展字段';
Comment on Column EDW.F_DEP_SHACNACN.ETLCYCLEDATE is 'ETL处理日期';
--ceshi
create table my_test (
 id varchar(32) primary key ,
 name varchar(50) ,
 age int ,
 comment varchar(100)
) 