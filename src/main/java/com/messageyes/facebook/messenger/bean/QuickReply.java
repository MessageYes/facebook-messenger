package com.messageyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * QuickReply provides a new way to present buttons to the user. When a button is tapped, the message is
 * sent in the conversation with developer-defined metadata in the {@link Callback}. After a button is
 * tapped, the buttons are dismissed preventing the issue where users could tap on buttons attached to
 * old messages in a conversation.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class QuickReply {
    /**
     * Value must be text. Required.
     */
    @JsonProperty("content_type")
    private String contentType = "text";

    /**
     * Caption of button. Required.
     */
    private String title;

    /**
     * Custom data that will be sent back via webhook. Required.
     */
    private String payload;
}
