package com.covidien.etl.validate;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.model.Device;

/**
 * @ClassName: Validator
 * @Description:
 */
public abstract class Validator {
    /**
     * Validator.
     */
    private Validator nextValidator;
    /**
     * @Title: validate
     * @Description:
     * @param device
     * device
     * @return DeviceValidateResult
     */
    public final DeviceValidateResult validate(final Device device) {

        for (Validator validator = this; validator != null; validator = validator.nextValidator) {
            DeviceValidateResult result = validator.valiateCurrent(device);
            if (DeviceValidateResult.Success != result) {
                return result;
            }
        }
        return DeviceValidateResult.Success;
    }
    /**
     * @Title: valiateCurrent
     * @Description:
     * @param device
     * device
     * @return DeviceValidateResult
     */
    protected abstract DeviceValidateResult valiateCurrent(Device device);
    /**
     * @Title: setNextValidator
     * @Description:
     * @param validator
     * validator
     */
    public final void setNextValidator(final Validator validator) {
        this.nextValidator = validator;
    }
}
