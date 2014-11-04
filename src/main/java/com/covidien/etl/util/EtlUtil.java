/**
 * @Title: EtlUtils.java
 * @Package com.covidien.etl.util
 * @Description:
 * @author tony.zhang2
 * @date 2013-12-18
 * @version V2.0
 */
package com.covidien.etl.util;

/**
 * @ClassName: EtlUtils
 * @Description:
 */
public final class EtlUtil {
    /**
     * @Title: EtlUtil
     * @Description:
     */
    private EtlUtil() {

    }

    /**
     * @Title: convertHeader
     * @Description: Convert CSV header name to match the checkstyle name
     *               conventions.
     * @param header
     *        CSV file header
     */
    public static void convertHeader(String[] header) {
        for (int i = 0; i < header.length; i++) {
            String temp = header[i];
            String regex = "^[a-z][a-zA-Z0-9]*$";
            if (temp.matches(regex)) {
                continue;
            }
            temp = temp.toLowerCase();
            while (temp.indexOf("_") >= 0) {
                int index = temp.indexOf("_");
                String str = temp.substring(index + 1, index + 2);
                temp = temp.replace("_" + str, str.toUpperCase());
            }
            header[i] = temp;
        }
    }
}
