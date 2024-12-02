package EchoTCP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 12345; // Define the port number on which the server will listen.

        try (ServerSocket serverSocket = new ServerSocket(port)) { // Create a server socket to listen on the defined port.
            System.out.println("Server is listening to the port :- " + port);

            while (true) { // Run indefinitely to accept and handle client connections.
                Socket socket = serverSocket.accept(); // Accept a client connection. (Connection-oriented)
                System.out.println("Client Connected!");

                // Setting up input and output streams for communication with the client.
                InputStream input = socket.getInputStream(); 
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);  // Here true is specified to enable auto-flush mode so that the buffer is cleared automatically every time

                String receivedMessage; // Variable to store the received message.

                // Continuously read messages sent by the client.
                while ((receivedMessage = reader.readLine()) != null) { 
                    System.out.println("Received Message :- " + receivedMessage);
                    writer.println("Echo :- " + receivedMessage); // Send an echo response back to the client.
                }

                System.out.println("Client Disconnected!"); // This prints when the client disconnects.
            }

        } catch (Exception ex) {
            System.out.println("Server error :- " + ex.getMessage()); // Handle exceptions that may occur during server operation.
        }
    }
}
