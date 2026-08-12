package com.rentagreement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentAgreementManagementApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                RentAgreementManagementApplication.class,
                args
        );

    }

}


//package com.rentagreement;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class RentAgreementManagementApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(RentAgreementManagementApplication.class, args);
//    }
//
//}
