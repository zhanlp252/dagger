package io.github.novareseller.boot.security;

import io.github.novareseller.security.model.ClientInfo;
import io.github.novareseller.security.spi.ClientInfoHolder;
import org.springframework.stereotype.Service;

/**
 * @author: Bowen huang
 * @date: 2021/05/17
 */
@Service
public class TestClientInfoHolder implements ClientInfoHolder {

    @Override
    public ClientInfo findClientInfo(int clientId) {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setId(1000);
        return clientInfo;
    }
}
