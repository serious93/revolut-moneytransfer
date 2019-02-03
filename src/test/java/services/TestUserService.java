package services;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.revolut.model.User;


public class TestUserService extends TestService {

    /*
      Scenario: test get user by given user name
                return 200 OK
   */
    @Test
    public void testGetUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/testing").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String jsonString = EntityUtils.toString(response.getEntity());
        User user = mapper.readValue(jsonString, User.class);
        assertThat(user.getUserName(), is(equalTo("testing")));
        assertThat(user.getEmailAddress(), is(equalTo("testing@revolut.com")));

    }

    /*
    Scenario: test get all users
              return 200 OK
     */
    @Test
    public void testGetAllUsers() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String jsonString = EntityUtils.toString(response.getEntity());
        User[] users = mapper.readValue(jsonString, User[].class);
        assertThat(users, not(emptyArray()));
    }

    /*
       Scenario: Create user using JSON
                 return 200 OK
    */
    @Test
    public void testCreateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/create").build();
        User user = new User();
        user.setUserName("createdUser");
        user.setEmailAddress("email-test@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));

        String jsonString = EntityUtils.toString(response.getEntity());
        User userAfterCreation = mapper.readValue(jsonString, User.class);
        assertThat(userAfterCreation.getUserName(), is(equalTo("createdUser")));
        assertThat(userAfterCreation.getEmailAddress(), is(equalTo("email-test@gmail.com")));
    }

    /*
        Scenario: Create user already existed using JSON
                  return 400 BAD REQUEST
    */
    @Test
    public void testCreateExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/create").build();
        User user = new User();
        user.setUserName("testing");
        user.setEmailAddress("testing@revolut.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(BAD_REQUEST_RESPONSE_CODE)));

    }

    /*
     Scenario: Update Existing User using JSON provided from client
               return 200 OK
     */
    @Test
    public void testUpdateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/2").build();
        User user = new User(2L, "updatedUser", "updatedEMail@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));
    }

    /*
    Scenario: Update non existed User using JSON provided from client
              return 404 NOT FOUND
    */
    @Test
    public void testUpdateNonExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/100").build();
        User user = new User(100L, "updatedUser", "updatedEMail@gmail.com");
        String jsonInString = mapper.writeValueAsString(user);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);

        String jsonString = EntityUtils.toString(response.getEntity());
        User userAfterUpdate = mapper.readValue(jsonString, User.class);
        assertThat(userAfterUpdate.getUserId(), is(equalTo(0L)));
    }

    /*
     Scenario: test delete user
                return 200 OK
    */
    @Test
    public void testDeleteUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/2").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(SUCCESSFUL_RESPONSE_CODE)));
    }

    /*
    Scenario: test delete non-existed user
              return 404 NOT FOUND
   */
    @Test
    public void testDeleteNonExistingUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/user/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, is(equalTo(NOT_FOUND_RESPONSE_CODE)));
    }


}
