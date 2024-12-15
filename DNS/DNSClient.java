package DNS;

import java.io.*;
import java.net.*;

public class DNSClient {

    public static void main(String[] args) {
        String serverAddress = "localhost";  // Server address (localhost for testing)
        int serverPort = 12345;  // Port number of the server

        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to DNS Server at " + serverAddress + ":" + serverPort);

            while (true) {
                System.out.print("Enter a domain name to resolve (or 'exit' to quit): ");
                String domainName = userInput.readLine().trim();

                if (domainName.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting client.");
                    break;
                }

                // Send the domain name to the server
                out.println(domainName);

                // Read and print the server response (IP address or error message)
                String response = in.readLine();
                System.out.println("Server response: " + response);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }
}
