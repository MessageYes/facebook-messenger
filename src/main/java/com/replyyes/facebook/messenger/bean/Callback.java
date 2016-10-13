package com.replyyes.facebook.messenger.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * All callbacks from the Facebook messenger platform have a common structure. This
 * class represents that structure at its highest level. It contains one or more {@link Entry}
 * instances, each of which has the potential to contain one or more {@link Messaging} instances.
 *
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference#format
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Callback {
    private String object;
    private List<Entry> entry;
}
