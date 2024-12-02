package remoteCommandExecution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        int port = 1234; // Server port

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

                    System.out.println("Client connected.");

                    String command;
                    while ((command = in.readLine()) != null) {
                        System.out.println("Received command: " + command);

                        // Prepare the command array for OS compatibility
                        String[] cmdArray;
                        if (System.getProperty("os.name").startsWith("Windows")) {
                            cmdArray = new String[]{"cmd.exe", "/c", command};
                        } else {
                            cmdArray = new String[]{"/bin/sh", "-c", command};
                        }

                        // Execute the command and get the output
                        Process process = Runtime.getRuntime().exec(cmdArray);
                        try (BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                            String outputLine;
                            while ((outputLine = processOutput.readLine()) != null) {
                                out.println(outputLine);
                            }
                        }

                        // Indicate the end of the command output
                        out.println("END_OF_COMMAND");
                    }
                } catch (Exception e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}