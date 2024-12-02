package EchoTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	 public static void main(String[] args) {
	       
		 
		 String hostName = "localhost";
		 int port = 12345;
		 
		 try(Socket socket = new Socket(hostName, port)){
			 
			 OutputStream output = socket.getOutputStream();
			 PrintWriter writer = new PrintWriter(output, true);
			 
			 InputStream input = socket.getInputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			 
			 
			 BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
			 String userInput;
			 
             System.out.println("Type your message (type 'exit' to quit):");

			 
			 while((userInput = consoleReader.readLine())!=null) {
				 if("exit".equalsIgnoreCase(userInput)) {
					 break;
				 }
				 
				 writer.println(userInput);
				 String serverResponse = reader.readLine();
				 System.out.println("Server response :- "+ serverResponse);
			 }
			
		 }catch(UnknownHostException ex) {
			 System.out.println("Error in Client :- "+ ex.getMessage());
		 }catch(IOException ex) {
			 System.out.println("Error in Client :- "+ ex.getMessage());

		 }
	    }
}
