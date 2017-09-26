package com.messageyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents the response payload that is returned from the Facebook API whenever a message
 * is sent to a user
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference#response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {
    @JsonProperty("recipient_id")
    private String recipientId;

    @JsonProperty("message_id")
    private String messageId;
}
