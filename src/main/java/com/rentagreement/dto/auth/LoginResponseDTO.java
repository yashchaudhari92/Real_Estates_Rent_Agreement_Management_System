package com.rentagreement.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private Long id;

    private String token;

    private String username;

    private String role;

    private boolean mainBroker;
}


//package com.rentagreement.dto.auth;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class LoginResponseDTO {
//
//    private Long id;
//
//    private String token;
//
//    private String username;
//
//    private String role;
//
//}