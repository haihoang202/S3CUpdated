import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Upload class in client side:
 * 4 main steps:
 * - extract keyword from the file
 * - encrypted keyword and plain text data
 * - send encrypted files and hashed keyword to the server
 * - clean up opening stream and buffer, delete file after uploads
 */
public class Uploader {

	String path;
	ExtractKeyPhrases extractKP;
	CipherFile cipher;
	ArrayList<String> fileList;
	StopwordRemover stop;
	Socket socket;
	public static String [] mauiKeyOptions = {
			"-l", "data/tmp/", "-m", "keyphrextr", "-t", "PorterStemmer", "-v", "none"
	    };
	
	public static String[] getMauiExtractionOptions(String path) {
        String [] options = mauiKeyOptions;
        options[1] = path;
        return options;
    }

	//Constructor
	public Uploader(String uploadFolder) {
		this.path = uploadFolder;

		extractKP = new ExtractKeyPhrases();

		cipher = new CipherFile();

		fileList = new ArrayList<>();

		stop = new StopwordRemover();

		socket = null;
	}

	//abstract class to upload documents
	public void upload() {
		// TODO Auto-generated method stub
		getFileList();

		extractKey(); // To get keyword extraction from the file

		encryptFiles(); // Encrypt plain text file and key file

		sendFile(); // Send to the cloud

		cleanUp(); // Clean up key file and encrypted file
	}

	/*
	 * Delete file from the upload folder after uploading them to the cloud
	 */
	private void cleanUp() {
		// TODO Auto-generated method stub
		for (String i : fileList) {
			File file = new File(i);
			if (file.exists())
				if (file.delete())
					System.out.println("Deleted file " + file.getName());
		}
	}

	/*
	 * Open socket to server side and upload ALL files (hashed keyword file and encrypted file)
	 * to the cloud
	 */
	private void sendFile() {
		// TODO Auto-generated method stub
		boolean success = false;
		DataOutputStream dos = null;

		try {
			socket = new Socket(Config.serverIP, Config.uploadPort);
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeInt(fileList.size());
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error sending number of files. Quit now!");
			System.exit(0);
		}

		try {
			socket.setKeepAlive(true);
			socket.setSoTimeout(10000);
		} catch (SocketException e) {
			// TODO: handle exception
			Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, e);
		}

		for (String file : fileList) {
			uploadFileOnNetwork(dos, socket, file);
		}

		try {
			dos.close();
			socket.close();
		} catch (SocketException ex) {
			Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/*
	 * Write file name, file size and byte to write through stream
	 */
	private void uploadFileOnNetwork(DataOutputStream dos, Socket sock, String filename) {
		// TODO Auto-generated method stub
		FileInputStream fis;

		try {
			File file = new File(filename);
			byte[] fileBytes = new byte[(int) file.length()];
			fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(fileBytes, 0, fileBytes.length);

			dos.writeUTF(file.getName());
			dos.writeInt(fileBytes.length);
			dos.flush();

			dos.write(fileBytes, 0, fileBytes.length);
			dos.flush();

			fis.close();
			bis.close();
		} catch (IOException e) {
			System.err.println("Error uploading file!");
		}
	}

	/*
	 * Use cipher to encrypted .txt file 
	 * Hash the keyword for the .key file
	 */
	private void encryptFiles() {
		// TODO Auto-generated method stub
		fileList.stream().forEach((String file) -> {
			try {
				if (file.endsWith(".txt")) {
					System.out.println("Encrypting file " + file);
					cipher.encrypt(Config.encryptionKey, file);
				} else if (file.endsWith(".key")) {
					System.out.println("Hashing file " + file);
					splitKeyword(file);
					cipher.hash(file);
				}
			} catch (Throwable e) {
				System.err.println("Error in encryption");
			}
		});
	}

	/*
	 * Use linked hash set to add unique word to the key file 
	 */
	private void splitKeyword(String file) {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			LinkedHashSet<String> lines = new LinkedHashSet<>();

			while ((line = br.readLine()) != null) {
				lines.add(line.toLowerCase());
				String[] splitline = line.split(" ");
				for (String i : splitline)
					lines.add(i.toLowerCase());
			}

			br.close();

//			ArrayList<String> temp = new ArrayList<>(lines);
//			temp = stop.truncate(temp);

			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String i : lines) {
				bw.write(i);
				bw.newLine();
			}
			bw.close();

		} catch (FileNotFoundException e) {
			System.err.println("Cannot read file " + file);
		} catch (IOException e) {
			// TODO: handle exception
			System.err.println("Cannot write to file " + file);
		}
	}

	/*
	 * Use Maui to extract the .txt file to .key file which contains important keyword 
	 */
	private void extractKey() {
		// TODO Auto-generated method stub
		String[] options = getMauiExtractionOptions(path);
        //Attempt to extract the keywords
        try {
            //Uses Maui to make the .key files
            extractKP.extract(options);
        } catch(Exception e) {
            System.err.println("Problem extracting from Maui");
            e.printStackTrace();
        }

		getFileList();

	}

	/*
	 * Get list of files from the upload folder
	 */
	private ArrayList<String> getFileList() {
		// TODO Auto-generated method stub
		File dir = new File(this.path);
		fileList = new ArrayList<>();

		if (dir.isDirectory()) {
			System.out.println("In directory " + dir.getAbsolutePath());
			String[] directories = dir.list();
			for (String i : directories)
				fileList.add(dir.getPath() + File.separator + i);

			return fileList;
		}
		return null;
	}

}
