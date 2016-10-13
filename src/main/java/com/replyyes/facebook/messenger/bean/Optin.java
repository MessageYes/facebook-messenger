package com.replyyes.facebook.messenger.bean;

import lombok.Data;

/**
 * Wrapper for the data-ref field on the 'Send to Messenger' plugin. This field can be used
 * by developers to associate a click/tap event on the plugin with a callback.
 *
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/authentication
 */
@Data
public class Optin {
    /**
     * data-ref parameter that was defined with the entry point. Required.
     */
    private String ref;
}
