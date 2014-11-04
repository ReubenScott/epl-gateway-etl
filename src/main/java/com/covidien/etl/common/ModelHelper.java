package com.covidien.etl.common;

import com.covidien.etl.model.BaseModel;
import com.covidien.etl.model.Customer;
import com.covidien.etl.model.Device;
import com.covidien.etl.model.Location;
import com.covidien.etl.model.LocationRole;

/**
 * @ClassName: ModelHelper
 * @Description:
 */
public class ModelHelper {
    /**
     * Define the delimiter.
     */
    private static final String SPLITTER = "|";
    /**
     * @Title: getHeader
     * @Description:
     * @param type
     * type
     * @return String
     */
    public static String getHeader(final EtlType type) {
        return getHeader(type, false);
    }
    /**
     * @Title: getHeader
     * @Description:
     * @param type
     * type
     * @param hasException
     * hasException
     * @return String
     */
    public static String getHeader(final EtlType type,
            final boolean hasException) {
        StringBuilder sb = new StringBuilder();
        switch (type) {
        case Customer:
            sb.append("CUSTOMER_ID|NAME|PHONE|FAX|DISTRIBUTOR_FLAG|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
            break;
        case Location:
            sb.append("LOCATION_ID|ADDRESS_LINE1|ADDRESS_MODIFIER_1|ADDRESS_MODIFIER_2|ADDRESS_MODIFIER_3|ADDRESS_MODIFIER_4|CITY|STATE_PROVINCE|POSTAL_CODE|COUNTRY_CODE|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
            break;
        case LocationRole:
            sb.append("CUSTOMER_ID|LOCATION_ID|LOCATION_ROLE|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
            break;
        case Device:
            sb.append("LOCATION_ID|CUSTOMER_ID|MAINTENANCE_EXPIRATION_DATE|SERIAL_NUMBER|SKU|SOURCE_SYSTEM|INSTALL_COUNTRY_CODE|INSTALLATION_DATE|ACTUAL_SHIP_DATE|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
        default:
            break;
        }

        if (hasException) {
            sb.append("|EXCEPTION");
        }
        return sb.toString();
    }
    /**
     * @Title: parseModel
     * @Description:
     * @param model
     * model
     * @return String String
     */
    public static String parseModel(final BaseModel model) {
        return parseModel(model, false);
    }
    /**
     * @Title: parseModel
     * @Description:
     * @param model
     * model
     * @param hasException
     * hasException
     * @return String
     */
    public static String parseModel(final BaseModel model,
            final boolean hasException) {

        StringBuilder sb = new StringBuilder();
        if (model instanceof Customer) {
            Customer customer = (Customer) model;
            sb.append(customer.getCUSTOMER_ID()).append(SPLITTER)
                    .append(customer.getNAME()).append(SPLITTER)
                    .append(customer.getPHONE()).append(SPLITTER)
                    .append(customer.getFAX()).append(SPLITTER)
                    .append(customer.getDISTRIBUTOR_FLAG());

        } else if (model instanceof Location) {
            Location location = (Location) model;
            sb.append(location.getLOCATION_ID()).append(SPLITTER)
                    .append(location.getADDRESS_LINE1()).append(SPLITTER)
                    .append(location.getADDRESS_MODIFIER_1()).append(SPLITTER)
                    .append(location.getADDRESS_MODIFIER_2()).append(SPLITTER)
                    .append(location.getADDRESS_MODIFIER_3()).append(SPLITTER)
                    .append(location.getADDRESS_MODIFIER_4()).append(SPLITTER)
                    .append(location.getCITY()).append(SPLITTER)
                    .append(location.getSTATE_PROVINCE()).append(SPLITTER)
                    .append(location.getPOSTAL_CODE()).append(SPLITTER)
                    .append(location.getCOUNTRY_CODE());

        } else if (model instanceof LocationRole) {
            LocationRole locationRole = (LocationRole) model;
            sb.append(locationRole.getCUSTOMER_ID()).append(SPLITTER)
                    .append(locationRole.getLOCATION_ID()).append(SPLITTER)
                    .append(locationRole.getLOCATION_ROLE());

        } else if (model instanceof Device) {
            Device device = (Device) model;
            sb.append(device.getLOCATION_ID()).append(SPLITTER)
                    .append(device.getCUSTOMER_ID()).append(SPLITTER)
                    .append(device.getMAINTENANCE_EXPIRATION_DATE())
                    .append(SPLITTER).append(device.getSERIAL_NUMBER())
                    .append(SPLITTER).append(device.getSKU()).append(SPLITTER)
                    .append(device.getSOURCE_SYSTEM()).append(SPLITTER)
                    .append(device.getINSTALL_COUNTRY_CODE()).append(SPLITTER)
                    .append(device.getINSTALLATION_DATE()).append(SPLITTER)
                    .append(device.getACTUAL_SHIP_DATE());
        }

        sb.append(SPLITTER).append(model.getIS_DELETED()).append(SPLITTER)
                .append(model.getLAST_CHANGE_DATE()).append(SPLITTER)
                .append(model.getBATCH_NUMBER());
        if (hasException) {
            sb.append(SPLITTER).append(model.getException());
        }
        return sb.toString().replaceAll("null", "");
    }
}
