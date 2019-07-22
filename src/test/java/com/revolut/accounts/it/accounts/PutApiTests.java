package com.revolut.accounts.it.accounts;

import com.revolut.accounts.dao.AccountDataStore;
import com.revolut.accounts.it.BaseIntegrationTest;
import com.revolut.accounts.util.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPut;
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

public class PutApiTests extends BaseIntegrationTest {

    // PUT Accounts :: No Accept Header :: Default Response JSON Content
    @Test
    public void
    givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson()
            throws IOException {
        // Given
        HttpUriRequest request = new HttpPut(URL);

        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(Constants.APPLICATION_JSON, mimeType);
    }

    // PUT Accounts :: Valid transfer object with amount less than customer balance :: Transfer Balance
    @Test
    public void
    givenPuttRequestWithAmountTransferRequestBody_WhenExecuted_TransferAmount()
            throws IOException, ParseException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", 8); // Balance: 6000.50
        account.put("amount", 2500.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_ACCEPTED));
        assertEquals(15000.00d - 2500.75d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d + 2500.75d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: Valid transfer object with amount greater than customer balance :: InsufficientBalanceException
    @Test
    public void
    givenPuttRequestWithAmountGreaterThanCustomerBalance_WhenExecuted_Exception()
            throws IOException, ParseException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", 8); // Balance: 6000.50
        account.put("amount", 25000.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CONFLICT));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_INSUFFICIENT_BALANCE + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object with customer id == beneficiary id :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithCustomerIdEqualsBeneficiaryId_WhenExecuted_Exception()
            throws IOException, ParseException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", 5);
        account.put("amount", 5000.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_CUSTOMER_BENEFICIARY_SAME + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object with invalid customer id :: ResourceNotFoundException
    @Test
    public void
    givenPuttRequestWithInvalidCustomerId_WhenExecuted_Exception()
            throws IOException, ParseException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 15);
        account.put("beneficiaryId", 8); // Balance: 6000.50
        account.put("amount", 5000.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_USER_NOT_FOUND + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object with invalid beneficiary id :: ResourceNotFoundException
    @Test
    public void
    givenPuttRequestWithInvalidBeneficiaryId_WhenExecuted_Exception()
            throws IOException, ParseException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", 18);
        account.put("amount", 5000.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_USER_NOT_FOUND + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object with negative amount :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithNegativeAmount_WhenExecuted_Exception()
            throws IOException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", 8); // Balance: 6000.50
        account.put("amount", -500.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_AMOUNT_IS_INVALID + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object without amount :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithoutAmount_WhenExecuted_Exception()
            throws IOException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", 8); // Balance: 6000.50

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_AMOUNT_IS_INVALID + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object without customer id :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithoutCustomerId_WhenExecuted_Exception()
            throws IOException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("beneficiaryId", 8); // Balance: 6000.50
        account.put("amount", -500.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_CUSTOMER_IS_NULL + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        //assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object without beneficiary id :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithoutBeneficiaryId_WhenExecuted_Exception()
            throws IOException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("amount", -500.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_BENEFICIARY_IS_NULL + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        //assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object with string beneficiary id :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithStringBeneficiaryId_WhenExecuted_Exception()
            throws IOException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", 5); // Balance: 15000.00
        account.put("beneficiaryId", "test_id");
        account.put("amount", 500.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_INVALID_REQUEST + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        //assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }

    // PUT Accounts :: transfer object with string customer id :: InvalidRequestException
    @Test
    public void
    givenPuttRequestWithStringCustomerId_WhenExecuted_Exception()
            throws IOException {

        // Given
        HttpPut request = new HttpPut(URL);

        JSONObject account = new JSONObject();
        account.put("customerId", "test_id");
        account.put("beneficiaryId", 8); // Balance: 6000.50
        account.put("amount", 500.75);

        request.setEntity(new StringEntity(account.toJSONString()));

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals("{\"message\":\"" + Constants.EXCEPTION_MESSAGE_INVALID_REQUEST + "\"}",
                new String(httpResponse.getEntity().getContent().readAllBytes()));
        //assertEquals(15000.00d, AccountDataStore.getInstance().getAccount(5L).getBalance(), 0);
        assertEquals(6000.50d, AccountDataStore.getInstance().getAccount(8L).getBalance(), 0);
    }
}
