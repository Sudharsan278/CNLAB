package MCQ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MCQClient {

    public static void main(String[] args) {
        String serverAddress = "localhost";  // Server address
        int serverPort = 12345;  // Server port number

        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server...");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                // Display the question
                if (serverMessage.startsWith("Question")) {
                    System.out.println(serverMessage); // Print the question
                    
                    // Read and print all the options (4 lines expected)
                    for (int i = 0; i < 4; i++) {
                        System.out.println(in.readLine());
                    }

                    // Get the user's answer
                    System.out.print("Enter your answer (1-4): ");
                    String answer = userInput.readLine();
                    out.println(answer);  // Send the answer to the server
                } else {
                    // Display other server messages, e.g., final score
                    System.out.println(serverMessage);
                }
            }

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }
}
