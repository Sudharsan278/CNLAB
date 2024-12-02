package remoteCommandExecution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String serverAddress = "localhost"; 
        int port = 1234;
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server. Enter commands to execute:");

            String command;
            while (true) {
                System.out.print("Command: ");
                command = scanner.nextLine();

               
                out.println(command);

                
                String response;
                while (!(response = in.readLine()).equals("END_OF_COMMAND")) {
                    System.out.println(response);
                }
            }
        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}