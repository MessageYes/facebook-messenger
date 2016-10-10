package com.replyyes.facebook.messenger.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * The user ids that the Facebook Messenger platform uses are page-scoped IDs (PSID).
 * That means the user ids are unique for a given page.
 *
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
}
