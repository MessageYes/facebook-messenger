package com.replyyes.facebook.messenger.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * A {@link Callback} is composed of a list of Entry instances.
 * 
 * An Entry contains data specific to one or more messages. During moments of high load, the Facebook
 * Messenger platform may batch together several Entry instances that each contain multiple {@link Messaging}
 * instances into a single Callback. It is important to iterate through all Entry and Messaging
 * instances.
 *
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference#format
 * @see Callback
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {
    private String id;
    private Long time;
    private List<Messaging> messaging;
}
