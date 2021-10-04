// exception for when session is timed out
public class InvalidSession extends Exception
{
	public InvalidSession(String str)
	{
		super(str);
	}
}
