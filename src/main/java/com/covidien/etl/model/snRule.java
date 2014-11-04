package com.covidien.etl.model;

/**
 * @ClassName: snRule
 * @Description:
 */
public class SNRule {
    /**
     * deviceType
     */
    private String deviceType;
    /**
     * snRegx.
     */
    private String snRegx;
    /**
     * @Title: getDeviceType
     * @Description:
     * @return String
     */
    public final String getDeviceType() {
        return this.deviceType;
    }
    /**
     * @Title: setDeviceType
     * @Description:
     * @param deviceType
     * deviceType
     */
    public final void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }
    /**
     * @Title: getSnRegx
     * @Description:
     * @return String
     */
    public final String getSnRegx() {
        return this.snRegx;
    }
    /**
     * @Title: setSnRegx
     * @Description:
     * @param snRegx
     * snRegx
     */
    public final void setSnRegx(final String snRegx) {
        this.snRegx = snRegx;
    }
}
