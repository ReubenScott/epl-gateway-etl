package com.covidien.etl.job;

import com.covidien.etl.job.impl.EtlJobTriggerImpl;

/**
 * @ClassName: EtlJobTriggerFactory
 * @Description:
 */
public class EtlJobTriggerFactory {
    /**
     * etlJobTrigger.
     */
    private static EtlJobTrigger etlJobTrigger;
    /**
     * @Title: createEtlJobTrigger
     * @Description:
     * @return EtlJobTrigger
     */
    public static final EtlJobTrigger createEtlJobTrigger() {
        etlJobTrigger = new EtlJobTriggerImpl();
        return etlJobTrigger;
    }
}
