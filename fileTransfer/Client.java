package fileTransfer;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Server IP
    private static final int SERVER_PORT = 12345;             // Server port

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Request file from server
            System.out.print("Enter the file name to request: ");
            String fileName = userInput.readLine();
            out.println(fileName); // Send file name to server

            // Read and display server's response (file properties and contents)
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}