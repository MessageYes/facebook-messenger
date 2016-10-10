package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The {@link OutboundPayload} for {@link Attachment} instances can have a list of Elements. For example,
 * template messages require elements.
 *
 * https://developers.facebook.com/docs/messenger-platform/send-api-reference
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Element {
    /**
     * Bubble title. Required.
     */
    private String title;
    /**
     * URL that is opened when bubble is tapped. Optional.
     */
    @JsonProperty("item_url")
    private String itemUrl;
    /**
     * Bubble image. Optional.
     */
    @JsonProperty("image_url")
    private String imageUrl;
    /**
     * Bubble subtitle. Optional.
     */
    private String subtitle;
}
