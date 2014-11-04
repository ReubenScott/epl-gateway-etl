package com.covidien.etl.model;

/**
 * @ClassName: Device
 * @Description:
 */
public class Device extends BaseModel {
    /**
     * SOURCE_SYSTEM.
     */
    private String SOURCE_SYSTEM;
    /**
     * SKU.
     */
    private String SKU;
    /**
     * SERIAL_NUMBER.
     */
    private String SERIAL_NUMBER;
    /**
     * NAME.
     */
    private String NAME;
    /**
     * DESCRIPTION.
     */
    private String DESCRIPTION;
    /**
     * MAINTENANCE_EXPIRATION_DATE.
     */
    private String MAINTENANCE_EXPIRATION_DATE;
    /**
     * INSTALL_COUNTRY_CODE.
     */
    private String INSTALL_COUNTRY_CODE;
    /**
     * CUSTOMER_ID.
     */
    private String CUSTOMER_ID;
    /**
     * LOCATION_ID.
     */
    private String LOCATION_ID;
    /**
     * INSTALLATION_DATE.
     */
    private String INSTALLATION_DATE;
    /**
     * ACTUAL_SHIP_DATE.
     */
    private String ACTUAL_SHIP_DATE;
    /**
     * @Title: getSOURCE_SYSTEM
     * @Description:
     * @return String
     */
    public final String getSOURCE_SYSTEM() {
        return this.SOURCE_SYSTEM;
    }
    /**
     * @Title: getSKU
     * @Description:
     * @return String
     */
    public final String getSKU() {
        return this.SKU;
    }
    /**
     * @Title: getSERIAL_NUMBER
     * @Description:
     * @return String
     */
    public final String getSERIAL_NUMBER() {
        return SERIAL_NUMBER;
    }
    /**
     * @Title: getNAME
     * @Description:
     * @return String
     */
    public final String getNAME() {
        return this.NAME;
    }
    /**
     * @Title: getDESCRIPTION
     * @Description:
     * @return String
     */
    public final String getDESCRIPTION() {
        return this.DESCRIPTION;
    }
    /**
     * @Title: getMAINTENANCE_EXPIRATION_DATE
     * @Description:
     * @return String
     */
    public final String getMAINTENANCE_EXPIRATION_DATE() {
        return this.MAINTENANCE_EXPIRATION_DATE;
    }
    /**
     * @Title: getINSTALL_COUNTRY_CODE
     * @Description:
     * @return String
     */
    public final String getINSTALL_COUNTRY_CODE() {
        return this.INSTALL_COUNTRY_CODE;
    }
    /**
     * @Title: getCUSTOMER_ID
     * @Description:
     * @return Strings
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
     * @Title: setSOURCE_SYSTEM
     * @Description:
     * @param sOURCE_SYSTEM
     * sOURCE_SYSTEM
     */
    public final void setSOURCE_SYSTEM(final String sOURCE_SYSTEM) {
        this.SOURCE_SYSTEM = sOURCE_SYSTEM;
    }
    /**
     * @Title: setSKU
     * @Description:
     * @param sKU
     * sKU
     */
    public final void setSKU(final String sKU) {
        this.SKU = sKU;
    }
    /**
     * @Title: setSERIAL_NUMBER
     * @Description:
     * @param sERIAL_NUMBER
     * sERIAL_NUMBER
     */
    public final void setSERIAL_NUMBER(final String sERIAL_NUMBER) {
        this.SERIAL_NUMBER = sERIAL_NUMBER;
    }
    /**
     * @Title: setNAME
     * @Description:
     * @param nAME
     * nAME
     */
    public final void setNAME(final String nAME) {
        this.NAME = nAME;
    }
    /**
     * @Title: setDESCRIPTION
     * @Description:
     * @param dESCRIPTION
     * dESCRIPTION
     */
    public final void setDESCRIPTION(final String dESCRIPTION) {
        this.DESCRIPTION = dESCRIPTION;
    }
    /**
     * @Title: setMAINTENANCE_EXPIRATION_DATE
     * @Description:
     * @param mAINTENANCE_EXPIRATION_DATE
     * mAINTENANCE_EXPIRATION_DATE
     */
    public final void setMAINTENANCE_EXPIRATION_DATE(
            final String mAINTENANCE_EXPIRATION_DATE) {
        this.MAINTENANCE_EXPIRATION_DATE = mAINTENANCE_EXPIRATION_DATE;
    }
    /**
     * @Title: setINSTALL_COUNTRY_CODE
     * @Description:
     * @param iNSTALL_COUNTRY_CODE
     * iNSTALL_COUNTRY_CODE
     */
    public final void
            setINSTALL_COUNTRY_CODE(final String iNSTALL_COUNTRY_CODE) {
        this.INSTALL_COUNTRY_CODE = iNSTALL_COUNTRY_CODE;
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
     * @Title: getINSTALLATION_DATE
     * @Description:
     * @return String
     */
    public final String getINSTALLATION_DATE() {
        return this.INSTALLATION_DATE;
    }
    /**
     * @Title: setINSTALLATION_DATE
     * @Description:
     * @param iNSTALLATION_DATE
     * iNSTALLATION_DATE
     */
    public final void setINSTALLATION_DATE(final String iNSTALLATION_DATE) {
        this.INSTALLATION_DATE = iNSTALLATION_DATE;
    }
    /**
     * @Title: getACTUAL_SHIP_DATE
     * @Description:
     * @return String
     */
    public final String getACTUAL_SHIP_DATE() {
        return this.ACTUAL_SHIP_DATE;
    }
    /**
     * @Title: setACTUAL_SHIP_DATE
     * @Description:
     * @param aCTUAL_SHIP_DATE
     * aCTUAL_SHIP_DATE
     */
    public final void setACTUAL_SHIP_DATE(final String aCTUAL_SHIP_DATE) {
        this.ACTUAL_SHIP_DATE = aCTUAL_SHIP_DATE;
    }

}
