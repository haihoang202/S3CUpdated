import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RetrieveUploadedFiles {
	
	IndexFile index;
	ServerSocket servsoc = null;
	Socket sock = null;
	DataInputStream dis = null;
	int numFiles = 0;
	ArrayList<String> fileList;
	
	public RetrieveUploadedFiles(IndexFile index) {
		// TODO Auto-generated constructor stub
		this.index = index;
	}

	public void retrieve() {
		// TODO Auto-generated method stub
		
		openConnection();
	
		getAndStoreFile();
		
		postProcess();
		
		closeConnection();
		
	}

	private void openConnection() {
		// TODO Auto-generated method stub
		try {
			servsoc = new ServerSocket(Config.uploadPort);
			System.out.println("Now listening on port " + Config.uploadPort);
			sock = servsoc.accept();
			System.out.println("Accepted connection to: " + sock);
			
			dis = new DataInputStream(sock.getInputStream());
			numFiles = dis.readInt();
			
			System.out.println("Retrieving " + numFiles + "files!");
		} catch (Exception e){
			System.err.println(this.getClass().getName() + ": Error reading number of files. Quit now!");
		}
	}
	
	private void getAndStoreFile() {
		// TODO Auto-generated method stub
		int fileSize;
		String fileName = "prebirthfile";
		int bytesRead;
		int current = 0;
		BufferedOutputStream bos = null;
		byte[] fileBytes;
		
		for(int i = 0 ; i< numFiles; i++){
			try{
				fileName = dis.readUTF();
				fileSize = dis.readInt();
				fileBytes = new byte[fileSize];
				
				System.out.println("Reading file " + fileName + " from client!");
				
				File storeFile = new File(Config.watchLocation + File.separator + fileName);
				if(storeFile.exists())
					storeFile.delete();
				storeFile.createNewFile();

				bos = new BufferedOutputStream(new FileOutputStream(storeFile));
				
				bytesRead = dis.read(fileBytes, 0, fileSize);
				current = bytesRead;
				
				if(bytesRead != fileSize){
					int numTry = 0;
					do {
						bytesRead = dis.read(fileBytes, current, fileSize - current);
						if (bytesRead >= 0) current += bytesRead;
						numTry++;
					} while(bytesRead > -1 & numTry < 10);
				}
				
				bos.write(fileBytes);
				bos.flush();
				
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println(this.getClass().getName() + ": 123 " + fileName + " " + e.getMessage());
			}
		}
		try {
			bos.close();
			dis.close();
//			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void postProcess() {
		// TODO Auto-generated method stub
		getFileList();
		
		for(String file: fileList){
			if (file.endsWith(".txt")){
				processTextFile(file);
			} else if (file.endsWith(".key")){
//				processKeyFile(file);
			}
		}
		
		try{
			index.writeIndexTableToIndexFile();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try{
			index.writeDocumentSizesToDocSizes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void processKeyFile(String file) {
		// TODO Auto-generated method stub
		File keyFile = new File(file);
		FileInputStream fis = null;
		Path storePath = Paths.get(Config.storeLocation + File.separator + keyFile.getName()); 
		try {
			fis = new FileInputStream(keyFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while((line = br.readLine()) != null){
				index.addToPostingList(line, keyFile.getName().replace(".key", ".txt"));
			}
			br.close();
			Files.copy(keyFile.toPath(), storePath, REPLACE_EXISTING);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error key");
			e.printStackTrace();
		}
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyFile.delete();
	}

	private void processTextFile(String file) {
		// TODO Auto-generated method stub
		File textFile = new File(file);
		Path storePath = Paths.get(Config.storeLocation + File.separator + textFile.getName()); 

		if(textFile.exists())
			index.getDocumentSizes().put(textFile.getName(), textFile.length());
		try {
			Files.copy(textFile.toPath(), storePath, REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textFile.delete();
	}

	private ArrayList<String> getFileList() {
		// TODO Auto-generated method stub
		File dir = new File(Config.watchLocation);
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

	private void closeConnection() {
		// TODO Auto-generated method stub
		try {
			dis.close();
			servsoc.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


















