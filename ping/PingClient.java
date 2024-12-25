package ping;


import java.io.*;
import java.net.*;

public class PingClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; 
        int port = 12345; 

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to Ping Server");

            String input;
            while (true) {
                System.out.print("Enter domain or IP to ping (or type 'exit' to quit): ");
                input = userInput.readLine();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Exiting...");
                    break;
                }

                out.println(input); 
                String response = in.readLine();
                System.out.println(response);	
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
