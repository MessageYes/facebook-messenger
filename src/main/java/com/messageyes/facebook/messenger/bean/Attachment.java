package com.messageyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Both {@link InboundMessage} and {@link OutboundMessage} can have an attachment. It can
 * be as simple as an image or as complicated as template.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Attachment {
    private String type;
    private OutboundPayload payload;
}
