// exception for when user attempts to withdraw more money in balance
public class InsufficientFunds extends Exception
{
	public InsufficientFunds(String m){
		super(m);
	}
}
