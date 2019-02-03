package com.revolut.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.revolut.dao.HibernateUtil;
import com.revolut.dao.impl.AccountDAOImpl;
import com.revolut.model.Account;
import com.revolut.model.UserTransaction;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

    private final AccountService accountService = new AccountService();

    private final AccountDAOImpl accountDAO = new AccountDAOImpl(HibernateUtil.getSessionFactory());

    /**
	 * Transfer fund between two accounts.
	 * @param transaction
	 * @return
	 */
	@POST
	public Response transferFund(UserTransaction transaction) {
        Account fromAccount = accountDAO.findById(transaction.getFromAccountId(), Account.class);
        Account toAccount = accountDAO.findById(transaction.getToAccountId(), Account.class);
        if ((fromAccount != null) && (toAccount != null) && (fromAccount.getBalance().compareTo(transaction.getAmount()) > 0)) {
            accountService.updateAccountBalance(transaction.getFromAccountId(), transaction.getAmount().negate());
            accountService.updateAccountBalance(transaction.getToAccountId(), transaction.getAmount());
            return Response.status(Response.Status.OK).build();
        } else {
            throw new WebApplicationException("Transaction failed");
        }
    }

}
