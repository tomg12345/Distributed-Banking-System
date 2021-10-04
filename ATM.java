import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// ATM client class

public class ATM
{
	BankInterface bank;
	Scanner scan = new Scanner(System.in);

	// unique session ID
	static Long sessionID = 0L;

	// Constructor that starts the sign-in process
	public ATM(BankInterface bank)
	{
		this.bank = bank;
		this.signin();
	}

	// Enables user to sign-in to the server
	private void signin()
	{

		// ask user to login or quit
		System.out.println("Please enter command 'login' to begin or 'quit' to exit: ");
		// read command
		String s = scan.next();

		// Keep asking until valid response
		while (!s.equals("login") && !s.equals("quit"))
		{
			System.out.println("Invalid Command!  Please enter command 'login' to begin or 'quit' to exit: ");
			// read command
			s = scan.next();
		}

		// if quit is entered, end client session
		if(s.equals("quit"))
		{
			System.out.println("Goodbye!");
			System.exit(0);
		}

		// assign a new sessionID in case user timed out already before
		sessionID = 0L;

		// while sessionID is valid
		while (sessionID.equals(0L))
		{
			// get username
			System.out.println("Please enter username : ");
			String username = scan.next();

			// get password
			System.out.println("Please enter password : ");
			String password = scan.next();

			// validate login through server
			try
			{
				sessionID = bank.login(username, password);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
			catch (InvalidLogin e)
			{
				System.out.println("Invalid credentials entered. Please try again");
			}
		}
		System.out.println(sessionID);
		System.out.println("Login Successful - This session is valid for 5 minutes");

		// login successful, initilise menu options
		mainMenu(sessionID);
	}

	// giver user option to choose available actions
	private void mainMenu(long sessionID)
	{
		System.out.println("Please choose from the following options: \n"
				+ "1 - Deposit\n"
				+ "2 - Withdraw\n"
				+ "3 - Balance Inquiry\n"
				+ "4 - Statement\n"
				+ "5 - Exit System" );
		// read in answer
		int result = scan.nextInt();
		// process answer and call appropriate method
		switch (result){
			case 1: deposit(sessionID);
					break;
			case 2: withdraw(sessionID);
					break;
			case 3: checkBalance(sessionID);
					break;
			case 4: getStatement(sessionID);
					break;
			case 5: logout(sessionID);
					break;
			// if choice is invalid, return to main menu
			default:System.out.println("Invalid selection!");
					mainMenu(sessionID);
					break;
		}
	}

	// main method that sets up and run client
	public static void main (String args[]) throws Exception
	{
		// initilise security manager
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		try
		{
			String name = "BankServer";
			// lookup object that is binded to registry
			BankInterface bank = (BankInterface) Naming.lookup(name);

			//Starts the atm client
			new ATM(bank);
			System.out.println("Connected");
		}
		catch(Exception e)
		{
			System.err.println("ATM Error!");
			e.printStackTrace();
		}
	}

	// Deposit money in user bank account
	private void deposit(long sessionID)
	{
		System.out.println("Please enter your account number: ");
		int account = scan.nextInt();
		System.out.println("Please enter the amount you wish to deposit: ");
		int accountBalance = scan.nextInt();

		// call servers deposit method
		try
		{
			bank.deposit(account, accountBalance, sessionID);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		// check accoount is valid
		catch (InvalidAccount e)
		{
			System.out.println("Invalid Accoung Number! Try again");
			// calls itself to redo process
			deposit(sessionID);
		}
		// check if session timed out
		catch (InvalidSession e)
		{
			System.out.println("This session has expired - please log in again.");
			// let user sign in again if it has
			signin();
		}
		// deposit successful
		System.out.println("Successfully deposited $" + accountBalance + " to account " + account);
		// return to main menu
		mainMenu(sessionID);
	}

	// Withdraw money from user bank account
	private void withdraw(long sessionID)
	{
		System.out.println("Please enter your account number: ");
		int account = scan.nextInt();
		System.out.println("Please enter the amount you wish to withdraw: ");
		int accountBalance = scan.nextInt();

		// call servers deposit method
		try
		{
			bank.withdraw(account, accountBalance, sessionID);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		// check account is valid
		catch (InvalidAccount e)
		{
			System.out.println("Invalid Accoung Number! Try again");
			// calls itself to redo process
			withdraw(sessionID);
		}
		// check if sufficient funds are available
		catch (InsufficientFunds e)
		{
			System.out.println("There are insufficient funds in the account to complete this transaction");
			System.out.println("Try again");
			// calls itself to redo process
			withdraw(sessionID);
		}
		// check if session timed out
		catch (InvalidSession e)
		{
			System.out.println("This session has expired - please log in again.");
			// let user sign in again if it has
			signin();
		}
		// withdrawal successful
		System.out.println("Successfully withdrew $" +accountBalance+ " from account " + account);
		// return to main menu
		mainMenu(sessionID);
	}

	// check account balance through server
	private void checkBalance(long sessionID)
	{
		float balance = 0;
		System.out.println("Please enter your account number: ");
		int account = scan.nextInt();

		// call servers check balance method
		try
		{
			balance = bank.checkBalance(account, sessionID);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		// check account is valid
		catch (InvalidAccount e)
		{
			System.out.println("Invalid Accoung Number! Try again");
			// calls itself to redo process
			checkBalance(sessionID);
		}
		// check if session timed out
		catch (InvalidSession e)
		{
			System.out.println("This session has expired - please log in again.");
			// let user sign in again if it has
			signin();
		}
		// balance check successful
		System.out.println("The current balance of account " + account + " is $" + balance);
		// return to main menu
		mainMenu(sessionID);
	}

	// create a statement of user bank account for defined time period
	private void getStatement(long sessionID)
	{
		// enter required information
		Statement statement = null;
		System.out.println("Please enter your account number: ");
		int account = scan.nextInt();
		System.out.println("Please enter the start date (dd/mm/yyyy) for the statement: ");
		String start = scan.next();
		System.out.println("Please enter the end date (dd/mm/yyyy) for the statement: ");
		String end = scan.next();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date from = null, to = null;

		try
		{
			// apply formatting to dates
			from = (Date)formatter.parse(start);
			to = (Date)formatter.parse(end);

			// call server method for generating bank statement
			statement = bank.getStatement(account, from, to, sessionID);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		// check account is valid
		catch (InvalidAccount e)
		{
			System.out.println("Invalid Accoung Number! Try again");
			// calls itself to redo process
			getStatement(sessionID);
		}
		// check if session timed out
		catch (InvalidSession e)
		{
			System.out.println("This session has expired - please log in again.");
			// let user sign in again if it has
			signin();
		}
		// check if dates entered are valid
		catch(ParseException ex)
		{
			System.out.println("Invalid Dates - Please start again");
			// calls itself to redo process
			getStatement(sessionID);
		}

		// list containing valid transactions from statement
		List<Transaction> trans = statement.getTransations();
		System.out.println("Date\t\t\t\t\t\t Transaction Type\tAmount\t\tBalance");

		// loop over transaction list and print details to console
		for(Transaction t: trans)
		{
			System.out.println(t.getDate().toString()
			 + "\t\t\t " + t.getType()
			 + "\t\t" + t.getAmount()
			 + "\t\t" + t.getBalance());
		}
		// statement creation successful
		System.out.println();
		// return to main menu
		mainMenu(sessionID);
	}

	// log user out of server
	private void logout(long sessionID)
	{
		try
		{
			bank.logout(sessionID);
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
		}
		System.out.println("Goodbye!");
		System.exit(0);
	}
}
