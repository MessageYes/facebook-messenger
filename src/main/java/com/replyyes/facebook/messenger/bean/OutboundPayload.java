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
 * The data for an {@link Attachment} is contained within this class. There several types of attachments:
 * <ul>
 *   <li>image</li>
 *   <li>audio</li>
 *   <li>video</li>
 *   <li>file</li>
 *   <li>template message</li>
 * </ul>
 *
 * Consequently, the OutboundPayload class has a fair bit of flexibility in order to represent those types.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OutboundPayload {
    /**
     * URL location of the attachment. Used by the following:
     * - image
     * - audio
     * - video
     * - file
     */
    private String url;

    /**
     * The message template type. Used by message templates.
     */
    @JsonProperty("template_type")
    private String templateType;

    /**
     * A text message. Used by the button template.
     */
    private String text;

    /**
     * The buttons displayed in a message. Used by the button template.
     */
    @JacksonXmlProperty(localName = "button")
    @JacksonXmlElementWrapper(localName = "buttons")
    private List<Button> buttons;

    /**
     * Contains data rendered in message templates.
     */
    @JacksonXmlProperty(localName = "element")
    @JacksonXmlElementWrapper(localName = "elements")
    private List<Element> elements;
}
