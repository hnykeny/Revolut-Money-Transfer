package com.revolut.accounts.it.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accounts.Application;
import com.revolut.accounts.it.BaseIntegrationTest;
import com.revolut.accounts.util.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GetApiTests extends BaseIntegrationTest {

    @BeforeClass
    public static void setupServer() throws IOException {
        Application.main(new String[]{String.valueOf(PORT)});
    }

    // GET Accounts :: No Accept Header :: Default Response JSON Content
    @Test
    public void
    givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson()
            throws IOException {
        // Given
        HttpUriRequest request = new HttpGet(URL);

        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(Constants.APPLICATION_JSON, mimeType);
    }

    // GET Accounts	:: No request parameters :: All accounts in datastore
    @Test
    public void
    givenGetAccountsRequestWithoutRequestParameters_WhenExecuted_ListOfAccountsReceived()
            throws IOException, ParseException {
        // Given
        HttpUriRequest request = new HttpGet(URL);

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        ObjectMapper mapper = new ObjectMapper();
        JSONArray accounts =
                (JSONArray) jsonParser.parse(new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(accounts.size(), 12);
    }

    // GET Accounts	:: some request parameters without id :: Invalid Request Exception
    @Test
    public void
    givenGetAccountRequestWithoutIdParameters_WhenExecuted_Exception() throws IOException {

        // Given
        HttpUriRequest request = new HttpGet(URL + "/as32");

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_ID_IS_NOT_VALID + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
    }

    // GET Accounts	:: valid id in request parameter :: Return Account
    @Test
    public void
    givenGetAccountRequestWithValidIdParameter_WhenExecuted_ReturnAccount() throws IOException, ParseException {

        // Given
        HttpUriRequest request = new HttpGet(URL + "/9");

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        JSONObject account =
                (JSONObject) jsonParser.parse(new String(httpResponse.getEntity().getContent().readAllBytes()));
        //Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(account.get("id"), 9L);
        assertEquals(account.get("name"), "Max");
    }


    // GET Accounts	:: Invalid id in request parameter :: ResourceNotFoundException
    @Test
    public void
    givenGetAccountRequestWithInvalidIdParameter_WhenExecuted_Exception() throws IOException {

        // Given
        HttpUriRequest request = new HttpGet(URL + "/234");

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_USER_NOT_FOUND + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
    }

    // GET Accounts	:: string id in request parameter :: InvalidRequestException
    @Test
    public void
    givenGetAccountRequestWithStringIdParameter_WhenExecuted_Exception() throws IOException {

        // Given
        HttpUriRequest request = new HttpGet(URL + "/asd");

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_ID_IS_NOT_VALID + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
    }
}
