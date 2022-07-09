package clientServerMessage;

import java.io.*;
import java.net.*;

public class ServerSide {
	public static void main(String[] args) throws Exception{
		try {
			ServerSocket ss = new ServerSocket(8080);
			System.out.println("Server is waiting for the client to connect");

			Socket sk = ss.accept();
	        System.out.println("Connection Established");

	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        PrintWriter pw = new PrintWriter(sk.getOutputStream(), true);

	        /* Server reading the message from Client through its InputStream */

	        InputStreamReader isr = new InputStreamReader(sk.getInputStream());
	        BufferedReader ser_receive = new BufferedReader(isr);
	        String send, receive;
	        while(true) {
	        	if((receive = ser_receive.readLine())!=null)
	        	{
	        		System.out.println("Client : "+ receive);
	        	}
	        	send = br.readLine();
	        	pw.println(send);
	        	if(send.equals("bye"))  {
	        		System.exit(0);
	        	}
	        }
		}
	    catch(Exception e)
	    {}
	}
}