package io.github.novareseller.security.spi;

import io.github.novareseller.security.model.ClientInfo;

/**
 * @author: Bowen huang
 * @date: 2021/05/17
 */
public interface ClientInfoHolder {


    ClientInfo findClientInfo(int clientId);

}
