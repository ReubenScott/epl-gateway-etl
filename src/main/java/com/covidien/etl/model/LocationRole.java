package com.covidien.etl.model;

/**
 * @ClassName: LocationRole
 * @Description:
 */
public class LocationRole extends BaseModel {
    /**
     * CUSTOMER_ID.
     */
    private String CUSTOMER_ID;
    /**
     * LOCATION_ID.
     */
    private String LOCATION_ID;
    /**
     * LOCATION_ROLE.
     */
    private String LOCATION_ROLE;
    /**
     * @Title: getCUSTOMER_ID
     * @Description:
     * @return String
     */
    public final String getCUSTOMER_ID() {
        return this.CUSTOMER_ID;
    }
    /**
     * @Title: getLOCATION_ID
     * @Description:
     * @return String
     */
    public final String getLOCATION_ID() {
        return this.LOCATION_ID;
    }
    /**
     * @Title: getLOCATION_ROLE
     * @Description:
     * @return String
     */
    public final String getLOCATION_ROLE() {
        return this.LOCATION_ROLE;
    }
    /**
     * @Title: setCUSTOMER_ID
     * @Description:
     * @param cUSTOMER_ID
     * cUSTOMER_ID
     */
    public final void setCUSTOMER_ID(final String cUSTOMER_ID) {
        this.CUSTOMER_ID = cUSTOMER_ID;
    }
    /**
     * @Title: setLOCATION_ID
     * @Description:
     * @param lOCATION_ID
     * lOCATION_ID
     */
    public final void setLOCATION_ID(final String lOCATION_ID) {
        this.LOCATION_ID = lOCATION_ID;
    }
    /**
     * @Title: setLOCATION_ROLE
     * @Description:
     * @param lOCATION_ROLE
     * lOCATION_ROLE
     */
    public final void setLOCATION_ROLE(final String lOCATION_ROLE) {
        this.LOCATION_ROLE = lOCATION_ROLE;
    }
}
