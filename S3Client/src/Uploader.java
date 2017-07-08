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

public class Uploader {

	String path;
	ExtractKeyPhrases extractKP;
	CipherFile cipher;
	ArrayList<String> fileList;
	StopwordRemover stop;
	Socket socket;

	public Uploader(String uploadFolder) {
		this.path = uploadFolder;

		extractKP = new ExtractKeyPhrases();

		cipher = new CipherFile();

		fileList = new ArrayList<>();

		stop = new StopwordRemover();

		socket = null;
	}

	public void upload() {
		// TODO Auto-generated method stub
		getFileList();

		extractKey(); // To get keyword extraction from the file

		encryptFiles(); // Encrypt plain text file and key file

		sendFile(); // Send to the cloud

		cleanUp(); // Clean up key file and encrypted file
	}

	private void cleanUp() {
		// TODO Auto-generated method stub
		for (String i : fileList) {
			File file = new File(i);
			if (file.exists())
				if (file.delete())
					System.out.println("Deleted file " + file.getName());
		}
	}

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

	private void encryptFiles() {
		// TODO Auto-generated method stub
		fileList.stream().forEach((String file) -> {
			try {
				if (file.endsWith(".txt")) {
					cipher.encrypt(Config.encryptionKey, file);
				} else if (file.endsWith(".key")) {
					splitKeyword(file);
					cipher.hash(file);
				}
			} catch (Throwable e) {
				System.err.println("Error in encryption");
			}
		});
	}

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
			// String[] temp = new String[lines.size()];
			// lines.toArray(temp);

			ArrayList<String> temp = new ArrayList<>(lines);
			temp = stop.truncate(temp);

			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String i : temp) {
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

	private void extractKey() {
		// TODO Auto-generated method stub

		String[] opts = { "-l", "data/tmp", "-m", "keyphrextr", "-t", "PorterStemmer", "-v", "none", "-n", "10" };

		for (String file : fileList) {
			if (file.endsWith(".key")) {
				File thisfile = new File(file);
				thisfile.delete();
			} else if (file.endsWith(".txt")) {
				File thisfile = new File(file);
				int filesize = (int) Integer.parseInt(opts[opts.length - 1]);
				if (thisfile.exists())
					filesize = ((Double) (thisfile.length() * 0.1)).intValue();
				if (filesize < 1000)
					filesize = 20;
				else
					filesize = (int) (filesize * 0.05);
				opts[1] = this.path;
				opts[opts.length - 1] = String.valueOf(filesize);
				try {
					String [] optclone = opts.clone();
					extractKP.extract(optclone);
				} catch (Exception e) {
					System.err.println("Maui extracting issue!");
				}
			}
		}

		getFileList();

	}

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
