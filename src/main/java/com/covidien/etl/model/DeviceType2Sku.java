package com.covidien.etl.model;

/**
 * @ClassName: DeviceType2Sku
 * @Description:
 */
public class DeviceType2Sku {
    /**
     * deviceType.
     */
    private String deviceType;
    /**
     * sourceSystem.
     */
    private String sourceSystem;
    /**
     * skus.
     */
    private String skus;
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
     * @Title: getSourceSystem
     * @Description:
     * @return String
     */
    public final String getSourceSystem() {
        return this.sourceSystem;
    }
    /**
     * @Title: setSourceSystem
     * @Description:
     * @param sourceSystem
     * sourceSystem
     */
    public final void setSourceSystem(final String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    /**
     * @Title: getSkus
     * @Description:
     * @return String
     */
    public final String getSkus() {
        return this.skus;
    }
    /**
     * @Title: setSkus
     * @Description:
     * @param skus
     * skus
     */
    public final void setSkus(final String skus) {
        this.skus = skus;
    }

}
