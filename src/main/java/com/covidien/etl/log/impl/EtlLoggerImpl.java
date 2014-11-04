/**
 * This Logger is a EtlLogger implementation.<br/>
 * <strong>Note</strong>: this Logger will write log messages to log file(s)
 * named by the jobName, different jobName, different log file.<br/>
 * The sendEmail method will send the log file as a attachment to recipients who
 * were specified in <em>conf/etl.ini</em> file via <strong>recipients</strong>
 * field.<br/>
 * This class applies to Singleton Pattern(lazy load mode)
 * @link ch.qos.logback.classic.Logger
 * @author guangfeng.ning <guangfeng.ning@covidien.com>
 * @version 1.0
 * @since 1.0
 */
package com.covidien.etl.log.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.common.EtlFileHelper;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.common.ModelHelper;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.job.EtlJobFactory;
import com.covidien.etl.log.EtlLogger;
import com.covidien.etl.model.BaseModel;

/**
 * @ClassName: EtlLoggerImpl
 * @Description:
 */
public class EtlLoggerImpl implements EtlLogger {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(EtlLoggerImpl.class);
    /**
     * EtlLogger.
     */
    private static volatile EtlLogger instance;
    /**
     * lock.
     */
    private static Object lock = new Object();
    /**
     * loggers.
     */
    private static Map<String, ch.qos.logback.classic.Logger> loggers;
    /**
     * session.
     */
    private Session session;
    /**
     * recipients.
     */
    private String recipients;
    /**
     * path.
     */
    private String path;
    /**
     * from.
     */
    private String from;
    /**
     * host.
     */
    private String host;
    /**
     * user.
     */
    private String user;
    /**
     * password.
     */
    private String password;
    /**
     * port.
     */
    private String port;
    /**
     * auth.
     */
    private boolean auth;
    /**
     * jobName.
     */
    private String jobName;
    /**
     * <p>
     * Title: EtlLoggerImpl
     * </p>
     * .
     * <p>
     * Description:
     * </p>
     * .
     */
    public EtlLoggerImpl() {
        Properties etlCfg = PropertyReader.getInstance().read("log.properties");

        String recipientsFilePath = etlCfg.getProperty("recipients.file.path");
        if (recipientsFilePath != null
                && recipientsFilePath.trim().length() > 0) {
            List<String> recipientsList = EtlFileHelper.getInstance().read(
                    recipientsFilePath);
            if (recipientsList.size() > 0) {
                recipients = recipientsList.get(0);
            }
        }
        if (recipients == null) {
            recipients = etlCfg.getProperty("recipients");
        }
        LOGGER.info("recipients is :" + recipients);

        from = etlCfg.getProperty("mail.from");
        host = etlCfg.getProperty("mail.smtp.host");
        user = etlCfg.getProperty("mail.smtp.user");
        password = etlCfg.getProperty("mail.smtp.password");
        port = etlCfg.getProperty("mail.smtp.port");
        auth = Boolean.parseBoolean(etlCfg.getProperty("mail.smtp.auth"));
        path = etlCfg.getProperty("log.path", "/var/log/etltool/");

        if (auth) {
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", user);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            session = Session.getDefaultInstance(props, null);
        } else {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "false");
            session = Session.getInstance(props, null);
        }
        loggers = new HashMap<String, ch.qos.logback.classic.Logger>();
        jobName = EtlJobFactory.getCurrentJobId();
    }
    /**
     * @Title: getInstance
     * @Description: get the singleton Logger instance. using double-check
     * mechanism.
     * @return EtlLogger
     */
    public static EtlLogger getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new EtlLoggerImpl();
                }
            }
        }
        return instance;
    }
    @Override
    public final boolean
            successInsert(final EtlType type, final BaseModel model) {
        String fileName = null;
        switch (type) {
        case Customer:
            fileName = Constant.CUSTOMER_SUCCESS_INSERT;
            break;
        case Location:
            fileName = Constant.LOCATION_SUCCESS_INSERT;
            break;
        case LocationRole:
            fileName = Constant.LOCATION_ROLE_SUCCESS_INSERT;
            break;
        case Device:
            fileName = Constant.DEVICE_SUCCESS_INSERT;
            break;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model));
    }
    @Override
    public final boolean
            successUpdate(final EtlType type, final BaseModel model) {
        String fileName = null;
        switch (type) {
        case Customer:
            fileName = Constant.CUSTOMER_SUCCESS_UPDATE;
            break;
        case Location:
            fileName = Constant.LOCATION_SUCCESS_UPDATE;
            break;
        case LocationRole:
            fileName = Constant.LOCATION_ROLE_SUCCESS_UPDATE;
            break;
        case Device:
            fileName = Constant.DEVICE_SUCCESS_UPDATE;
            break;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model));
    }
    @Override
    public final boolean
            successDelete(final EtlType type, final BaseModel model) {
        String fileName = null;
        switch (type) {
        case Customer:
            fileName = Constant.CUSTOMER_SUCCESS_DELETE;
            break;
        case Location:
            fileName = Constant.LOCATION_SUCCESS_DELETE;
            break;
        case LocationRole:
            fileName = Constant.LOCATION_ROLE_SUCCESS_DELETE;
            break;
        case Device:
            fileName = Constant.DEVICE_SUCCESS_DELETE;
            break;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model));
    }
    @Override
    public final boolean failInsert(final EtlType type, final BaseModel model) {
        String fileName = null;
        switch (type) {
        case Customer:
            fileName = Constant.CUSTOMER_FAIL_INSERT;
            break;
        case Location:
            fileName = Constant.LOCATION_FAIL_INSERT;
            break;
        case LocationRole:
            fileName = Constant.LOCATION_ROLE_FAIL_INSERT;
            break;
        case Device:
            fileName = Constant.DEVICE_FAIL_INSERT;
            break;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model, true), true);
    }
    @Override
    public final boolean failUpdate(final EtlType type, final BaseModel model) {
        String fileName = null;
        switch (type) {
        case Customer:
            fileName = Constant.CUSTOMER_FAIL_UPDATE;
            break;
        case Location:
            fileName = Constant.LOCATION_FAIL_UPDATE;
            break;
        case LocationRole:
            fileName = Constant.LOCATION_ROLE_FAIL_UPDATE;
            break;
        case Device:
            fileName = Constant.DEVICE_FAIL_UPDATE;
            break;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model, true), true);
    }
    @Override
    public final boolean failDelete(final EtlType type, final BaseModel model) {
        String fileName = null;
        switch (type) {
        case Customer:
            fileName = Constant.CUSTOMER_FAIL_DELETE;
            break;
        case Location:
            fileName = Constant.LOCATION_FAIL_DELETE;
            break;
        case LocationRole:
            fileName = Constant.LOCATION_ROLE_FAIL_DELETE;
            break;
        case Device:
            fileName = Constant.DEVICE_FAIL_DELETE;
            break;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model, true), true);
    }
    @Override
    public final boolean failValidate(final EtlType type,
            final DeviceValidateResult errorRype, final BaseModel model) {

        if (EtlType.Device != type) {
            return false;
        }
        String fileName = null;
        switch (errorRype) {
        case Success:
            return false;
        case SKUError:
            fileName = Constant.DEVICE_FAIL_SKU_VALIDATE;
            break;
        case SNError:
            fileName = Constant.DEVICE_FAIL_SN_VALIDATE;
        default:
            break;
        }
        return log(jobName + "_" + fileName, type,
                ModelHelper.parseModel(model, true), true);

    }
    /**
     * @Title: log
     * @Description:
     * @param fileName
     * fileName
     * @param type
     * type
     * @param content
     * content
     * @return boolean
     */
    private boolean log(final String fileName, final EtlType type,
            final String content) {
        return log(fileName, type, content, false);
    }
    /**
     * @Title: log
     * @Description:
     * @param fileName
     * fileName
     * @param type
     * type
     * @param content
     * content
     * @param hasException
     * hasException
     * @return boolean
     */
    private synchronized boolean log(final String fileName, final EtlType type,
            final String content, final boolean hasException) {
        if (content == null || content.trim().length() == 0) {
            return false;
        }
        String logFile = path + fileName + ".CSV";
        File file = new File(logFile);
        if (!file.exists()) {
            FileWriter fw = null;
            PrintWriter out = null;
            try {
                fw = new FileWriter(logFile);
                out = new PrintWriter(fw);
                out.println(ModelHelper.getHeader(type, hasException));
            } catch (IOException e) {
                LOGGER.error("Exception:", e);
            } finally {
                out.close();
                try {
                    fw.close();
                } catch (IOException e) {
                    LOGGER.error("Exception:", e);
                }
            }
        }

        FileWriter fw = null;
        PrintWriter out = null;
        try {
            fw = new FileWriter(logFile, true);
            out = new PrintWriter(fw);
            out.println(content);
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        } finally {
            out.close();
            try {
                fw.close();
            } catch (IOException e) {
                LOGGER.error("Exception:", e);
            }
        }
        return true;
    }
    @Override
    public final boolean log(final String content) {
        return log(this.jobName, content);
    }
    /**
     * @Title: log
     * @Description: Log the message.
     * @param jobName
     * where the log from, and forms the log file name.
     * @param content
     * the body of log.
     * @return boolean
     */
    private boolean log(final String jobName, final String content) {
        ch.qos.logback.classic.Logger logger = getLogger(jobName);
        if (logger != null) {
            logger.info(content);
            return true;
        }
        return false;
    }
    @Override
    public final boolean sendEmailWithException(final String emailSubjet,
            final String emailContent) {
        return sendEmail(this.jobName, emailSubjet, emailContent, true);
    }
    @Override
    public final boolean sendEmail(final String emailSubjet,
            final String emailContent) {
        return sendEmail(this.jobName, emailSubjet, emailContent, true);
    }
    /**
     * @Title: sendEmail
     * @Description: Send the log file (decided by jobName) to recipients.
     * @param jobName
     * where the log from, and forms the log file name, and the subject of the
     * email.
     * @param subject
     * subject
     * @param emailContent
     * emailContent
     * @param hasAttachFile
     * hasAttachFile
     * @return boolean
     */
    private boolean sendEmail(final String jobName, final String subject,
            final String emailContent, final boolean hasAttachFile) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(from);
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipients));
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // add at least simple body

            // attachment the log file
            if (hasAttachFile) {
                MimeBodyPart body = new MimeBodyPart();
                body.setContent(emailContent, "text/html;charset = utf-8");

                String[] failedTypes = {
                        Constant.CUSTOMER_FAIL_INSERT,
                        Constant.CUSTOMER_FAIL_UPDATE,
                        Constant.CUSTOMER_FAIL_DELETE,
                        Constant.DEVICE_FAIL_INSERT,
                        Constant.DEVICE_FAIL_UPDATE,
                        Constant.DEVICE_FAIL_DELETE,
                        Constant.LOCATION_FAIL_INSERT,
                        Constant.LOCATION_FAIL_UPDATE,
                        Constant.LOCATION_FAIL_DELETE,
                        Constant.LOCATION_ROLE_FAIL_INSERT,
                        Constant.LOCATION_ROLE_FAIL_UPDATE,
                        Constant.LOCATION_ROLE_FAIL_DELETE };

                Multipart multipart = new MimeMultipart();
                boolean isEmpty = true;
                for (String failedType : failedTypes) {
                    String fileName = path + jobName + "_" + failedType
                            + ".CSV";
                    File logFile = new File(fileName);
                    if (!logFile.exists()) {
                        continue;
                    }
                    isEmpty = false;
                    MimeBodyPart attachMent = new MimeBodyPart();
                    FileDataSource dataSource = new FileDataSource(logFile);
                    attachMent.setDataHandler(new DataHandler(dataSource));
                    attachMent.setFileName(jobName + "_" + failedType + ".CSV");
                    attachMent.setDisposition(MimeBodyPart.ATTACHMENT);
                    multipart.addBodyPart(attachMent);
                }
                multipart.addBodyPart(body);
                if (isEmpty) {
                    msg.setContent(emailContent, "text/html;charset = utf-8");
                } else {
                    msg.setContent(multipart);
                }
            } else {
                // msg.setText(emailContent);
                msg.setContent(emailContent, "text/html;charset = utf-8");
            }
            Transport transport = session.getTransport("smtp");
            int index = 0;
            boolean isSuccessful = false;
            while (index <= 3 && !isSuccessful) {
                try {
                    if (auth) {
                        transport.connect(user, password);
                    } else {
                        transport.connect();
                    }
                    isSuccessful = true;
                } catch (Exception e) {
                    LOGGER.error(
                            "send email failed, then retry it. Exception:", e);
                    isSuccessful = false;
                    index++;
                }
            }
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (MessagingException mex) {
            LOGGER.error("send failed, exception:", mex);
            return false;
        }
        return true;
    }
    /**
     * @Title: getLogger
     * @Description:
     * @param file
     * file
     * @return ch.qos.logback.classic.Logger
     */
    private synchronized ch.qos.logback.classic.Logger getLogger(
            final String file) {
        if (loggers.containsKey(file)) {
            return loggers.get(file);
        } else {
            LoggerContext lc = (LoggerContext) LoggerFactory
                    .getILoggerFactory();
            PatternLayoutEncoder ple = new PatternLayoutEncoder();
            ple.setPattern("%date [%thread] %msg%n");
            ple.setContext(lc);
            ple.start();
            FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
            fileAppender.setFile(path + file + ".log");
            LOGGER.info("log path is :" + path + file + ".log");
            fileAppender.setEncoder(ple);
            fileAppender.setContext(lc);
            fileAppender.start();
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory
                    .getLogger(EtlLoggerImpl.class);
            logger.addAppender(fileAppender);
            logger.setLevel(Level.DEBUG);
            logger.setAdditive(false);
            loggers.put(file, logger);
            return logger;
        }
    }
}
