package com.covidien.etl.validate;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.model.Device;

/**
 * @ClassName: DeviceValidator
 * @Description:
 */
public final class DeviceValidator {
    /**
     * DeviceValidator.
     */
    private static DeviceValidator validator = new DeviceValidator();
    /**
     * currentValidator.
     */
    private Validator currentValidator;
    /**
     * sku validator.
     */
    private Validator skuValidator;
    /**
     * sn validator.
     */
    private Validator snValidator;

    /**
     * @Title: getInstance
     * @Description:
     * @return DeviceValidator
     */
    public static DeviceValidator getInstance() {
        return validator;
    }

    /**
     * @Title: DeviceValidator
     * @Description:
     */
    private DeviceValidator() {
        skuValidator = new SKUValidator();
        snValidator = new SNValidator();
        skuValidator.setNextValidator(snValidator);
        currentValidator = skuValidator;
    }

    /**
     * @Title: validate
     * @Description:
     * @param device
     *        device
     * @return DeviceValidateResult
     */
    public DeviceValidateResult validate(final Device device) {
        return currentValidator.validate(device);
    }

    /**
     * set validator again to reuse this instance.
     * 
     * @Title: rebuild
     */
    public void rebuild() {
        skuValidator.setNextValidator(snValidator);
        currentValidator = skuValidator;
    }
}
