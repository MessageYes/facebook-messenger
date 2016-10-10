package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * A button that can be used in the button template message.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference/button-template
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Button {
    /**
     * Value is web_url, postback or phone_number. Required.
     */
    private String type;

    /**
     * Button title. Required.
     */
    private String title;

    /**
     * For web_url buttons, this URL is opened in a mobile browser when the button is tapped.
     * Required if type is web_url
     */
    private String url;

    /**
     * For postback buttons, this data will be sent back to you via webhook. For phone_number
     * buttons, this must be a well formatted phone number.
     * Required if type is postback or phone_number.
     */
    private String payload;
}
