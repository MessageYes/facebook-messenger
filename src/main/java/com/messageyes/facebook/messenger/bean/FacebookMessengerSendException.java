package com.messageyes.facebook.messenger.bean;

import lombok.Getter;

@Getter
public class FacebookMessengerSendException extends Exception {
    private static final long serialVersionUID = 5013033180081733121L;

    private final Long errorCode;
    private final Long errorSubCode;

    public FacebookMessengerSendException(ErrorPayload error) {
        super(error.getMessage());

        errorCode = error.getCode();
        errorSubCode = error.getErrorSubcode();
    }
}
