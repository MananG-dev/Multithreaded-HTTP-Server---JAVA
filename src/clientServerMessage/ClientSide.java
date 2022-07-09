package clientServerMessage;
import java.net.*;
import java.io.*;

public class ClientSide {
	private static Socket sk;

	public static void main(String[] args) throws Exception	{
		//try : If there is an exception which is raised inside it, it can be caught and handled easily
		try	{
			sk = new Socket("localhost", 40888);
			System.out.println("==== Welcome to the Client Side ====");
			
			// To read the Message from the Keyboard
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            /* For Sending the Message to Server ...
               PrintWriter is basically the link between Client and Server. */
            PrintWriter pw = new PrintWriter(sk.getOutputStream(), true);

            // Client reading the message from Server through its InputStream
            InputStreamReader isr = new InputStreamReader(sk.getInputStream());
            BufferedReader ser_receive = new BufferedReader(isr);
            String send, receive;
            while(true) {
                // To send the message which is generated from the Keyboard
                send = br.readLine();   // Read line by line and save it in String send
                pw.println(send);
                if((receive = ser_receive.readLine())!=null)  {
                    System.out.println("Server : " + receive);
                }
            }
		}
		catch(Exception e) {
			System.err.println("Connection Error: " + e.getMessage());
		}
	}
}
