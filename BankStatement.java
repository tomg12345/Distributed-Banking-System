import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// a list of transactions within a given date range
public class BankStatement implements Statement
{
	List<Transaction> transactions;
	// start date
	Date from;
	// end date
	Date to;
	int accountNumber;

	public BankStatement(List<Transaction> t, Date from, Date to, int accountNumber)
	{
		this.transactions = t;
		this.from = from;
		this.to = to;
		this.accountNumber = accountNumber;
	}

	@Override
	public int getAccountnum()
	{
		return accountNumber;
	}

	@Override
	public Date getStartDate()
	{
		return from;
	}

	@Override
	public Date getEndDate()
	{
		return to;
	}

	@Override
	public String getAccoutName()
	{
		return ""+accountNumber;
	}

	@Override
	public List<Transaction> getTransations()
	{
		return transactions;
	}
}
