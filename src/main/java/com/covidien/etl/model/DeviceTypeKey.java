package com.covidien.etl.model;

/**
 * @ClassName: DeviceTypeKey
 * @Description:
 */
public class DeviceTypeKey {
    /**
     * @Title: DeviceTypeKey
     * @Description:
     * @param sku
     * sku
     * @param sourceSystem
     * sourceSystem
     * @param serailNumberValidation
     * serailNumberValidation
     */
    public DeviceTypeKey(final String sku, final String sourceSystem, final String serailNumberValidation) {
        this.sku = sku;
        this.sourceSystem = sourceSystem;
        this.serailNumberValidation = serailNumberValidation;
    }
    /**
     * sku.
     */
    private String sku;
    /**
     * sourceSystem.
     */
    private String sourceSystem;
    /**
     * serailNumberValidation.
     */
    private String serailNumberValidation;
    /**
     * @Title: getSku
     * @Description:
     * @return String
     */
    public final String getSku() {
        return this.sku;
    }
    /**
     * @Title: setSku
     * @Description:
     * @param sku
     * sku
     */
    public final void setSku(final String sku) {
        this.sku = sku;
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
     * @Title: getSerailNumberValidation
     * @Description:
     * @return String
     */
    public final String getSerailNumberValidation() {
        return this.serailNumberValidation;
    }
    /**
     * @Title: setSerailNumberValidation
     * @Description:
     * @param serailNumberValidation
     * serailNumberValidation
     */
    public final void setSerailNumberValidation(final String serailNumberValidation) {
        this.serailNumberValidation = serailNumberValidation;
    }
}
