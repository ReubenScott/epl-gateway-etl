/**
 * @Title: Constant.java
 * @Package com.covidien.etl.common
 * @Description:
 * @author tony.zhang2
 * @date 2013-12-14
 * @version V2.0
 */
package com.covidien.etl.common;

import java.util.Properties;

/**
 * @ClassName: Constant
 * @Description:
 */
public final class Constant {
    /**
     * @Title: Constant
     * @Description:
     */
    private Constant() {
    }

    /**
     * Customer SQL log Constant.
     */
    public static final String CUSTOMER_FAIL_INSERT = "CUSTOMER_FAIL_INSERT";
    /**
     * Customer SQL log Constant.
     */
    public static final String CUSTOMER_SUCCESS_INSERT = "CUSTOMER_SUCCESS_INSERT";
    /**
     * Customer SQL log Constant.
     */
    public static final String CUSTOMER_SUCCESS_UPDATE = "CUSTOMER_SUCCESS_UPDATE";
    /**
     * Customer SQL log Constant.
     */
    public static final String CUSTOMER_FAIL_UPDATE = "CUSTOMER_FAIL_UPDATE";
    /**
     * Customer SQL log Constant.
     */
    public static final String CUSTOMER_SUCCESS_DELETE = "CUSTOMER_SUCCESS_DELETE";
    /**
     * Customer SQL log Constant.
     */
    public static final String CUSTOMER_FAIL_DELETE = "CUSTOMER_FAIL_DELETE";

    /**
     * LOCATION SQL log Constant.
     */
    public static final String LOCATION_FAIL_INSERT = "LOCATION_FAIL_INSERT";
    /**
     * LOCATION SQL log Constant.
     */
    public static final String LOCATION_SUCCESS_INSERT = "LOCATION_SUCCESS_INSERT";
    /**
     * LOCATION SQL log Constant.
     */
    public static final String LOCATION_SUCCESS_UPDATE = "LOCATION_SUCCESS_UPDATE";
    /**
     * Customer SQL log Constant.
     */
    public static final String LOCATION_FAIL_UPDATE = "LOCATION_FAIL_UPDATE";
    /**
     * LOCATION SQL log Constant.
     */
    public static final String LOCATION_SUCCESS_DELETE = "LOCATION_SUCCESS_DELETE";
    /**
     * LOCATION SQL log Constant.
     */
    public static final String LOCATION_FAIL_DELETE = "LOCATION_FAIL_DELETE";

    /**
     * LOCATION_ROLE SQL log Constant.
     */
    public static final String LOCATION_ROLE_FAIL_INSERT = "LOCATION_ROLE_FAIL_INSERT";
    /**
     * LOCATION_ROLE SQL log Constant.
     */
    public static final String LOCATION_ROLE_SUCCESS_INSERT = "LOCATION_ROLE_SUCCESS_INSERT";
    /**
     * LOCATION_ROLE SQL log Constant.
     */
    public static final String LOCATION_ROLE_SUCCESS_UPDATE = "LOCATION_ROLE_SUCCESS_UPDATE";
    /**
     * LOCATION_ROLE SQL log Constant.
     */
    public static final String LOCATION_ROLE_FAIL_UPDATE = "LOCATION_ROLE_FAIL_UPDATE";
    /**
     * LOCATION_ROLE SQL log Constant.
     */
    public static final String LOCATION_ROLE_SUCCESS_DELETE = "LOCATION_ROLE_SUCCESS_DELETE";
    /**
     * LOCATION_ROLE SQL log Constant.
     */
    public static final String LOCATION_ROLE_FAIL_DELETE = "LOCATION_ROLE_FAIL_DELETE";

    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_FAIL_INSERT = "DEVICE_FAIL_INSERT";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_SUCCESS_INSERT = "DEVICE_SUCCESS_INSERT";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_SUCCESS_UPDATE = "DEVICE_SUCCESS_UPDATE";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_FAIL_UPDATE = "DEVICE_FAIL_UPDATE";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_SUCCESS_DELETE = "DEVICE_SUCCESS_DELETE";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_FAIL_DELETE = "DEVICE_FAIL_DELETE";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_FAIL_SKU_VALIDATE = "DEVICE_FAIL_SKU_VALIDATE";
    /**
     * DEVICE SQL log Constant.
     */
    public static final String DEVICE_FAIL_SN_VALIDATE = "DEVICE_FAIL_SN_VALIDATE";
    /**
     * CSV_DELIMTIER_PIPE.
     */
    public static final int CSV_DELIMTIER_PIPE = 124;
    /**
     * CSV_DELIMTIER_COMMA.
     */
    public static final int CSV_DELIMTIER_COMMA = 44;
    /**
     * properties.
     */
    private static Properties properties = PropertyReader.getInstance().readResource("config.properties");
    /**
     * Configuration files path.
     */
    private static final String CONFIG_FILE_PATH = properties.getProperty("configFilePath");

    /**
     * @Title: getConfigFilePath
     * @Description:
     * @return String, Configuration files path.
     */
    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH;
    }

    /**
     * CSV_SIZE_KEY.
     */
    public static final String CSV_SIZE_KEY = "SIZE";

    /**
     * CSV_BATCH_NUMBER_KEY.
     */
    public static final String CSV_BATCH_NUMBER_KEY = "BATCH_NUMBER";
}
