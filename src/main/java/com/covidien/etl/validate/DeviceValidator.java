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
        Validator skuValidator = new SKUValidator();
        Validator snValidator = new SNValidator();
        skuValidator.setNextValidator(snValidator);
        currentValidator = skuValidator;
    }
    /**
     * @Title: validate
     * @Description:
     * @param device
     * device
     * @return DeviceValidateResult
     */
    public DeviceValidateResult validate(final Device device) {
        return currentValidator.validate(device);
    }
}
