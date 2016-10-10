package com.replyyes.facebook.messenger.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * StatusUpdate is a type of event that can be received in a {@link Callback} @{link Entry}. It represents
 * a status update of "delivered" or "read" on outbound message(s) sent to a user.
 *
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/message-delivered
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/message-read
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusUpdate {
	/**
	 * The list of messages to update may or may not be populated. If not, the watermark must be used.
	 */
    private List<String> mids;

    private Long watermark;
    private Long seq;
}
