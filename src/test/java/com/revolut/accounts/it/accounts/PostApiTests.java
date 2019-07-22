package com.revolut.accounts.it.accounts;

import com.revolut.accounts.dao.AccountDataStore;
import com.revolut.accounts.it.BaseIntegrationTest;
import com.revolut.accounts.util.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PostApiTests extends BaseIntegrationTest {

    // POST Accounts :: No Accept Header :: Default Response JSON Content
    @Test
    public void
    givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson()
            throws IOException {
        // Given
        HttpUriRequest request = new HttpPost(URL);

        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(Constants.APPLICATION_JSON, mimeType);
    }

    // POST Accounts :: Valid Account json in request body :: Create Account
    @Test
    public void
    givenPostRequestWithAccounttRequestBody_WhenExecuted_CreateAccount()
            throws IOException, ParseException {

        // Given
        HttpPost request = new HttpPost(URL);

        JSONObject account = new JSONObject();
        account.put("id", 13);
        account.put("name", "Lilly");
        account.put("balance", 123.34);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_ACCEPTED));
        assertEquals(13, AccountDataStore.getInstance().getAllAccounts().size());
    }

    // POST Accounts :: account without id but with name and amount in request body:: InvalidRequestException
    @Test
    public void
    givenPostAccountRequestWithoutIdParameters_WhenExecuted_Exception() throws IOException {

        // Given
        HttpPost request = new HttpPost(URL);

        JSONObject account = new JSONObject();
        account.put("name", "Lilly");
        account.put("balance", 123.34);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_ID_IS_NULL + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(12, AccountDataStore.getInstance().getAllAccounts().size());
    }

    // POST Accounts :: account without name but with id and amount in request body:: InvalidRequestException
    @Test
    public void
    givenPostAccountRequestWithoutNameParameters_WhenExecuted_Exception() throws IOException {

        // Given
        HttpPost request = new HttpPost(URL);

        JSONObject account = new JSONObject();
        account.put("id", 13);
        account.put("balance", 123.34);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_NAME_IS_NULL + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(12, AccountDataStore.getInstance().getAllAccounts().size());
    }

    // POST Accounts :: account without negative balance in request body:: InvalidRequestException
    @Test
    public void
    givenPostAccountRequestWithNegativeBalance_WhenExecuted_Exception() throws IOException {

        // Given
        HttpPost request = new HttpPost(URL);

        JSONObject account = new JSONObject();
        account.put("id", 13);
        account.put("name", "Karl");
        account.put("balance", -123.34);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_BALANCE_IS_INVALID + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(12, AccountDataStore.getInstance().getAllAccounts().size());
    }

    // POST Accounts :: account with id already in data store :: ResourceAlreadyExistsException
    @Test
    public void
    givenPostAccountRequestWithAlreadyPresentId_WhenExecuted_Exception() throws IOException {

        // Given
        HttpPost request = new HttpPost(URL);

        JSONObject account = new JSONObject();
        account.put("id", 9);
        account.put("name", "Karl");
        account.put("balance", 123.34);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CONFLICT));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_USER_ALREADY_EXISTS + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(12, AccountDataStore.getInstance().getAllAccounts().size());
    }

    // POST Accounts :: account with string id  :: InvalidRequestException
    @Test
    public void
    givenPostAccountRequestWithStringId_WhenExecuted_Exception() throws IOException {

        // Given
        HttpPost request = new HttpPost(URL);

        JSONObject account = new JSONObject();
        account.put("id", "asd");
        account.put("name", "Karl");
        account.put("balance", 123.34);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_INVALID_REQUEST + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(12, AccountDataStore.getInstance().getAllAccounts().size());
    }

    // POST Accounts :: No request Body :: InvalidRequestException
    @Test
    public void
    givenPostAccountRequestWithoutRequestBody_WhenExecuted_Exception() throws IOException {

        // Given
        HttpPost request = new HttpPost(URL);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_INVALID_REQUEST + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(12, AccountDataStore.getInstance().getAllAccounts().size());
    }

}
