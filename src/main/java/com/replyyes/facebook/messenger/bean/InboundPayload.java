package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * A {@link Callback} potentially may have a payload. This payload contains metadata specific to the
 * messenger bot. It can be set in a previous @{link OutboundMessage} as with @{link QuickReply} or app
 * configuration such as the Getting Started button or static menu.
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class InboundPayload {
    /**
     * Custom data that will be sent back via webhook.
     */
    private String payload;
}
