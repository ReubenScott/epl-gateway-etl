package com.covidien.etl.model;

/**
 * @ClassName: LocationRole
 * @Description:
 */
public class LocationRole extends BaseModel {
    /**
     * customerId.
     */
    private String customerId;
    /**
     * locationId.
     */
    private String locationId;
    /**
     * locationRole.
     */
    private String locationRole;

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
     * @return the locationRole
     */
    public final String getLocationRole() {
        return locationRole;
    }

    /**
     * @param locationRole
     *        the locationRole to set
     */
    public final void setLocationRole(String locationRole) {
        this.locationRole = locationRole;
    }

}
