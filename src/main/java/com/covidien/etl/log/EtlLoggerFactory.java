package com.covidien.etl.log;

import com.covidien.etl.log.impl.EtlLoggerImpl;

/**
 * @ClassName: EtlLoggerFactory
 * @Description:
 */
public final class EtlLoggerFactory {
    /**
     * @Title: EtlLoggerFactory
     * @Description:
     */
    private EtlLoggerFactory() {
    }

    /**
     * Defined a multiple threads log instance.
     * 
     * @Title: getLogger
     * @return EtlLogger
     */
    public static EtlLogger getLogger() {
        return EtlLoggerImpl.getInstance();
    }
}
