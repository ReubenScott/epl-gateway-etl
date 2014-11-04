package com.covidien.etl.model;

/**
 * @ClassName: Customer
 * @Description:
 */
public class Customer extends BaseModel {
    /**
     * CUSTOMER_ID.
     */
    private String CUSTOMER_ID;
    /**
     * NAME.
     */
    private String NAME;
    /**
     * PHONE.
     */
    private String PHONE;
    /**
     * FAX.
     */
    private String FAX;
    /**
     * DISTRIBUTOR_FLAG.
     */
    private int DISTRIBUTOR_FLAG;
    /**
     * @Title: getCUSTOMER_ID
     * @Description:
     * @return String
     */
    public final String getCUSTOMER_ID() {
        return this.CUSTOMER_ID;
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
     * @Title: getNAME
     * @Description:
     * @return String
     */
    public final String getNAME() {
        return this.NAME;
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
     * @Title: getPHONE
     * @Description:
     * @return String
     */
    public final String getPHONE() {
        return this.PHONE;
    }
    /**
     * @Title: setPHONE
     * @Description:
     * @param pHONE
     * pHONE
     */
    public final void setPHONE(final String pHONE) {
        this.PHONE = pHONE;
    }
    /**
     * @Title: getFAX
     * @Description:
     * @return String
     */
    public final String getFAX() {
        return this.FAX;
    }
    /**
     * @Title: setFAX
     * @Description:
     * @param fAX
     * fAX
     */
    public final void setFAX(final String fAX) {
        this.FAX = fAX;
    }
    /**
     * @Title: getDISTRIBUTOR_FLAG
     * @Description:
     * @return int
     */
    public final int getDISTRIBUTOR_FLAG() {
        return this.DISTRIBUTOR_FLAG;
    }
    /**
     * @Title: setDISTRIBUTOR_FLAG
     * @Description:
     * @param dISTRIBUTOR_FLAG
     * dISTRIBUTOR_FLAG
     */
    public final void setDISTRIBUTOR_FLAG(final int dISTRIBUTOR_FLAG) {
        this.DISTRIBUTOR_FLAG = dISTRIBUTOR_FLAG;
    }

}
