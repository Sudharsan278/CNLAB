package chatAppTCPMultipleFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    // Server's hostname and port to connect
    private static final String hostName = "localhost"; // Server is running on the same machine
    private static final int port = 12345; // Port on which the server is listening

    public static void main(String[] args) {
        try (
            // Create a socket to connect to the server
            Socket socket = new Socket(hostName, port);

            // Output stream to send messages to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Input stream to read user input from the console
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to the Server!");

            // Start a new thread to handle reading messages from the server
            new Thread(new ReadThread(socket)).start();

            // Main thread handles sending messages
            String clientMessage;
            while ((clientMessage = consoleReader.readLine()) != null) {
                out.println(clientMessage); // Send the message to the server
                if (clientMessage.equalsIgnoreCase("exit")) {
                    System.out.println("You have left the chat.");
                    break; // Exit the loop when user types "exit"
                }
            }

        } catch (IOException ex) {
            System.out.println("Error in Client: " + ex.getMessage());
        }
    }
}

// Thread to handle reading messages from the server
class ReadThread implements Runnable {

    private BufferedReader in; // Input stream to receive messages from the server

    // Constructor to initialize the input stream
    public ReadThread(Socket socket) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            System.out.println("Error initializing ReadThread: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        String serverMessage;

        try {
            // Continuously read and print messages from the server
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException ex) {
            System.out.println("Connection to the server was closed: " + ex.getMessage());
        }
    }
}















//class WriteThread{
//	
//	private PrintWriter out;
//	private BufferedReader consoleReader;
//	
//	public WriteThread(Socket socket) {
//		
//		try {
//			out = new PrintWriter(socket.getOutputStream(), true);
//			consoleReader = new BufferedReader(new InputStreamReader(System.in));
//		}catch(IOException ex) {
//			System.out.println("Error in getting the output stream : " + ex.getMessage());
//		}	
//	}
//	
//	public void run() {
//		
//		
//		String clientMessage;
//		
//		try {
//			while((clientMessage = consoleReader.readLine())!= null) {
//				
//				if(clientMessage.equalsIgnoreCase("exit")) {
//					System.out.println("You have left the chat!");
//					break;
//				}
//				
//				out.println(clientMessage);
//			}
//		}catch(IOException ex) {
//            System.out.println("Error reading input: " + ex.getMessage());
//		}
//	}
//}
