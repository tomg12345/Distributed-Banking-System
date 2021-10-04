import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// Bank Server Class
public class Bank extends UnicastRemoteObject implements BankInterface
{
	// Users accounts
	private List<Account> accounts;
	// Stores login details of all users
	private HashMap<String, String> loginDetails;
	// Stores info on all active sessions
	private HashMap<Long, Date> activeUsers;

	public Bank() throws RemoteException
	{
		super();
		// Set up accounts
		accounts = getAccounts();
		// Set up user login details
		loginDetails = getLoginDetails();
		// Create HashMap for storing info on active sessions
		activeUsers = new HashMap<Long, Date>();
	}

	public static void main(String args[]) throws Exception
	{
		// initilise security manager
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}

		try
		{
			String name = "BankServer";
			BankInterface engine = new Bank();
			// Bind object to registry
			Naming.rebind(name, engine);
			System.out.println("Bank Server Started");
		}
		catch(Exception e)
		{
			System.err.println("Bank Server Exception!");
			e.printStackTrace();
		}
	}


	//Check user entered credentials and if recognised, returns unique session ID
	@Override
	public long login(String username, String password) throws RemoteException, InvalidLogin
	{
		//check if username is valid
		if(loginDetails.containsKey(username))
		{
			System.out.println("True");
			String enteredPassword = loginDetails.get(username);

			// check if password is valid
			if (password.equals(enteredPassword))
			{
				// unique id assigned to user session
				long uniqueID = generateUniqueID();
				return uniqueID;
			}
			// Incorrect password entered
			else
			{
				throw new InvalidLogin();
			}
		}
		// Incorrect credentials entered, return default value
		return 0L;
	}

	// Deposit money in user bank account
	@Override
	public void deposit(int accountNumber, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount
	{
		// check if session is still valid
    boolean sessionExp = sessionExpired(sessionID);
		// if session timed out
    if (sessionExp == true)
		{
    	throw new InvalidSession("Session Timed Out");
    }

		// Check if user entered account exists
		Account acc = findAccount(accounts, accountNumber);
		if (acc != null)
		{
			// deposit ammount
			acc.deposit(amount);
		}
		// if account details are invalid
		else throw new InvalidAccount("Invalid Account");
	}


	// Withdraw money from user bank account
	@Override
	public void withdraw(int accountNumber, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount, InsufficientFunds
	{
		// check if session is still valid
		boolean sessionExp = sessionExpired(sessionID);
		// if session timed out
    if (sessionExp == true)
		{
    	throw new InvalidSession("Session Timed Out");
    }

		// Check if user entered account exists
		Account acc = findAccount(accounts, accountNumber);
		boolean transactionSuccess;
		if (acc != null)
		{
			// withdraw amount and return whether it was successful
			transactionSuccess = acc.withdraw(amount);
			if (transactionSuccess = false)
			{
				throw new InsufficientFunds("Insufficient Funds");
			}
		}
		// if account details are invalid
		else throw new InvalidAccount("Invalid Account");
	}

	// check user bank balance
	@Override
	public int checkBalance(int accountNumber, long sessionID) throws RemoteException, InvalidSession, InvalidAccount
	{
		// check if session is still valid
		boolean sessionExp = sessionExpired(sessionID);
		// if session timed out
    if (sessionExp == true)
		{
    	throw new InvalidSession("Session Timed Out");
    }

		// Check if user entered account exists
		Account acc = findAccount(accounts, accountNumber);
		if (acc != null)
		{
			// check balance
			return acc.checkBalance();
		}
		// if account details are invalid
		else throw new InvalidAccount("Invalid Account");
	}

	// create user statement containing transactions in date range
	@Override
	public Statement getStatement(int accountNumber, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, InvalidAccount
	{
		// check if session is still valid
		boolean sessionExp = sessionExpired(sessionID);
		// if session timed out
    if (sessionExp == true)
		{
    	throw new InvalidSession("Session Timed Out");
    }

		// Check if user entered account exists
		Account acc = findAccount(accounts, accountNumber);
		Statement s = null;
		if (acc != null)
		{
			// create statement
			s = acc.getStatement(from, to);
		}
		// if account details are invalid
		else throw new InvalidAccount("Invalid Account");
		return s;
	}

	// create sample accounts to test
	private List<Account> getAccounts()
	{
		Account acc1 = new Account(123456789, 600);
		Account acc2 = new Account(987654321, 1080);

		List<Account> accounts = new ArrayList<Account>();
		accounts.add(acc1);
		accounts.add(acc2);

		return accounts;
	}

	// check account number is valid
	private Account findAccount(List<Account> accounts, int accountNumber)
	{
		for (Account acc : accounts)
		{
			if (acc.getAccNum() == accountNumber)
			{
				return acc;
			}
		}
		return null;
	}

	// create sample logins to test
	// Stored in HashMap in format 'Username, Password'
	private HashMap<String, String> getLoginDetails()
	{
		HashMap<String, String> loginDetails = new HashMap<String, String>();
		loginDetails.put("123456789", "1234");
		loginDetails.put("987654321", "4321");
		return loginDetails;
	}

	// Returns a unique ID which is assigned to a user's session and times out after defined time period
	private long generateUniqueID()
	{
		Random randomGenerator = new Random();
		boolean notUnique = true;
		long unique = 0;

		// repeat until a unique key is created
		while(notUnique)
		{
			unique = randomGenerator.nextLong();
			// want id to be positive
			if (unique < 0)
			{
				unique *= -1;
			}

			//Check if sessionID already exists
			if (!activeUsers.containsKey(unique))
			{
				// record sessionID and creation time
				activeUsers.put(unique, new Date());
				// unique id found
				notUnique = false;
			}
		}
		System.out.println(unique);
		return unique;
	}

	// check if unique session ID is still within active time period
	private boolean sessionExpired(long sessionID)
	{
		// id generation time
		Date startUp = activeUsers.get(sessionID);
		// current time
		Date now = new Date();

		// check if difference is greater than 5 minutes
		if (now.getTime() - startUp.getTime() >= 5*60*1000)
		{
			// if expired remove from list of valid ids
			activeUsers.remove(sessionID);
			return true;
		}
		// else do nothing an return false
		return false;
	}

	@Override
	public void logout(long sessionID) throws RemoteException
	{
		activeUsers.remove(sessionID);
	}
}
