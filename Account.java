import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Bank account class

public class Account implements Serializable
{

	private int accountNumber;					// Account number
	private int accountBalance;					// Account balance
	private List<Transaction> transactions = new ArrayList<Transaction>();	// History of account transactions

	// Constructor
	public Account(int accountNumber, int accountBalance)
	{
		this.accountNumber = accountNumber;
		this.accountBalance = accountBalance;
	}

	// Deposit money in user bank account
	public void deposit(int deposit)
	{
		// make sure deposit is greater than zero
		if (deposit > 0)
		{
			accountBalance += deposit;

			// add transaction to history
			addTransaction(new Transaction("Deposit", deposit, new Date(), accountBalance, accountNumber));
		}
	}

	// Withdraw money from user bank account
	public boolean withdraw(int withdraw)
	{
		// check that there is enough money in account to withdraw
		int balance = accountBalance - withdraw;
		if (balance >= 0)
		{
			accountBalance = balance;

			// add transaction to history
			addTransaction(new Transaction("Withdrawal", withdraw, new Date(), accountBalance, accountNumber));
			return true;
		}
		return false;
	}

	// Check balance of user bank account
	public int checkBalance()
	{
		return accountBalance;
	}

	// Return statement to user recording transactions from defined time period
	public Statement getStatement(Date from, Date to)
	{
		// list to store desired transactions
		List<Transaction> desiredTransactions = new ArrayList<Transaction>();

		// loop through the transactions
		for (Transaction t: transactions)
		{
			Date date = t.getDate();

			// if in date range
			if(date.after(from) && date.before(to))
			{
				desiredTransactions.add(t);
			}
		}

		// make a statenebt with desired transactions
		BankStatement statement = new BankStatement(desiredTransactions, from, to, accountNumber);
		return statement;
	}

	// Add transaction to history
	public void addTransaction(Transaction t)
	{
		transactions.add(t);
	}


	//Getter and setter methods
	public int getAccNum()
	{
		return accountNumber;
	}

	public void setAccNum(int accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public int getAmt()
	{
		return accountBalance;
	}

	public void setAmt(int accountBalance)
	{
		this.accountBalance = accountBalance;
	}
}
