package com.covidien.etl.job;

import com.covidien.etl.dbstore.DBInit;

/**
 * @ClassName: EtlMain
 * @Description:
 */
public class EtlMain {
    /**
     * @Title: main
     * @Description:
     * @param args
     * args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        // if (args.length == 1) {
        // if ("--skuInit".equals(args[0])) {
        // dbInit.init();
        // return;
        // }
        // }

        if (args.length > 0) {
            DBInit dbInit = new DBInit();
            for (String str : args) {
                if ("--skuInit".equals(str)) {
                    dbInit.skuInit();
                } else if ("--snInit".equals(str)) {
                    dbInit.snInit();
                }
            }
            return;
        }
        EtlJobTrigger trigger = EtlJobTriggerFactory.createEtlJobTrigger();
        trigger.trigger();
    }
}
