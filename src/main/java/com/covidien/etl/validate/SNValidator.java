package com.covidien.etl.validate;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.dao.SNDAO;
import com.covidien.etl.model.Device;

/**
 * @ClassName: SNValidator
 * @Description:
 */
public class SNValidator extends Validator {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SNValidator.class);
    /**
     * SNDAO.
     */
    private SNDAO snDAO;

    /**
     * @Title: SNValidator
     * @Description:
     */
    public SNValidator() {
        snDAO = new SNDAO();
    }

    @Override
    public final DeviceValidateResult valiateCurrent(final Device device) {
        try {
            String rule = snDAO.getRuleByDevice(device);
            if (rule == null) {
                return DeviceValidateResult.Success;
            }
            if (!device.getSerialNumber().matches(rule)) {
                return DeviceValidateResult.SNError;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
        return DeviceValidateResult.Success;
    }

    /**
     * @Title: main
     * @Description:
     * @param args
     *        args
     */
    public static void main(final String[] args) {
        System.out.println("P1234568T".matches("P(\\d{7})(T|TX|J|JX)"));
    }
}
