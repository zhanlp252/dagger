package io.github.novareseller.security.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: Bowen huang
 * @date: 2021/05/17
 */
@Getter
@Setter
public class ClientInfo {


    private static final long serialVersionUID = 0x6bb7e943ad0c6f24L;


    /**
     * The identifier of each app. It is specified, not automatically generated.
     */
    private Integer id;

    /**
     * The name of each app.
     */
    private String name;

    /**
     * The secret key of each app.
     * This key is for sign the request before invoking the APIs of payment gateway.
     */
    private String secretKey;

    /**
     * The url of each app to receive the notification from payment gateway,
     * while the payment status is changed.
     * This url is used only when the notification url is not specified in the payment.
     */
    private String notificationUrl;

    /**
     * The strategy of choice account before making payment.
     */
    private int accountStrategy;
}
