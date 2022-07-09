package clientServer;

import java.util.*;
import java.io.*;
import java.net.*;

public class serverFile implements Runnable	{
	//To Locate all the Web Files
	static final File WebFile_location = new File(".//HTML files");
	static final String Default_file = "index.html";
	static final String File_not_found = "404Error.html";
	static final String Method_notSupported = "notSupported.html";
	
	// PORT: to listen the connection between Client and Server
	static final int PORT = 8080;
	
	//Client Connection via Socket class
	private Socket socket;
	//To get the IP address of Client
	static SocketAddress SocketChannel;
	public serverFile(Socket sk)	{
		this.socket = sk;
		serverFile.SocketChannel = sk.getRemoteSocketAddress();
	}
	
	public static void main(String args[]) throws Exception	{
		try	{
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println(" ====== Welcome to Server ======");
			System.out.println("\nServer Started... \nListening to PORT: " + PORT );
			while(true) {
				serverFile myServer = new serverFile(serverSocket.accept());
				System.out.println("Connection to client: " + SocketChannel + " at " + new Date());
				
				// Creating thread for every new user
				Thread thread = new Thread(myServer);
				thread.start();
				//serverSocket.close();
			}
		}
		catch(IOException e)	{
			System.err.println("Server connection error: " + e);
		}
	}
	
	public void run()	{
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream dataout = null;
		String fileRequested = null;
		
		try	{
			// To manage the particular client connection
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// To send the HTTP Header / data to client
			out = new PrintWriter(socket.getOutputStream());
			//To get the requested data from webfile location to client
			dataout = new BufferedOutputStream(socket.getOutputStream());
			
			//To get the first line of the request from client via BufferedReader
			String input = in.readLine();
			// We parse the request with a String Tokenizer
			StringTokenizer st = new StringTokenizer(input);
			
			// To get the HTTP method of the requested file
			String method = st.nextToken().toUpperCase();
			// Now we get the file requested
			fileRequested = st.nextToken().toLowerCase();
			
			// If the request will neither GET nor HEAD
			if(!method.equals("GET") && !method.equals("HEAD"))	{
				notSupported(method, out, dataout);
			}
			else	{
				// GET or HEAD method
				supportedFile(fileRequested, method, out, dataout);
			}
		}
		catch(FileNotFoundException e)	{
			try	{
				fileNotFound(out, dataout, fileRequested);
			}
			catch(IOException ioe)	{
				System.err.println("Error with file not found exception: " + ioe);
			}
		}
		catch(IOException e)	{
			System.err.println("Server error: " + e);
		}
		finally	{
			try	{
				// Closing all the Connections
				in.close();
				out.close();
				dataout.close();
				socket.close();
			}
			catch(Exception e)	{
				System.err.println("Error closing stream: " + e.getMessage());
			}
		}
	}
	
	// Methods for Not supporting GET or HEAD method
	private void notSupported(String method, PrintWriter out, BufferedOutputStream dataout) throws IOException	{
		System.out.println("501 Not Implemented: " + method + " method.");
		
		// To return the Not supported file to client
		File file = new File(WebFile_location, Method_notSupported);
		int fileLength = (int) file.length();
		String contentType = "text/html";
		
		//Reading content from file to return to client
		byte[] fileData = readFileData(file, fileLength);
		
		//Sending HTTP Header with Data to client
		out.println("HTTP/1.1 501 Not Implemented");
		out.println("Server: Java HTTP Server from Manan: 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + contentType);
		out.println("Content-Length: " + fileLength);
		out.println();		//Blank line between Headers and Content, very important!
		out.flush();		//Flush Character Output Stream Buffer
		
		dataout.write(fileData, 0, fileLength);
		dataout.flush();
	}
	
	// Method for Supported file i.e for GET or HEAD methods
	private void supportedFile(String fileRequested,String method, PrintWriter out, BufferedOutputStream dataout) throws IOException	{
		if(fileRequested.endsWith("/"))	{
			//To send Default File to client
			fileRequested += Default_file;
		}
		
		File file = new File(WebFile_location, fileRequested);
		int fileLength = (int) file.length();
		String content = getContentType(fileRequested);
		
		if(method.equals("GET"))	{
			byte[] fileData = readFileData(file, fileLength);
			
			// 200 OK response that the request has been Succeeded
			out.println("HTTP/1.1 200 OK");
			out.println("Server: Java HTTP Server from Manan: 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: " + content);
			out.println("Content-length: " + fileLength);
			out.println();
			out.flush();

			dataout.write(fileData, 0, fileLength);
			dataout.flush();
		}
		System.out.println("File: " + fileRequested + " of type " + content + " returned.");
	}
	
	// Method for Reading the Requested file Content
	private byte[] readFileData(File file, int fileLength) throws IOException	{
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try	{
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		}
		finally	{
			if(fileIn != null)	{
				fileIn.close();
			}
		}
		return fileData;
	}
	
	// Method to return the MIME type
	private String getContentType(String fileRequested)	{
		if(fileRequested.endsWith(".html") || fileRequested.endsWith(".htm"))	{
			return "text/html";
		}
		else
			return "text/plain";	
	}
	
	// Method if the requested file is with valid but not in the Server's Directory 
	private void fileNotFound(PrintWriter out, BufferedOutputStream dataout, String fileRequested) throws IOException	{
		File file = new File(WebFile_location, File_not_found);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server from Manan: 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		
		dataout.write(fileData, 0, fileLength);
		dataout.flush();
	
		System.out.println("File: " + fileRequested + " not found");
	}
}