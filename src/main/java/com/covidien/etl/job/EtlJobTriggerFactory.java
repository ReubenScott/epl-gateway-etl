package com.covidien.etl.job;

import com.covidien.etl.job.impl.EtlJobTriggerImpl;

/**
 * @ClassName: EtlJobTriggerFactory
 * @Description:
 */
public final class EtlJobTriggerFactory {
    /**
     * @Title: EtlJobTriggerFactory
     * @Description:
     */
    private EtlJobTriggerFactory() {
    }
    /**
     * etlJobTrigger.
     */
    private static EtlJobTrigger etlJobTrigger;
    /**
     * @Title: createEtlJobTrigger
     * @Description:
     * @return EtlJobTrigger
     */
    public static EtlJobTrigger createEtlJobTrigger() {
        etlJobTrigger = new EtlJobTriggerImpl();
        return etlJobTrigger;
    }
}
