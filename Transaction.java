import java.util.Date;
import java.io.Serializable;

public class Transaction implements Serializable
{
	String transactionType;
	int amount;
	Date date;
	int balance;
	int accountNumber;

	public Transaction(String transactionType, int amount, Date d, int bal, int accountNumber)
	{
		this.transactionType = transactionType;
		this.amount = amount;
		this.date = d;
		this.balance = bal;
		this.accountNumber = accountNumber;
	}

	public String getType()
	{
		return transactionType;
	}

	public void setType(String transactionType)
	{
		this.transactionType = transactionType;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public int getBalance()
	{
		return balance;
	}

	public void setBalance(int balance)
	{
		this.balance = balance;
	}
}
