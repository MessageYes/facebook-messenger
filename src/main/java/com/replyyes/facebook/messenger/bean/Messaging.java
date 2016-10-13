package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * An {@link Entry} contains one or Messaging instances. Data about a specific message is
 * contained within this class.
 *
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference
 * @see Entry
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Messaging {
    /**
     * Sender of the message. Required.
     */
    private User sender;

    /**
     * Recipient of the message. Required.
     */
    private User recipient;

    /**
     * Every message event except for message delivered will have the timestamp set.
     * Optional.
     */
    private Long timestamp;

    /**
     * Set if the callback pertains to an inbound message. Optional.
     */
    private InboundMessage message;

    /**
     * Postbacks occur when a Postback button, Get Started button, Persistent menu or
     * Structured Message is tapped.
     * https://developers.facebook.com/docs/messenger-platform/webhook-reference/postback-received
     */
    private InboundPayload postback;

    /**
     * Set if the callback pertains to a 'Send to Messenger' plugin click/tap. Optional.
     * https://developers.facebook.com/docs/messenger-platform/plugin-reference/send-to-messenger
     */
    private Optin optin;

    /**
     * Set if the callback pertains to a status update that message(s) were delivered. Optional.
     */
    private StatusUpdate delivery;

    /**
     * Set if the callback pertains to a status update that message(s) were read. Optional.
     */
    private StatusUpdate read;
}
