package services;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.revolut.model.Account;

public class TestAccountService extends TestService {

    /*
    Scenario: test get user account
          return 200 OK
     */
    @Test
    public void testGetAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));
        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertTrue(account.getUserName().equals("esantamarina"));
    }

    /*
   Scenario: test get all user accounts
             return 200 OK
   */
    @Test
    public void testGetAllAccounts() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String jsonString = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(jsonString, Account[].class);
        assertThat(accounts, not(emptyArray()));
    }

    /*
    Scenario: test get account balance given account ID
              return 200 OK
    */
    @Test
    public void testGetAccountBalance() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String balance = EntityUtils.toString(response.getEntity());
        BigDecimal res = new BigDecimal(balance).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal db = new BigDecimal(100).setScale(2, RoundingMode.HALF_EVEN);
        assertTrue(res.equals(db));
    }

    /*
   Scenario: test create new user account
             return 200 OK
   */
    @Test
    public void testCreateAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        BigDecimal balance = new BigDecimal(10).setScale(2, RoundingMode.HALF_EVEN);
        Account acc = new Account("createdAccount", balance, "CNY");
        String jsonInString = mapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String jsonString = EntityUtils.toString(response.getEntity());
        Account aAfterCreation = mapper.readValue(jsonString, Account.class);
        assertThat(aAfterCreation.getUserName(), is(equalTo("createdAccount")));
        assertThat(aAfterCreation.getCurrencyCode(), is(equalTo("CNY")));
    }

    /*
    Scenario: delete valid user account
              return 200 OK
    */
    @Test
    public void testDeleteAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/6").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));
    }

    /*    Scenario: test delete non-existent account. return 404 NOT FOUND
              return 404 NOT FOUND
    */
    @Test
    public void testDeleteNonExistingAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(NOT_FOUND_RESPONSE_CODE)));
    }

}
