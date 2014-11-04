package com.covidien.etl.model;

/**
 * @ClassName: Batch
 * @Description:
 */
public class Batch {
    /**
     * sourceName.
     */
    private String sourceName;
    /**
     * batchNumber.
     */
    private int batchNumber;
    /**
     * batchRunTimestamp.
     */
    private String batchRunTimestamp;
    /**
     * rowsSent.
     */
    private int rowsSent;
    /**
     * objectName.
     */
    private String objectName;
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
     *        fileName
     */
    public final void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the sourceName
     */
    public final String getSourceName() {
        return sourceName;
    }

    /**
     * @param sourceName
     *        the sourceName to set
     */
    public final void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * @return the batchNumber
     */
    public final int getBatchNumber() {
        return batchNumber;
    }

    /**
     * @param batchNumber
     *        the batchNumber to set
     */
    public final void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    /**
     * @return the batchRunTimestamp
     */
    public final String getBatchRunTimestamp() {
        return batchRunTimestamp;
    }

    /**
     * @param batchRunTimestamp
     *        the batchRunTimestamp to set
     */
    public final void setBatchRunTimestamp(String batchRunTimestamp) {
        this.batchRunTimestamp = batchRunTimestamp;
    }

    /**
     * @return the rowsSent
     */
    public final int getRowsSent() {
        return rowsSent;
    }

    /**
     * @param rowsSent
     *        the rowsSent to set
     */
    public final void setRowsSent(int rowsSent) {
        this.rowsSent = rowsSent;
    }

    /**
     * @return the objectName
     */
    public final String getObjectName() {
        return objectName;
    }

    /**
     * @param objectName
     *        the objectName to set
     */
    public final void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public final String toString() {
        return "SOURCE_NAME:" + sourceName + ";BATCH_NUMBER:" + batchNumber + ";BATCH_RUN_TIMESTAMP:"
                + batchRunTimestamp + ";ROWS_SENT:" + rowsSent + ";OBJECT_NAME:" + objectName;
    }
}
