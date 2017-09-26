package com.messageyes.facebook.messenger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.messageyes.facebook.messenger.FacebookMessengerClient;
import com.messageyes.facebook.messenger.bean.Attachment;
import com.messageyes.facebook.messenger.bean.Callback;
import com.messageyes.facebook.messenger.bean.Element;
import com.messageyes.facebook.messenger.bean.Entry;
import com.messageyes.facebook.messenger.bean.ErrorPayload;
import com.messageyes.facebook.messenger.bean.ErrorResponse;
import com.messageyes.facebook.messenger.bean.FacebookMessengerSendException;
import com.messageyes.facebook.messenger.bean.InboundMessage;
import com.messageyes.facebook.messenger.bean.MessageRequest;
import com.messageyes.facebook.messenger.bean.MessageResponse;
import com.messageyes.facebook.messenger.bean.Messaging;
import com.messageyes.facebook.messenger.bean.OutboundMessage;
import com.messageyes.facebook.messenger.bean.OutboundPayload;
import com.messageyes.facebook.messenger.bean.QuickReply;
import com.messageyes.facebook.messenger.bean.User;

@PowerMockIgnore("javax.crypto.*")
@PrepareForTest(HttpClients.class)
public class FacebookMessengerClientTest extends PowerMockTestCase  {
    // The object value is a placeholder, %s, so it can be replaced with String.format.
    private static final String TEST_CALLBACK_JSON_FORMAT =
        "{\"object\":\"%s\"," +
          "\"entry\":[{" +
            "\"id\":\"test entry id\",\"time\":123456789,\"messaging\":[{" +
              "\"sender\":{\"id\":\"test sender id\"},\"recipient\":{\"id\":\"test recipient id\"}," +
              "\"timestamp\":987654321,\"message\":{\"mid\":\"mid.testmessage:testhello\",\"seq\":54,\"text\":\"test text message\"}" +
            "}]" +
          "}]" +
        "}";

    private static final String TEST_CALLBACK_NO_ENTRY_TIME =
        "{\"object\":\"page\"," +
          "\"entry\":[{" +
            "\"id\":\"test entry id\",\"messaging\":[{" +
              "\"sender\":{\"id\":\"test sender id\"},\"recipient\":{\"id\":\"test recipient id\"}," +
              "\"timestamp\":987654321,\"message\":{\"mid\":\"mid.testmessage:testhello\",\"seq\":54,\"text\":\"test text message\"}" +
            "}]" +
          "}]" +
        "}";

    private FacebookMessengerClient impl;
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse restResponse;
    private StatusLine restStatus;

    @BeforeMethod
    public void setup() throws Exception {
        PowerMockito.mockStatic(HttpClients.class);
        HttpClientBuilder httpClientBuilder = mock(HttpClientBuilder.class);
        when(HttpClients.custom()).thenReturn(httpClientBuilder);

        httpClient = mock(CloseableHttpClient.class);
        when(httpClientBuilder.build()).thenReturn(httpClient);

        restResponse = mock(CloseableHttpResponse.class);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(restResponse);

        restStatus = mock(StatusLine.class);
        when(restResponse.getStatusLine()).thenReturn(restStatus);

        impl = new FacebookMessengerClient();
    }

    @Test
    public void deserializeCallback() {
        Callback callback = impl.deserializeCallback(String.format(TEST_CALLBACK_JSON_FORMAT, "page"));

        assertEquals(callback.getObject(), "page");
        assertEquals(callback.getEntry().size(), 1);

        Entry entry = callback.getEntry().get(0);
        assertEquals(entry.getId(), "test entry id");
        assertEquals(entry.getTime().longValue(), 123456789L);
        assertEquals(entry.getMessaging().size(), 1);

        Messaging messaging = entry.getMessaging().get(0);
        assertEquals(messaging.getSender().getId(), "test sender id");
        assertEquals(messaging.getRecipient().getId(), "test recipient id");

        InboundMessage message = messaging.getMessage();
        assertEquals(message.getMid(), "mid.testmessage:testhello");
        assertEquals(message.getSeq().longValue(), 54L);
        assertEquals(message.getText(), "test text message");

    }

    @Test
    public void deserializeCallback_notPageObject() {
        assertNull(impl.deserializeCallback(String.format(TEST_CALLBACK_JSON_FORMAT, "not page object")));
    }

    @Test
    public void deserializeCallback_notJSON() {
        assertNull(impl.deserializeCallback("not json"));
    }

    @Test
    public void deserializeCallback_noEntryTime() {
        Callback callback = impl.deserializeCallback(TEST_CALLBACK_NO_ENTRY_TIME);

        assertEquals(callback.getObject(), "page");
        assertEquals(callback.getEntry().size(), 1);

        Entry entry = callback.getEntry().get(0);
        assertEquals(entry.getId(), "test entry id");

        assertNull(entry.getTime());

        assertEquals(entry.getMessaging().size(), 1);

        Messaging messaging = entry.getMessaging().get(0);
        assertEquals(messaging.getSender().getId(), "test sender id");
        assertEquals(messaging.getRecipient().getId(), "test recipient id");
    }

    @Test
    public void isValidRequest_match() {
        assertTrue(impl.isValidRequest("test app secret key", "sha1=e50ffabcd617e2e693ba706b9b02e6931cf931f3", "matching request body"));
    }

    @Test
    public void isValidRequest_mismatch() {
        assertFalse(impl.isValidRequest("test app secret key", "sha1=e50ffabcd617e2e693ba706b9b02e6931cf931f3", "mismatching request body"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void isValidRequest_blankKey() {
        impl.isValidRequest("", "sha1=e50ffabcd617e2e693ba706b9b02e6931cf931f3", "mismatching request body");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void isValidRequest_blankSignature() {
        impl.isValidRequest("test app secret key", "", "mismatching request body");
    }

    @Test
    public void sendGenericMessage_200Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(200);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessageId("msg-123");
        messageResponse.setRecipientId("test recipient id");

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        Element element = new Element();
        element.setImageUrl("test image url");
        element.setTitle("test element title");
        QuickReply quickReply = new QuickReply();
        quickReply.setTitle("test quick reply title");
        quickReply.setContentType("test content type");
        assertEquals(impl.sendGenericMessage("test_page_access_token", "test recipient id", ImmutableList.of(element),
            ImmutableList.of(quickReply)), messageResponse);

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);

        OutboundMessage message = new OutboundMessage();
        Attachment attachment = new Attachment();
        OutboundPayload payload = new OutboundPayload();
        payload.setTemplateType("generic");
        payload.setElements(ImmutableList.of(element));
        attachment.setType("template");
        attachment.setPayload(payload);
        message.setAttachment(attachment);
        message.setQuickReplies(ImmutableList.of(quickReply));
        messageRequest.setMessage(message);

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }

    @Test
    public void sendImageMessage_200Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(200);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessageId("msg-123");
        messageResponse.setRecipientId("test recipient id");

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        assertEquals(impl.sendImageMessage("test_page_access_token", "test recipient id", "test image url"), messageResponse);

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);

        OutboundMessage message = new OutboundMessage();
        Attachment attachment = new Attachment();
        OutboundPayload payload = new OutboundPayload();
        payload.setUrl("test image url");
        attachment.setType("image");
        attachment.setPayload(payload);
        message.setAttachment(attachment);
        messageRequest.setMessage(message);

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }

    @Test
    public void sendImageMessage_400Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(400);

        ErrorPayload error = new ErrorPayload();
        error.setCode(222L);
        error.setErrorSubcode(333L);
        error.setFbtraceId("fb_trace_id");
        error.setMessage("Error Sending Message");
        error.setType("Internal Error");

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(error);

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(errorResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        try {
            impl.sendImageMessage("test_page_access_token", "test recipient id", "test image url");
            fail("FacebookMessengerSendException expected");
        } catch (FacebookMessengerSendException e) {
            assertEquals(e.getMessage(), error.getMessage());
            assertEquals(e.getErrorCode(), error.getCode());
            assertEquals(e.getErrorSubCode(), error.getErrorSubcode());
        }

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);

        OutboundMessage message = new OutboundMessage();
        Attachment attachment = new Attachment();
        OutboundPayload payload = new OutboundPayload();
        payload.setUrl("test image url");
        attachment.setType("image");
        attachment.setPayload(payload);
        message.setAttachment(attachment);
        messageRequest.setMessage(message);

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }

    @Test
    public void sendTextMessage_200Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(200);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessageId("msg-123");
        messageResponse.setRecipientId("test recipient id");

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        assertEquals(impl.sendTextMessage("test_page_access_token", "test recipient id", "test message"), messageResponse);

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);

        OutboundMessage message = new OutboundMessage();
        message.setText("test message");
        messageRequest.setMessage(message);

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }

    @Test
    public void sendTextMessage_400Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(400);

        ErrorPayload error = new ErrorPayload();
        error.setCode(222L);
        error.setErrorSubcode(333L);
        error.setFbtraceId("fb_trace_id");
        error.setMessage("Error Sending Message");
        error.setType("Internal Error");

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(error);

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(errorResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        try {
            assertNull(impl.sendTextMessage("test_page_access_token", "test recipient id", "test message"));
            fail("FacebookMessengerSendException expected");
        } catch (FacebookMessengerSendException e) {
            assertEquals(e.getMessage(), error.getMessage());
            assertEquals(e.getErrorCode(), error.getCode());
            assertEquals(e.getErrorSubCode(), error.getErrorSubcode());
        }

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);

        OutboundMessage message = new OutboundMessage();
        message.setText("test message");
        messageRequest.setMessage(message);

        HttpPost httpPost = new HttpPost(FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        httpPost.setEntity(new StringEntity(FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest), ContentType.APPLICATION_JSON));

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }

    @Test
    public void sendOutboundMessage_200Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(200);

        OutboundMessage message = new OutboundMessage();
        message.setText("test message");

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessageId("msg-123");
        messageResponse.setRecipientId("test recipient id");

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        assertEquals(impl.sendOutboundMessage("test_page_access_token", "test recipient id", message), messageResponse);

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);
        messageRequest.setMessage(message);

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }

    @Test
    public void sendOutboundMessage_400Status() throws Exception {
        when(restStatus.getStatusCode()).thenReturn(400);

        ErrorPayload error = new ErrorPayload();
        error.setCode(222L);
        error.setErrorSubcode(333L);
        error.setFbtraceId("fb_trace_id");
        error.setMessage("Error Sending Message");
        error.setType("Internal Error");

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(error);

        String responsePayload = FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(errorResponse);
        when(restResponse.getEntity()).thenReturn(new StringEntity(responsePayload));

        OutboundMessage message = new OutboundMessage();
        message.setText("test message");

        try {
            assertNull(impl.sendOutboundMessage("test_page_access_token", "test recipient id", message));
            fail("FacebookMessengerSendException expected");
        } catch (FacebookMessengerSendException e) {
            assertEquals(e.getMessage(), error.getMessage());
            assertEquals(e.getErrorCode(), error.getCode());
            assertEquals(e.getErrorSubCode(), error.getErrorSubcode());
        }

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient, times(1)).execute(httpPostCaptor.capture());

        MessageRequest messageRequest = new MessageRequest();
        User recipient = new User();
        recipient.setId("test recipient id");
        messageRequest.setRecipient(recipient);
        messageRequest.setMessage(message);

        HttpPost httpPost = new HttpPost(FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        httpPost.setEntity(new StringEntity(FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest), ContentType.APPLICATION_JSON));

        assertEquals(httpPostCaptor.getValue().getURI().toString(), FacebookMessengerClient.DEFAULT_FACEBOOK_MESSAGE_ENDPOINT + "test_page_access_token");
        assertEquals(IOUtils.toString(httpPostCaptor.getValue().getEntity().getContent(), StandardCharsets.UTF_8),
            FacebookMessengerClient.OBJECT_MAPPER.writeValueAsString(messageRequest));
    }
}
