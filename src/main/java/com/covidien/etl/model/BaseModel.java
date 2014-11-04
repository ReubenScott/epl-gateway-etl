package com.covidien.etl.model;

/**
 * @ClassName: BaseModel
 * @Description:
 */
public abstract class BaseModel {
    /**
     * BATCH_NUMBER.
     */
    private int BATCH_NUMBER;
    /**
     * IS_DELETED.
     */
    private int IS_DELETED;
    /**
     * LAST_CHANGE_DATE.
     */
    private String LAST_CHANGE_DATE;
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
     * exception
     */
    public final void setException(final String exception) {
        this.exception = exception;
    }
    /**
     * @Title: getBATCH_NUMBER
     * @Description:
     * @return int
     */
    public final int getBATCH_NUMBER() {
        return this.BATCH_NUMBER;
    }
    /**
     * @Title: setBATCH_NUMBER
     * @Description:
     * @param bATCH_NUMBER
     * bATCH_NUMBER
     */
    public final void setBATCH_NUMBER(final int bATCH_NUMBER) {
        this.BATCH_NUMBER = bATCH_NUMBER;
    }
    /**
     * @Title: getIS_DELETED
     * @Description:
     * @return int
     */
    public final int getIS_DELETED() {
        return this.IS_DELETED;
    }
    /**
     * @Title: setIS_DELETED
     * @Description:
     * @param iS_DELETED
     * iS_DELETED
     */
    public final void setIS_DELETED(final int iS_DELETED) {
        this.IS_DELETED = iS_DELETED;
    }
    /**
     * @Title: getLAST_CHANGE_DATE
     * @Description:
     * @return String
     */
    public final String getLAST_CHANGE_DATE() {
        return this.LAST_CHANGE_DATE;
    }
    /**
     * @Title: setLAST_CHANGE_DATE
     * @Description:
     * @param lAST_CHANGE_DATE
     * lAST_CHANGE_DATE
     */
    public final void setLAST_CHANGE_DATE(final String lAST_CHANGE_DATE) {
        this.LAST_CHANGE_DATE = lAST_CHANGE_DATE;
    }
}
