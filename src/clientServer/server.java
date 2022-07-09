package clientServer;

import java.net.*;
import java.util.*;
import java.io.*;

public class server implements Runnable
{
	static File fileLocation = new File(".//HTML files");
	static String WebPage = "index.html";
	static int PORT = 8009;
	private Socket sk;
	public server(Socket s) {
		this.sk = s;
	}
	public static void main(String args[]) throws Exception {
		try {
			ServerSocket ss = new ServerSocket(PORT);
			System.out.println("Welcome to Server Side\n");
			while(true)	{
				server myServer = new server(ss.accept());
				System.out.println("Server Started on Connection");
				Thread th = new Thread(myServer);
				th.start();
			}
		}
		catch(IOException e)	{
			System.err.println("Server connection error: " + e.getMessage());
		}
	}

	public void run() {
		BufferedReader br = null;
		PrintWriter out = null;
		BufferedOutputStream bos = null;
		try {
			br = new BufferedReader(new InputStreamReader(sk.getInputStream()));
			out = new PrintWriter(sk.getOutputStream());
			bos = new BufferedOutputStream(sk.getOutputStream());

		File file = new File(fileLocation, WebPage);
		int fileLength = (int) file.length();
		String content = "text/html";

		FileInputStream filein = new FileInputStream(file);
		byte[] fileData = new byte[fileLength];
		filein.read(fileData);
		filein.close();

		sendFile(out, bos, content, fileLength, fileData);
		}
		catch(FileNotFoundException fnfe)	{
			System.out.println("Error: " + fnfe);
		}
		catch(IOException e)	{
			System.out.println("IO Error: " + e);
		}
	}

	private void sendFile(PrintWriter out, BufferedOutputStream bos, String content, int fileLength, byte[] fileData) throws IOException
	{
		out.println("HTTP/1.1 200 OK");
		out.println("Server: Java HTTP Server from Manan: 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();

		bos.write(fileData, 0, fileLength);
		bos.flush();
	}
}