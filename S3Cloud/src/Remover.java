import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/*
 * Remove instance of a document from the system
 * Steps:
 * - Delete key file and encrypted text file from the storage collection
 * - Delete instance from the index table
 * - Write back the index table to the index file
 * - Delete instance from the document size 
 * - Write back the document size to document size file
 */
public class Remover {

	ServerSocket servsoc;
	Socket sock;
	IndexFile indexFile;
	boolean affected;
	StringBuilder sb;
	
	//Constructor
	public Remover(IndexFile index) {
		// TODO Auto-generated constructor stub
		servsoc = null;
		sock = null;
		this.indexFile = index;
		affected = false;
		sb = new StringBuilder();
	}

	/*
	 * Remove operation 
	 */
	public void waitToRemove() {
		// TODO Auto-generated method stub
		try{
			/*
			 * Open connection to get file to remove
			 */
			servsoc = new ServerSocket(Config.removePort);
			System.out.println("Now listening to remove request on port " + Config.removePort);
			sock = servsoc.accept();
			
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			String fileToRemove = (String) ois.readObject();
			
			//Remove instance from IndexTable
			for (String topic : indexFile.getIndexTable().keySet()) {
				if(indexFile.getIndexTable().get(topic).contains(fileToRemove)){
					System.out.println("Found an keyword of " + fileToRemove + " in the IndexFile"
							+ "\nRemoving...");
					affected = true;
					indexFile.getIndexTable().get(topic).remove(fileToRemove);
				}
			}
			
			//Write back to the index file
			if (affected) {
				sb.append("1");
				try {
					indexFile.writeIndexTableToIndexFile();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			
			affected = false;
			
			//Remove instance from DocSize
			if(indexFile.getDocumentSizes().remove(fileToRemove) != null) {
				System.out.println("Found size instance of " + fileToRemove + " in the DocSizes"
						+ "\nRemoving...");
				affected = true;
			}
			
			//Write back to the document size file
			if(affected) {
				sb.append("2");
				try{
					indexFile.writeDocumentSizesToDocSizes();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			
			//Send back the result to the client
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			
			if (sb.toString() == "12") 
				oos.writeObject("Succesfully removed the file " + fileToRemove);
			else 
				oos.writeObject("Unsuccessfully removed the file " + fileToRemove);
				
			//Close connection, openning stream
			oos.flush();
			oos.close();
			sock.close();
			servsoc.close();
			
		} catch (Exception e){
			System.err.println("Error opening removing port");
			e.printStackTrace();
		}		
		
	}

}
