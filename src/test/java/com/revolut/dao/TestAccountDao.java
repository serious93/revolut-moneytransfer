package com.revolut.dao;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revolut.dao.impl.AccountDAOImpl;
import com.revolut.model.Account;

public class TestAccountDao {

	private static AccountDAOImpl accountDAO;

	private static final BigDecimal BALANCE = new BigDecimal(100.00).setScale(2, RoundingMode.HALF_EVEN);

	@BeforeClass
	public static void setup() {
		accountDAO = new AccountDAOImpl(HibernateUtil.getSessionFactory());
	}

	@Test
	public void testGetAccountById() {
		Account account = accountDAO.findById(1L, Account.class);
		assertThat(account.getBalance(), is(equalTo(BALANCE)));
	}

    @Test
    public void testCreateAccount() {
        BigDecimal balance = new BigDecimal(10).setScale(2, RoundingMode.HALF_EVEN);
        Account account = new Account("testingAccount", balance, "CNY");
        accountDAO.save(account);
        Account afterCreation = accountDAO.findById(account.getAccountId(), Account.class);
        assertThat(afterCreation.getUserName(), is(equalTo("testingAccount")));
        assertThat(afterCreation.getCurrencyCode(), is(equalTo("CNY")));
        assertThat(afterCreation.getBalance(), is(equalTo(balance)));
    }

    @Test
    public void testGetAllAccounts() {
        List<Account> allAccounts = accountDAO.getAllAccounts();
        assertThat(allAccounts, not(empty()));
    }


	/*@Test
	public void testGetNonExistingAccById() throws CustomException {
		Account account = h2DaoFactory.getAccountDAO().getAccountById(100L);
		assertTrue(account == null);
	}



	@Test
	public void testDeleteAccount() throws CustomException {
		int rowCount = h2DaoFactory.getAccountDAO().deleteAccountById(2L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(2L) == null);
	}

	@Test
	public void testDeleteNonExistingAccount() throws CustomException {
		int rowCount = h2DaoFactory.getAccountDAO().deleteAccountById(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

	@Test
	public void testUpdateAccountBalanceSufficientFund() throws CustomException {

		BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdated = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaDeposit);
		assertTrue(rowsUpdated == 1);
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(1L).getBalance().equals(afterDeposit));
		BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 1);
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(1L).getBalance().equals(afterWithDraw));

	}

	@Test(expected = CustomException.class)
	public void testUpdateAccountBalanceNotEnoughFund() throws CustomException {
		BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 0);

	}
 */
}
