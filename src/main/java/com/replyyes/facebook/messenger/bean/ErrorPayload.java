package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The payload of an {@link ErrorResponse} that gets returned by the Facebook API whenever a message
 * fails to be sent to a user.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference/errors
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ErrorPayload {
    private String message;

    private String type;

    private Long code;

    @JsonProperty("error_subcode")
    private Long errorSubcode;

    @JsonProperty("fbtrace_id")
    private String fbtraceId;
}
