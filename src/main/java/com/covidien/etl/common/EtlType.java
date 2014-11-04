package com.covidien.etl.common;

/**
 * @ClassName: EtlType
 * @Description:
 */
public enum EtlType {
    /**
     * Eunm classes for EtlType.
     */
    Customer(0), Location(1), LocationRole(2), Device(3);
    /**
     * int value.
     */
    private int value;
    /**
     * @Title: EtlType
     * @Description:
     * @param value
     * value
     */
    private EtlType(final int value) {
        this.value = value;
    }
    /**
     * @Title: getValue
     * @Description:
     * @return int
     * @throws
     */
    public int getValue() {
        return value;
    }
}
