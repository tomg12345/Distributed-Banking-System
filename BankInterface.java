import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;


public interface BankInterface extends Remote
{

  public long login(String username, String password) throws RemoteException, InvalidLogin;

  public void logout(long sessionID)throws RemoteException;

  public void deposit(int accountNumber, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount;

  public void withdraw(int accountNumber, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount, InsufficientFunds;

  public int checkBalance(int accountNumber, long sessionID) throws RemoteException, InvalidSession, InvalidAccount;

  public Statement getStatement(int accountNumber, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, InvalidAccount;

}
