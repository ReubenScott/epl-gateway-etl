package com.covidien.etl.model;

/**
 * @ClassName: Customer
 * @Description:
 */
public class Customer extends BaseModel {
    /**
     * customerId.
     */
    private String customerId;
    /**
     * name.
     */
    private String name;
    /**
     * PHONE.
     */
    private String phone;
    /**
     * fax.
     */
    private String fax;
    /**
     * distributorFlag.
     */
    private int distributorFlag;

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
     * @return the phone
     */
    public final String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *        the phone to set
     */
    public final void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the fax
     */
    public final String getFax() {
        return fax;
    }

    /**
     * @param fax
     *        the fax to set
     */
    public final void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the distributorFlag
     */
    public final int getDistributorFlag() {
        return distributorFlag;
    }

    /**
     * @param distributorFlag
     *        the distributorFlag to set
     */
    public final void setDistributorFlag(int distributorFlag) {
        this.distributorFlag = distributorFlag;
    }

}
