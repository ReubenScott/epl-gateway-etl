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
public final class ModelHelper {
    /**
     * @Title: ModelHelper
     * @Description:
     */
    private ModelHelper() {
    }

    /**
     * Define the delimiter.
     */
    private static final String SPLITTER = "|";

    /**
     * @Title: getHeader
     * @Description:
     * @param type
     *        type
     * @return String
     */
    public static String getHeader(final EtlType type) {
        return getHeader(type, false);
    }

    /**
     * @Title: getHeader
     * @Description:
     * @param type
     *        type
     * @param hasException
     *        hasException
     * @return String
     */
    public static String getHeader(final EtlType type, final boolean hasException) {
        StringBuilder sb = new StringBuilder();
        switch (type) {
        case Customer:
            sb.append("CUSTOMER_ID|NAME|PHONE|FAX|DISTRIBUTOR_FLAG|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
            break;
        case Location:
            sb.append("LOCATION_ID|ADDRESS_LINE1|ADDRESS_MODIFIER_1|ADDRESS_MODIFIER_2|ADDRESS_MODIFIER_3");
            sb.append("|ADDRESS_MODIFIER_4|CITY|STATE_PROVINCE|POSTAL_CODE|COUNTRY_CODE");
            sb.append("|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
            break;
        case LocationRole:
            sb.append("CUSTOMER_ID|LOCATION_ID|LOCATION_ROLE|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
            break;
        case Device:
            sb.append("LOCATION_ID|CUSTOMER_ID|MAINTENANCE_EXPIRATION_DATE|SERIAL_NUMBER|SKU|SOURCE_SYSTEM");
            sb.append("|INSTALL_COUNTRY_CODE|INSTALLATION_DATE|ACTUAL_SHIP_DATE");
            sb.append("|IS_DELETED|LAST_CHANGE_DATE|BATCH_NUMBER");
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
     *        model
     * @return String String
     */
    public static String parseModel(final BaseModel model) {
        return parseModel(model, false);
    }

    /**
     * @Title: parseModel
     * @Description:
     * @param model
     *        model
     * @param hasException
     *        hasException
     * @return String
     */
    public static String parseModel(final BaseModel model, final boolean hasException) {

        StringBuilder sb = new StringBuilder();
        if (model instanceof Customer) {
            Customer customer = (Customer) model;
            sb.append(customer.getCustomerId()).append(SPLITTER).append(customer.getName()).append(SPLITTER)
                    .append(customer.getPhone()).append(SPLITTER).append(customer.getFax()).append(SPLITTER)
                    .append(customer.getDistributorFlag());

        } else if (model instanceof Location) {
            Location location = (Location) model;
            sb.append(location.getLocationId()).append(SPLITTER).append(location.getAddressLine1()).append(SPLITTER)
                    .append(location.getAddressModifier1()).append(SPLITTER).append(location.getAddressModifier2())
                    .append(SPLITTER).append(location.getAddressModifier3()).append(SPLITTER)
                    .append(location.getAddressModifier4()).append(SPLITTER).append(location.getCity())
                    .append(SPLITTER).append(location.getStateProvince()).append(SPLITTER)
                    .append(location.getPostalCode()).append(SPLITTER).append(location.getCountryCode());

        } else if (model instanceof LocationRole) {
            LocationRole locationRole = (LocationRole) model;
            sb.append(locationRole.getCustomerId()).append(SPLITTER).append(locationRole.getLocationId())
                    .append(SPLITTER).append(locationRole.getLocationRole());

        } else if (model instanceof Device) {
            Device device = (Device) model;
            sb.append(device.getLocationId()).append(SPLITTER).append(device.getCustomerId()).append(SPLITTER)
                    .append(device.getMaintenanceExpirationDate()).append(SPLITTER).append(device.getSerialNumber())
                    .append(SPLITTER).append(device.getSku()).append(SPLITTER).append(device.getSourceSystem())
                    .append(SPLITTER).append(device.getInstallCountryCode()).append(SPLITTER)
                    .append(device.getInstallationDate()).append(SPLITTER).append(device.getActualShipDate());
        }

        sb.append(SPLITTER).append(model.getIsDeleted()).append(SPLITTER).append(model.getLastChangeDate())
                .append(SPLITTER).append(model.getBatchNumber());
        if (hasException) {
            sb.append(SPLITTER).append(model.getException());
        }
        return sb.toString().replaceAll("null", "");
    }
}
