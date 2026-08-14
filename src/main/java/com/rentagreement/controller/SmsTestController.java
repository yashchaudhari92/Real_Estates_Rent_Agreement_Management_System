//package com.rentagreement.controller;
//
//import com.rentagreement.service.SmsService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/test/sms")
//public class SmsTestController {
//
//    private final SmsService smsService;
//
//    public SmsTestController(
//            SmsService smsService
//    ) {
//
//        this.smsService = smsService;
//
//    }
//
//    @PostMapping
//    public ResponseEntity<String> sendTestSms(
//            @RequestParam String mobileNumber
//    ) {
//
//        smsService.sendSms(
//
//                mobileNumber,
//
//                "Test SMS from Rent Agreement Management System. "
//                        + "Twilio SMS integration is working successfully."
//
//        );
//
//        return ResponseEntity.ok(
//                "Test SMS sent successfully."
//        );
//
//    }
//
//}