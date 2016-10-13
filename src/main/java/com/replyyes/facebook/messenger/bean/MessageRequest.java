package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Represents the request payload that needs to be sent to the Facebook API to trigger a message to be
 * sent to a user.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference#request
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class MessageRequest {
    private User recipient;
    private OutboundMessage message;
}
