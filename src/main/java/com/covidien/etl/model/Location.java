package com.covidien.etl.model;

/**
 * @ClassName: Location
 * @Description:
 */
public class Location extends BaseModel {
    /**
     * locationId.
     */
    private String locationId;
    /**
     * addressLine1.
     */
    private String addressLine1;
    /**
     * addressModifier1.
     */
    private String addressModifier1;
    /**
     * addressModifier2.
     */
    private String addressModifier2;
    /**
     * addressModifier3.
     */
    private String addressModifier3;
    /**
     * addressModifier4.
     */
    private String addressModifier4;
    /**
     * CITY.
     */
    private String city;
    /**
     * stateProvince.
     */
    private String stateProvince;
    /**
     * postalCode.
     */
    private String postalCode;
    /**
     * countryCode.
     */
    private String countryCode;

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
     * @return the addressLine1
     */
    public final String getAddressLine1() {
        return addressLine1;
    }

    /**
     * @param addressLine1
     *        the addressLine1 to set
     */
    public final void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * @return the addressModifier1
     */
    public final String getAddressModifier1() {
        return addressModifier1;
    }

    /**
     * @param addressModifier1
     *        the addressModifier1 to set
     */
    public final void setAddressModifier1(String addressModifier1) {
        this.addressModifier1 = addressModifier1;
    }

    /**
     * @return the addressModifier2
     */
    public final String getAddressModifier2() {
        return addressModifier2;
    }

    /**
     * @param addressModifier2
     *        the addressModifier2 to set
     */
    public final void setAddressModifier2(String addressModifier2) {
        this.addressModifier2 = addressModifier2;
    }

    /**
     * @return the addressModifier3
     */
    public final String getAddressModifier3() {
        return addressModifier3;
    }

    /**
     * @param addressModifier3
     *        the addressModifier3 to set
     */
    public final void setAddressModifier3(String addressModifier3) {
        this.addressModifier3 = addressModifier3;
    }

    /**
     * @return the addressModifier4
     */
    public final String getAddressModifier4() {
        return addressModifier4;
    }

    /**
     * @param addressModifier4
     *        the addressModifier4 to set
     */
    public final void setAddressModifier4(String addressModifier4) {
        this.addressModifier4 = addressModifier4;
    }

    /**
     * @return the city
     */
    public final String getCity() {
        return city;
    }

    /**
     * @param city
     *        the city to set
     */
    public final void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the stateProvince
     */
    public final String getStateProvince() {
        return stateProvince;
    }

    /**
     * @param stateProvince
     *        the stateProvince to set
     */
    public final void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    /**
     * @return the postalCode
     */
    public final String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode
     *        the postalCode to set
     */
    public final void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the countryCode
     */
    public final String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode
     *        the countryCode to set
     */
    public final void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
