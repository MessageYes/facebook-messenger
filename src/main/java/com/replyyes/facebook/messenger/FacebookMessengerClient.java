package com.replyyes.facebook.messenger;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyyes.facebook.messenger.bean.Attachment;
import com.replyyes.facebook.messenger.bean.Callback;
import com.replyyes.facebook.messenger.bean.Element;
import com.replyyes.facebook.messenger.bean.ErrorResponse;
import com.replyyes.facebook.messenger.bean.FacebookMessengerSendException;
import com.replyyes.facebook.messenger.bean.MessageRequest;
import com.replyyes.facebook.messenger.bean.MessageResponse;
import com.replyyes.facebook.messenger.bean.OutboundMessage;
import com.replyyes.facebook.messenger.bean.OutboundPayload;
import com.replyyes.facebook.messenger.bean.QuickReply;
import com.replyyes.facebook.messenger.bean.User;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A client for interacting with the Facebook Messenger API. Multiple pages can be supported as the
 * page access token is a parameter for methods that make API calls.
 */
@RequiredArgsConstructor
@Slf4j
public class FacebookMessengerClient {

    protected static final String DEFAULT_FACEBOOK_MESSAGE_ENDPOINT = "https://graph.facebook.com/v2.6/me/messages?access_token=";
    protected static final Integer DEFAULT_REQUEST_TIMEOUT = 30000;

    private static final String CALLBACK_OBJECT_PAGE = "page";

    private static final String ATTACHMENT_TYPE_IMAGE = "image";
    private static final String ATTACHMENT_TYPE_TEMPLATE = "template";

    private static final String PAYLOAD_TEMPLATE_TYPE_GENERIC = "generic";

    private static final int MAX_GENERIC_MESSAGE_TITLE_LENGTH = 80;
    private static final int MAX_GENERIC_MESSAGE_SUBTITLE_LENGTH = 80;

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @NonNull
    private String facebookMessageEndPoint;
    /**
     * Timeout in milliseconds for requests sent to the Facebook Message endpoint.
     */
    @NonNull
    private Integer requestTimeout;

    /**
     * This constructor makes use of the DEFAULT_FACEBOOK_MESSAGE_ENDPOINT
     */
    public FacebookMessengerClient() {
        this(DEFAULT_FACEBOOK_MESSAGE_ENDPOINT, DEFAULT_REQUEST_TIMEOUT);
    }

    public Callback deserializeCallback(@NonNull String callbackJSON) {
        checkArgument(StringUtils.isNotBlank(callbackJSON), "callbackJSON cannot be blank");

        try {
            Callback callback = OBJECT_MAPPER.readValue(callbackJSON, Callback.class);

            // Confirm that the callback is a page object. That is a requirement according to the
            // Facebook documentation:
            // https://developers.facebook.com/docs/messenger-platform/webhook-reference#format
            if (callback != null && CALLBACK_OBJECT_PAGE.equals(callback.getObject())) {
                return callback;
            }
            log.error("The callback JSON is not a page object: {}", callbackJSON);
        } catch (IOException e) {
            log.error("Error deserializing callback JSON: {}", callbackJSON, e);
        }
        return null;
    }

    /**
     * Validates the provided signature by comparing it to a HmacSHA1 encoded string generated using the
     * provided request body and app secret key.
     *
     * https://developers.facebook.com/docs/messenger-platform/webhook-reference#security
     */
    public boolean isValidRequest(@NonNull String appSecretKey, @NonNull String signature, @NonNull String requestBody) {
        checkArgument(StringUtils.isNotBlank(appSecretKey), "appSecretKey cannot be blank");
        checkArgument(StringUtils.isNotBlank(signature), "signature cannot be blank");

        try {
            byte[] sha1 = HmacUtils.hmacSha1(appSecretKey.getBytes(StandardCharsets.UTF_8), requestBody.getBytes(StandardCharsets.UTF_8));
            return StringUtils.equals("sha1=" + Hex.encodeHexString(sha1), signature);
        } catch (Exception e) {
            log.error("Failed to generate hex encoded HmacSHA1 for requestBody {}", requestBody, e);
            return false;
        }
    }

    /**
     * Sends a generic template message to the given recipient. There must be at least one {@link Element}
     * to send, and may (or may not) include up to 10 quick replies.
     *
     * https://developers.facebook.com/docs/messenger-platform/send-api-reference/generic-template
     */
    public MessageResponse sendGenericMessage(@NonNull String pageAccessToken, @NonNull String recipientId, @NonNull List<Element> elements, List<QuickReply> quickReplies) throws FacebookMessengerSendException {
        checkArgument(StringUtils.isNotBlank(pageAccessToken), "pageAccessToken cannot be blank");
        checkArgument(StringUtils.isNotBlank(recipientId), "recipientId cannot be blank");
        checkArgument(CollectionUtils.isNotEmpty(elements), "elements cannot be empty");

        checkArgument(CollectionUtils.size(quickReplies) <= 10, "Facebook only supports 10 or fewer quick replies per message");

        for (Element element : elements) {
            if (StringUtils.length(element.getTitle()) > MAX_GENERIC_MESSAGE_TITLE_LENGTH ||
                StringUtils.length(element.getSubtitle()) > MAX_GENERIC_MESSAGE_SUBTITLE_LENGTH) {
                log.warn("Element title or subtitle too long: {}", element);
            }
        }

        Attachment attachment = new Attachment();
        OutboundPayload payload = new OutboundPayload();
        payload.setTemplateType(PAYLOAD_TEMPLATE_TYPE_GENERIC);
        payload.setElements(elements);
        attachment.setPayload(payload);
        attachment.setType(ATTACHMENT_TYPE_TEMPLATE);

        OutboundMessage message = new OutboundMessage();
        message.setAttachment(attachment);
        if (CollectionUtils.isNotEmpty(quickReplies)) {
            message.setQuickReplies(quickReplies);
        }

        return sendOutboundMessage(pageAccessToken, recipientId, message);
    }

    /**
     * Sends an image attachment to the given recipient.
     *
     * https://developers.facebook.com/docs/messenger-platform/send-api-reference/image-attachment
     */
    public MessageResponse sendImageMessage(@NonNull String pageAccessToken, @NonNull String recipientId, @NonNull String imageURL) throws FacebookMessengerSendException {
        checkArgument(StringUtils.isNotBlank(pageAccessToken), "pageAccessToken cannot be blank");
        checkArgument(StringUtils.isNotBlank(recipientId), "recipientId cannot be blank");
        checkArgument(StringUtils.isNotBlank(imageURL), "imageURL cannot be blank");

        Attachment attachment = new Attachment();
        OutboundPayload payload = new OutboundPayload();
        payload.setUrl(imageURL);
        attachment.setPayload(payload);
        attachment.setType(ATTACHMENT_TYPE_IMAGE);

        OutboundMessage message = new OutboundMessage();
        message.setAttachment(attachment);

        return sendOutboundMessage(pageAccessToken, recipientId, message);
    }

    /**
     * Sends a text message to the given recipient.
     *
     * https://developers.facebook.com/docs/messenger-platform/send-api-reference/text-message
     */
    public MessageResponse sendTextMessage(@NonNull String pageAccessToken, @NonNull String recipientId, @NonNull String messageText) throws FacebookMessengerSendException {
        checkArgument(StringUtils.isNotBlank(pageAccessToken), "pageAccessToken cannot be blank");
        checkArgument(StringUtils.isNotBlank(recipientId), "recipientId cannot be blank");
        checkArgument(StringUtils.isNotBlank(messageText), "messageText cannot be blank");

        OutboundMessage message = new OutboundMessage();
        message.setText(messageText);

        return sendOutboundMessage(pageAccessToken, recipientId, message);
    }

    /**
     * Sends a message to the given recipient. The {@link OutboundMessage} is not validated in any
     * way. It is up to the caller to ensure that the attributes set on the OutboundMessage represent
     * a valid request payload that Facebook will accept.
     */
    public MessageResponse sendOutboundMessage(@NonNull String pageAccessToken, @NonNull String recipientId, @NonNull OutboundMessage message) throws FacebookMessengerSendException {
        checkArgument(StringUtils.isNotBlank(pageAccessToken), "pageAccessToken cannot be blank");
        checkArgument(StringUtils.isNotBlank(recipientId), "recipientId cannot be blank");

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId(recipientId);
        messageRequest.setRecipient(recipient);
        messageRequest.setMessage(message);

        return sendMessageRequest(pageAccessToken, messageRequest);
    }

    private MessageResponse sendMessageRequest(@NonNull String pageAccessToken, @NonNull MessageRequest messageRequest) throws FacebookMessengerSendException {

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(requestTimeout)
                .setConnectTimeout(requestTimeout)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(facebookMessageEndPoint + pageAccessToken);
            httpPost.setEntity(new StringEntity(OBJECT_MAPPER.writeValueAsString(messageRequest), ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                log.debug("Successfully sent message. messageRequest: {}", messageRequest);
                return OBJECT_MAPPER.readValue(response.getEntity().getContent(), MessageResponse.class);
            } else {
                log.info("Failed to send messageRequest: {} response: {}", messageRequest, response);
                ErrorResponse errorResponse = OBJECT_MAPPER.readValue(
                    response.getEntity().getContent(), ErrorResponse.class);
                throw new FacebookMessengerSendException(errorResponse.getError());
            }
        } catch (IOException e) {
            log.error("Error sending messageRequest: {}", messageRequest, e);
            return null;
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
    }
}
