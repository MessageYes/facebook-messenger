package com.messageyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Represents the response object that is returned from the Facebook API whenever a message
 * fails to be sent to a user.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference/errors
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private ErrorPayload error;
}
