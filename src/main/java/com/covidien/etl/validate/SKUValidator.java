package com.covidien.etl.validate;

import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.dao.SKUDAO;
import com.covidien.etl.model.Device;

/**
 * @ClassName: SKUValidator
 * @Description:
 */
public class SKUValidator extends Validator {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SKUValidator.class);
    /**
     * SKUDAO.
     */
    private SKUDAO skuDAO;
    /**
     * @Title: SKUValidator
     * @Description:
     */
    public SKUValidator() {
        skuDAO = new SKUDAO();
    }
    @Override
    public final DeviceValidateResult valiateCurrent(final Device device) {
        try {
            if (!skuDAO.isSKUExist(device)) {
                return DeviceValidateResult.SKUError;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
        return DeviceValidateResult.Success;
    }
}
