package com.covidien.etl.log;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.model.BaseModel;

/**
 * @ClassName: EtlLogger
 * @Description:
 */
public interface EtlLogger {
    /**
     * @Title: log
     * @Description:
     * @param content
     * content
     * @return boolean
     */
    boolean log(String content);
    /**
     * @Title: successInsert
     * @Description:
     * @param type
     * type
     * @param model
     * model
     * @return boolean
     */
    boolean successInsert(EtlType type, BaseModel model);
    /**
     * @Title: successUpdate
     * @Description:
     * @param type
     * type
     * @param model
     * model
     * @return boolean
     */
    boolean successUpdate(EtlType type, BaseModel model);
    /**
     * @Title: successDelete
     * @Description:
     * @param type
     * type
     * @param model
     * model
     * @return boolean
     */
    boolean successDelete(EtlType type, BaseModel model);
    /**
     * @Title: failInsert
     * @Description:
     * @param type
     * type
     * @param model
     * model
     * @return boolean
     */
    boolean failInsert(EtlType type, BaseModel model);
    /**
     * @Title: failUpdate
     * @Description:
     * @param type
     * type
     * @param model
     * model
     * @return boolean
     */
    boolean failUpdate(EtlType type, BaseModel model);
    /**
     * @Title: failDelete
     * @Description:
     * @param type
     * type
     * @param model
     * model
     * @return boolean
     */
    boolean failDelete(EtlType type, BaseModel model);
    /**
     * @Title: failValidate
     * @Description:
     * @param type
     * type
     * @param errorType
     * errorType
     * @param model
     * model
     * @return boolean
     */
    boolean failValidate(EtlType type, DeviceValidateResult errorType, BaseModel model);
    /**
     * @Title: sendEmailWithException
     * @Description:
     * @param emailSubjet
     * emailSubjet
     * @param emailContent
     * emailContent
     * @return boolean
     */
    boolean sendEmailWithException(String emailSubjet, String emailContent);
    /**
     * @Title: sendEmail
     * @Description:
     * @param emailSubjet
     * emailSubjet
     * @param emailContent
     * emailContent
     * @return sendEmail
     */
    boolean sendEmail(String emailSubjet, String emailContent);
}
