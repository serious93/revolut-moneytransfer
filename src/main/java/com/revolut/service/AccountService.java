package com.revolut.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.dao.HibernateUtil;
import com.revolut.dao.impl.AccountDAOImpl;
import com.revolut.model.Account;

/**
 * Account Service 
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
	
    private final AccountDAOImpl accountDAO = new AccountDAOImpl(HibernateUtil.getSessionFactory());
    
    /**
     * Find all accounts
     * @return
     */
    @GET
    @Path("/all")
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    /**
     * Find by account id
     * @param accountId
     * @return
     */
    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId){
        return accountDAO.findById(accountId, Account.class);
    }
    
    /**
     * Find balance by account Id
     * @param accountId
     * @return
     */
    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) {
        final Account account = accountDAO.findById(accountId, Account.class);
        if(account == null){
            throw new WebApplicationException("Account not found " + accountId, Response.Status.NOT_FOUND);
        }
        return account.getBalance();
    }
    
    /**
     * Create Account
     * @param account
     * @return
     */
    @PUT
    @Path("/create")
    public Account createAccount(Account account){
        accountDAO.save(account);
        return accountDAO.findById(account.getAccountId(), Account.class);
    }

    /**
     * Deposit amount by account Id
     * @param accountId
     * @param amount
     * @return
     */
    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Account deposit(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        return updateAccountBalance(accountId, amount);
    }

    /**
     * Withdraw amount by account Id
     * @param accountId
     * @param amount
     * @return
     */
    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Account withdraw(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        Account account = accountDAO.findById(accountId, Account.class);
        if (account != null) {
            BigDecimal balance = account.getBalance();
            if (balance.compareTo(amount) < 0) {
                throw new WebApplicationException("Not sufficient Fund for account: " + accountId);
            }
            return updateAccountBalance(accountId, amount.negate());
        }
        else
        {
            throw new WebApplicationException("Account not found: " + accountId);
        }
    }

    public Account updateAccountBalance(long accountId, BigDecimal amount){
        Account targetAccount = accountDAO.findById(accountId, Account.class);
        targetAccount.setBalance(targetAccount.getBalance().add(amount));
        return accountDAO.update(targetAccount);
    }

    /**
     * Delete account by account Id
     * @param accountId
     * @return
     */
    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId){
        try {
            accountDAO.delete(accountDAO.findById(accountId, Account.class));
            return Response.status(Response.Status.OK).build();
        }
       catch (Exception e) {
           return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
