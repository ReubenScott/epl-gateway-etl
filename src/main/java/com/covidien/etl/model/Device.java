package com.covidien.etl.model;

/**
 * @ClassName: Device
 * @Description:
 */
public class Device extends BaseModel {
    /**
     * SOURCE_SYSTEM.
     */
    private String sourceSystem;
    /**
     * SKU.
     */
    private String sku;
    /**
     * SERIAL_NUMBER.
     */
    private String serialNumber;
    /**
     * NAME.
     */
    private String name;
    /**
     * DESCRIPTION.
     */
    private String description;
    /**
     * MAINTENANCE_EXPIRATION_DATE.
     */
    private String maintenanceExpirationDate;
    /**
     * INSTALL_COUNTRY_CODE.
     */
    private String installCountryCode;
    /**
     * CUSTOMER_ID.
     */
    private String customerId;
    /**
     * LOCATION_ID.
     */
    private String locationId;
    /**
     * INSTALLATION_DATE.
     */
    private String installationDate;
    /**
     * ACTUAL_SHIP_DATE.
     */
    private String actualShipDate;

    /**
     * @return the sourceSystem
     */
    public final String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @param sourceSystem
     *        the sourceSystem to set
     */
    public final void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    /**
     * @return the sku
     */
    public final String getSku() {
        return sku;
    }

    /**
     * @param sku
     *        the sku to set
     */
    public final void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * @return the serialNumber
     */
    public final String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber
     *        the serialNumber to set
     */
    public final void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description
     *        the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the maintenanceExpirationDate
     */
    public final String getMaintenanceExpirationDate() {
        return maintenanceExpirationDate;
    }

    /**
     * @param maintenanceExpirationDate
     *        the maintenanceExpirationDate to set
     */
    public final void setMaintenanceExpirationDate(String maintenanceExpirationDate) {
        this.maintenanceExpirationDate = maintenanceExpirationDate;
    }

    /**
     * @return the installCountryCode
     */
    public final String getInstallCountryCode() {
        return installCountryCode;
    }

    /**
     * @param installCountryCode
     *        the installCountryCode to set
     */
    public final void setInstallCountryCode(String installCountryCode) {
        this.installCountryCode = installCountryCode;
    }

    /**
     * @return the customerId
     */
    public final String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId
     *        the customerId to set
     */
    public final void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the locationId
     */
    public final String getLocationId() {
        return locationId;
    }

    /**
     * @param locationId
     *        the locationId to set
     */
    public final void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * @return the installationDate
     */
    public final String getInstallationDate() {
        return installationDate;
    }

    /**
     * @param installationDate
     *        the installationDate to set
     */
    public final void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    /**
     * @return the actualShipDate
     */
    public final String getActualShipDate() {
        return actualShipDate;
    }

    /**
     * @param actualShipDate
     *        the actualShipDate to set
     */
    public final void setActualShipDate(String actualShipDate) {
        this.actualShipDate = actualShipDate;
    }

}
