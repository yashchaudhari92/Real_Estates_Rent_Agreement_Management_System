package com.rentagreement.service;

import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.dto.auth.LoginRequestDTO;
import com.rentagreement.dto.auth.LoginResponseDTO;
import com.rentagreement.entity.Broker;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    BrokerResponseDTO registerBroker(BrokerRegisterRequestDTO request);

}