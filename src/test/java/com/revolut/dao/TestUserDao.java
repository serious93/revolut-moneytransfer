package com.revolut.dao;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revolut.dao.impl.UserDAOImpl;
import com.revolut.model.User;

public class TestUserDao {

	private static UserDAOImpl userDAO;

	@BeforeClass
	public static void setup() {
		userDAO = new UserDAOImpl(HibernateUtil.getSessionFactory());
	}

	@Test
	public void testCreateUser() {
		User savedUser = new User();
		savedUser.setUserName("Esteban");
		savedUser.setEmailAddress("esteban.santamarina");
		userDAO.save(savedUser);
		User userAfterInsert = userDAO.findById(savedUser.getUserId(), User.class);
		assertThat(userAfterInsert.getUserName(), is(equalTo("Esteban")));
		assertThat(userAfterInsert.getEmailAddress(), is(equalTo("esteban.santamarina")));
	}

	@Test
	public void testGetAllUsers() {
		List<User> allUsers = userDAO.getAllUsers();
		assertThat(allUsers, not(empty()));
	}

	@Test
	public void testGetUserById() {
		User retrievedUser = userDAO.findById(2L, User.class);
		assertTrue(retrievedUser.getUserName().equals("revolutUser"));
	}

	@Test
	public void testGetNonExistingUserById() {
	    User retrievedUser = userDAO.findById(10L, User.class);
        assertThat(retrievedUser, is(nullValue()));
	}

	@Test
	public void testGetNonExistingUserByName() {
        User retrievedUser = userDAO.getUserByName("esantamarina_failed");
        assertThat(retrievedUser, is(nullValue()));
	}

	@Test
	public void testUpdateUser() {
		User updatedUser = new User(1L, "esantamarina", "emailUpdated@gmail.com");
        updatedUser = userDAO.update(updatedUser);
        userDAO.update(updatedUser);
        User userAfterUpdate = userDAO.findById(updatedUser.getUserId(), User.class);
        assertThat(userAfterUpdate.getEmailAddress(), is(equalTo("emailUpdated@gmail.com")));
	}

	@Test
	public void testDeleteUser() {
        User deletedUser = userDAO.findById(1L, User.class);
		userDAO.delete(deletedUser);
        deletedUser = userDAO.findById(1L, User.class);
        assertThat(deletedUser, is(nullValue()));
	}

}
