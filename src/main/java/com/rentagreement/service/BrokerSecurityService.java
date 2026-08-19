package com.rentagreement.service;

import com.rentagreement.dto.broker.DeletePasswordRequestDTO;

public interface BrokerSecurityService {

    void setOrChangeDeletePassword(
            DeletePasswordRequestDTO request
    );

    boolean hasDeletePassword();

}