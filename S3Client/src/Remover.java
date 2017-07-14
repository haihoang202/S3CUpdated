import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/*
 * Open connection to server via remove port and send filename user wants to remove
 */
public class Remover {
	private Socket sock;
	
	public void remove(String fileToRemove) {
		// TODO Auto-generated method stub
		boolean success = false;
		String result = "fail";
		try{
			sock = new Socket(Config.serverIP, Config.removePort);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(fileToRemove);
			
			System.out.println("Removing file " + fileToRemove);
			
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			result = (String) ois.readObject();
			System.out.println("Result is " + result);
		
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
