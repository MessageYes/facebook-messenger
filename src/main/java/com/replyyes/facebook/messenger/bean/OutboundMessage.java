package com.replyyes.facebook.messenger.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

/**
 * An OutboundMessage instance is referred to in {@link MessageRequest} along with a {@link User}, the recipient.
 * Together they form the payload that is sent to the Facebook API which will trigger a message to be sent to a user.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference/text-message
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OutboundMessage {
    private String text;
    private Attachment attachment;

    /**
     * List of {@link QuickReply} to be sent with messages. Optional.
     * https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
     */
    @JsonProperty("quick_replies")
    @JacksonXmlProperty(localName = "quick_reply")
    @JacksonXmlElementWrapper(localName ="quick_replies")
    private List<QuickReply> quickReplies;
}
