package com.covidien.etl.dbstore;

/**
 * @ClassName: DBSetup
 * @Description:
 */
public final class DBSetup {
    /**
     * @Title: DBSetup
     * @Description:
     */
    private DBSetup() {
    }

    /**
     * TABLE1.
     */
    public static final String TABLE1 = "CREATE TABLE if not exists customer_responce_status (nid int(10) "
            + "unsigned DEFAULT NULL, customer_id varchar(30) DEFAULT NULL, "
            + "status varchar(15) DEFAULT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8";
    /**
     * TABLE2.
     */
    public static final String TABLE2 = "CREATE TABLE if not exists  location_role_responce_status "
            + "(nid int(10) unsigned DEFAULT NULL, customer_id varchar(30) DEFAULT NULL, location_id varchar(30) "
            + "DEFAULT NULL, status varchar(15) DEFAULT NULL ) ENGINE=MyISAM DEFAULT CHARSET=utf8";
    /**
     * TABLE3.
     */
    public static final String TABLE3 = "CREATE TABLE if not exists  location_responce_status (nid int(10) "
            + "unsigned DEFAULT NULL, location_id varchar(30) DEFAULT NULL, "
            + "status varchar(15) DEFAULT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8";
    /**
     * TABLE4.
     */
    public static final String TABLE4 = "CREATE TABLE if not exists  device_responce_status (nid int(10) "
            + "(nid int(10) unsigned serial_no varchar(30) DEFAULT NULL, status varchar(15) DEFAULT NULL)  "
            + "ENGINE=MyISAM DEFAULT CHARSET=utf8";
    /**
     * TABLE_XREF.
     */
    public static final String TABLE_XREF = "CREATE TABLE if not exists  xref (id int(10) unsigned NOT NULL "
            + "PRIMARY KEY AUTO_INCREMENT,nid int(10) unsigned DEFAULT NULL UNIQUE KEY, "
            + "ps_id varchar(100) DEFAULT NULL, type int(1) unsigned DEFAULT NULL,ps_last_change_time double,"
            + "created_time double,updated_time double, KEY xref_type_ps_id (type,ps_id),"
            + "KEY xref_type_nid (type,nid), KEY xref_ps_id (ps_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8";
    /**
     * TABLE XREF INDEX.
     */
    public static final String TABLE_XREF_INDEX_1 = "CREATE INDEX xref_type_ps_id ON xref (type, ps_id)";
    /**
     * TABLE XREF INDEX.
     */
    public static final String TABLE_XREF_INDEX_2 = "CREATE INDEX xref_type_nid ON xref (type, nid)";
    /**
     * TABLE XREF INDEX.
     */
    public static final String TABLE_XREF_INDEX_3 = "CREATE INDEX xref_ps_id ON xref (ps_id)";
    /**
     * TABLE_XREF_HISTORY.
     */
    public static final String TABLE_XREF_HISTORY = "CREATE TABLE if not exists  xref_history "
            + "(id int(10) unsigned NOT NULL ,nid int(10) unsigned DEFAULT NULL , "
            + "ps_id varchar(30) DEFAULT NULL, type int(1) unsigned DEFAULT NULL,ps_last_change_time double,"
            + "created_time double,updated_time double,KEY xref_type_ps_id (type,ps_id),"
            + "KEY xref_type_nid (type,nid), KEY xref_ps_id (ps_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8";
    /**
     * INDEX2.
     */
    public static final String INDEX2 = "create index location_responce_status_location_id"
            + " on location_responce_status(location_id);";
    /**
     * INDEX1.
     */
    public static final String INDEX1 = "create index customer_responce_status_customer_id "
            + "on customer_responce_status(customer_id)";
    /**
     * ALTER1.
     */
    public static final String ALTER1 = "alter table device_responce_status add column sku varchar(30) DEFAULT NULL";
    /**
     * CHECK_TABLE_DRS.
     */
    public static final String CHECK_TABLE_DRS = "desc device_responce_status";

}
