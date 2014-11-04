/**
 * @Title: Test.java
 * @Package com.covidien.etl.common
 * @Description:
 * @author tony.zhang2
 * @date 2013-12-14
 * @version V2.0
 */
package com.covidien.etl.common;

/**
 * @ClassName: EtlException
 * @Description:
 */
public class EtlException extends Exception {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * @Title: EtlException
     * @Description:
     * @param exception
     * exception
     */
    public EtlException(final String exception) {
        super(exception);
    }
}
