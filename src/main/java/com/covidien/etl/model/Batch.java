package com.covidien.etl.model;

/**
 * @ClassName: Batch
 * @Description:
 */
public class Batch {
    /**
     * SOURCE_NAME.
     */
    private String SOURCE_NAME;
    /**
     * BATCH_NUMBER.
     */
    private int BATCH_NUMBER;
    /**
     * BATCH_RUN_TIMESTAMP.
     */
    private String BATCH_RUN_TIMESTAMP;
    /**
     * ROWS_SENT.
     */
    private int ROWS_SENT;
    /**
     * OBJECT_NAME.
     */
    private String OBJECT_NAME;
    /**
     * fileName.
     */
    private String fileName;
    /**
     * @Title: getFileName
     * @Description:
     * @return String
     */
    public final String getFileName() {
        return this.fileName;
    }
    /**
     * @Title: setFileName
     * @Description:
     * @param fileName
     * fileName
     */
    public final void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    /**
     * @Title: getSOURCE_NAME
     * @Description:
     * @return String
     */
    public final String getSOURCE_NAME() {
        return this.SOURCE_NAME;
    }
    /**
     * @Title: setSOURCE_NAME
     * @Description:
     * @param sOURCE_NAME
     * sOURCE_NAME
     */
    public final void setSOURCE_NAME(final String sOURCE_NAME) {
        this.SOURCE_NAME = sOURCE_NAME;
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
     * @Title: getBATCH_RUN_TIMESTAMP
     * @Description:
     * @return String
     */
    public final String getBATCH_RUN_TIMESTAMP() {
        return this.BATCH_RUN_TIMESTAMP;
    }
    /**
     * @Title: setBATCH_RUN_TIMESTAMP
     * @Description:
     * @param bATCH_RUN_TIMESTAMP
     * bATCH_RUN_TIMESTAMP
     */
    public final void setBATCH_RUN_TIMESTAMP(final String bATCH_RUN_TIMESTAMP) {
        this.BATCH_RUN_TIMESTAMP = bATCH_RUN_TIMESTAMP;
    }
    /**
     * @Title: getROWS_SENT
     * @Description:
     * @return int
     */
    public final int getROWS_SENT() {
        return this.ROWS_SENT;
    }
    /**
     * @Title: setROWS_SENT
     * @Description:
     * @param rOWS_SENT
     * rOWS_SENT
     */
    public final void setROWS_SENT(final int rOWS_SENT) {
        ROWS_SENT = rOWS_SENT;
    }
    /**
     * @Title: getOBJECT_NAME
     * @Description:
     * @return String
     */
    public final String getOBJECT_NAME() {
        return this.OBJECT_NAME;
    }
    /**
     * @Title: setOBJECT_NAME
     * @Description:
     * @param oBJECT_NAME
     * oBJECT_NAME
     */
    public final void setOBJECT_NAME(final String oBJECT_NAME) {
        this.OBJECT_NAME = oBJECT_NAME;
    }
    @Override
    public final String toString() {
        return "SOURCE_NAME:" + SOURCE_NAME + ";BATCH_NUMBER:" + BATCH_NUMBER
                + ";BATCH_RUN_TIMESTAMP:" + BATCH_RUN_TIMESTAMP + ";ROWS_SENT:"
                + ROWS_SENT + ";OBJECT_NAME:" + OBJECT_NAME;
    }
}
