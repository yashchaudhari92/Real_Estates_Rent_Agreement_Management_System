//package com.rentagreement.service.impl;
//
//import com.rentagreement.service.SmsService;
//import com.twilio.Twilio;
//import com.twilio.exception.ApiException;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsServiceImpl implements SmsService {
//
//    private static final Logger logger =
//            LoggerFactory.getLogger(SmsServiceImpl.class);
//
//    private final String accountSid;
//    private final String authToken;
//    private final String fromPhoneNumber;
//
//    public SmsServiceImpl(
//            @Value("${twilio.account-sid}") String accountSid,
//            @Value("${twilio.auth-token}") String authToken,
//            @Value("${twilio.phone-number}") String fromPhoneNumber
//    ) {
//
//        this.accountSid = accountSid;
//        this.authToken = authToken;
//        this.fromPhoneNumber = fromPhoneNumber;
//
//        Twilio.init(
//                accountSid,
//                authToken
//        );
//
//        logger.info("Twilio SMS service initialized successfully.");
//
//    }
//
//    @Override
//    public void sendSms(
//            String mobileNumber,
//            String message
//    ) {
//
//        if (mobileNumber == null ||
//                mobileNumber.isBlank()) {
//
//            throw new IllegalArgumentException(
//                    "Mobile number cannot be empty."
//            );
//        }
//
//        if (message == null ||
//                message.isBlank()) {
//
//            throw new IllegalArgumentException(
//                    "SMS message cannot be empty."
//            );
//        }
//
//        try {
//
//            Message twilioMessage =
//                    Message.creator(
//
//                            new PhoneNumber(
//                                    mobileNumber
//                            ),
//
//                            new PhoneNumber(
//                                    fromPhoneNumber
//                            ),
//
//                            message
//
//                    ).create();
//
//            logger.info(
//                    "SMS sent successfully. To: {}, SID: {}",
//                    mobileNumber,
//                    twilioMessage.getSid()
//            );
//
//        } catch (ApiException exception) {
//
//            logger.error(
//                    "Failed to send SMS to: {}",
//                    mobileNumber,
//                    exception
//            );
//
//            throw exception;
//
//        }
//
//    }
//
//}