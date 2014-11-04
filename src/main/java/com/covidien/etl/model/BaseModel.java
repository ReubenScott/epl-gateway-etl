package com.covidien.etl.model;

/**
 * @ClassName: BaseModel
 * @Description:
 */
public abstract class BaseModel {
    /**
     * batchNumber.
     */
    private int batchNumber;
    /**
     * isDeleted.
     */
    private int isDeleted;
    /**
     * lastChangeDate.
     */
    private String lastChangeDate;
    /**
     * exception.
     */
    private String exception;

    /**
     * @Title: getException
     * @Description:
     * @return String
     */
    public final String getException() {
        return this.exception;
    }

    /**
     * @Title: setException
     * @Description:
     * @param exception
     *        exception
     */
    public final void setException(final String exception) {
        this.exception = exception;
    }

    /**
     * @Title: getBatchNumber
     * @Description:
     * @return int
     */
    public final int getBatchNumber() {
        return this.batchNumber;
    }

    /**
     * @Title: setBatchNumber
     * @Description:
     * @param batchNumber
     *        batchNumber
     */
    public final void setBatchNumber(final int batchNumber) {
        this.batchNumber = batchNumber;
    }

    /**
     * @Title: getIsDeleted
     * @Description:
     * @return int
     */
    public final int getIsDeleted() {
        return this.isDeleted;
    }

    /**
     * @Title: setIsDeleted
     * @Description:
     * @param isDeleted
     *        isDeleted
     */
    public final void setIsDeleted(final int isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @Title: getLastChangeDate
     * @Description:
     * @return String
     */
    public final String getLastChangeDate() {
        return this.lastChangeDate;
    }

    /**
     * @Title: setLastChangeDate
     * @Description:
     * @param lastChangeDate
     *        lastChangeDate
     */
    public final void setLastChangeDate(final String lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }
}
