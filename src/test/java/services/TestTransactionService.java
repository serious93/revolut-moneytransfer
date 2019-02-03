package services;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.revolut.model.Account;
import com.revolut.model.UserTransaction;

public class TestTransactionService extends TestService {

    /*
   Scenario: test deposit money to given account number
             return 200 OK
    */
    @Test
    public void testDeposit() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/5/deposit/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        assertThat(afterDeposit.getBalance(), is(equalTo(new BigDecimal(100).setScale(2, RoundingMode.HALF_EVEN))));

    }

    /*
      Scenario: test withdraw money from account given account number, account has sufficient fund
                return 200 OK
    */
    @Test
    public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/3/withdraw/50").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        assertThat(afterDeposit.getBalance(), is(equalTo(new BigDecimal(150).setScale(2, RoundingMode.HALF_EVEN))));
    }

    /*
       Scenario: test withdraw money from account given account number, no sufficient fund in account
                 return 500 INTERNAL SERVER ERROR
    */
    @Test
    public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/1000").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(ERROR_RESPONSE_CODE)));
    }

    /*
       Scenario: test transaction from one account to another with source account has sufficient fund
                 return 200 OK
    */
    @Test
    public void testTransactionEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(10).setScale(2, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction("EUR", amount, 2L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));
    }

    /*
        Scenario: test transaction from one account to another with source account has no sufficient fund
                  return 500 INTERNAL SERVER ERROR
    */
    @Test
    public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100000).setScale(2, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction("EUR", amount, 1L, 2L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(ERROR_RESPONSE_CODE)));
    }

}
