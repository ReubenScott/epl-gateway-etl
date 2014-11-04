package com.covidien.etl.model;

/**
 * @ClassName: Location
 * @Description:
 */
public class Location extends BaseModel {
    /**
     * LOCATION_ID.
     */
    private String LOCATION_ID;
    /**
     * ADDRESS_LINE1.
     */
    private String ADDRESS_LINE1;
    /**
     * ADDRESS_MODIFIER_1.
     */
    private String ADDRESS_MODIFIER_1;
    /**
     * ADDRESS_MODIFIER_2.
     */
    private String ADDRESS_MODIFIER_2;
    /**
     * ADDRESS_MODIFIER_3.
     */
    private String ADDRESS_MODIFIER_3;
    /**
     * ADDRESS_MODIFIER_4.
     */
    private String ADDRESS_MODIFIER_4;
    /**
     * CITY.
     */
    private String CITY;
    /**
     * STATE_PROVINCE.
     */
    private String STATE_PROVINCE;
    /**
     * POSTAL_CODE.
     */
    private String POSTAL_CODE;
    /**
     * COUNTRY_CODE.
     */
    private String COUNTRY_CODE;
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
     * @Title: setADDRESS_LINE1
     * @Description:
     * @param aDDRESS_LINE1
     * aDDRESS_LINE1
     */
    public final void setADDRESS_LINE1(final String aDDRESS_LINE1) {
        this.ADDRESS_LINE1 = aDDRESS_LINE1;
    }
    /**
     * @Title: setADDRESS_MODIFIER_1
     * @Description:
     * @param aDDRESS_MODIFIER_1
     * aDDRESS_MODIFIER_1
     */
    public final void setADDRESS_MODIFIER_1(final String aDDRESS_MODIFIER_1) {
        this.ADDRESS_MODIFIER_1 = aDDRESS_MODIFIER_1;
    }
    /**
     * @Title: setADDRESS_MODIFIER_2
     * @Description:
     * @param aDDRESS_MODIFIER_2
     * aDDRESS_MODIFIER_2
     */
    public final void setADDRESS_MODIFIER_2(final String aDDRESS_MODIFIER_2) {
        this.ADDRESS_MODIFIER_2 = aDDRESS_MODIFIER_2;
    }
    /**
     * @Title: setADDRESS_MODIFIER_3
     * @Description:
     * @param aDDRESS_MODIFIER_3
     * aDDRESS_MODIFIER_3
     */
    public final void setADDRESS_MODIFIER_3(final String aDDRESS_MODIFIER_3) {
        this.ADDRESS_MODIFIER_3 = aDDRESS_MODIFIER_3;
    }
    /**
     * @Title: setADDRESS_MODIFIER_4
     * @Description:
     * @param aDDRESS_MODIFIER_4
     * aDDRESS_MODIFIER_4
     */
    public final void setADDRESS_MODIFIER_4(final String aDDRESS_MODIFIER_4) {
        this.ADDRESS_MODIFIER_4 = aDDRESS_MODIFIER_4;
    }
    /**
     * @Title: setCITY
     * @Description:
     * @param cITY
     * cITY
     */
    public final void setCITY(final String cITY) {
        this.CITY = cITY;
    }
    /**
     * @Title: setSTATE_PROVINCE
     * @Description:
     * @param sTATE_PROVINCE
     * sTATE_PROVINCE
     */
    public final void setSTATE_PROVINCE(final String sTATE_PROVINCE) {
        this.STATE_PROVINCE = sTATE_PROVINCE;
    }
    /**
     * @Title: setPOSTAL_CODE
     * @Description:
     * @param pOSTAL_CODE
     * pOSTAL_CODE
     */
    public final void setPOSTAL_CODE(final String pOSTAL_CODE) {
        this.POSTAL_CODE = pOSTAL_CODE;
    }
    /**
     * @Title: setCOUNTRY_CODE
     * @Description:
     * @param cOUNTRY_CODE
     * cOUNTRY_CODE
     */
    public final void setCOUNTRY_CODE(final String cOUNTRY_CODE) {
        this.COUNTRY_CODE = cOUNTRY_CODE;
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
     * @Title: getADDRESS_LINE1
     * @Description:
     * @return String
     */
    public final String getADDRESS_LINE1() {
        return this.ADDRESS_LINE1;
    }
    /**
     * @Title: getADDRESS_MODIFIER_1
     * @Description:
     * @return String
     */
    public final String getADDRESS_MODIFIER_1() {
        return this.ADDRESS_MODIFIER_1;
    }
    /**
     * @Title: getADDRESS_MODIFIER_2
     * @Description:
     * @return String
     */
    public final String getADDRESS_MODIFIER_2() {
        return ADDRESS_MODIFIER_2;
    }
    /**
     * @Title: getADDRESS_MODIFIER_3
     * @Description:
     * @return String
     */
    public final String getADDRESS_MODIFIER_3() {
        return this.ADDRESS_MODIFIER_3;
    }
    /**
     * @Title: getADDRESS_MODIFIER_4
     * @Description:
     * @return String
     */
    public final String getADDRESS_MODIFIER_4() {
        return this.ADDRESS_MODIFIER_4;
    }
    /**
     * @Title: getCITY
     * @Description:
     * @return String
     */
    public final String getCITY() {
        return this.CITY;
    }
    /**
     * @Title: getSTATE_PROVINCE
     * @Description:
     * @return String
     */
    public final String getSTATE_PROVINCE() {
        return this.STATE_PROVINCE;
    }
    /**
     * @Title: getPOSTAL_CODE
     * @Description:
     * @return String
     */
    public final String getPOSTAL_CODE() {
        return this.POSTAL_CODE;
    }
    /**
     * @Title: getCOUNTRY_CODE
     * @Description:
     * @return String
     */
    public final String getCOUNTRY_CODE() {
        return this.COUNTRY_CODE;
    }

}
