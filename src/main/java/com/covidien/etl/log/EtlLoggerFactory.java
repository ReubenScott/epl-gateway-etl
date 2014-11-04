package com.covidien.etl.log;

import com.covidien.etl.log.impl.EtlLoggerImpl;

/**
 * @ClassName: EtlLoggerFactory
 * @Description:
 */
public class EtlLoggerFactory {
    /**
     * Define a log instance.
     */
    private static EtlLogger logger = new EtlLoggerImpl();
    /**
     * @Title: getLogger
     * @Description:
     * @return EtlLogger
     */
    public static EtlLogger getLogger() {
        return logger;
    }
}
