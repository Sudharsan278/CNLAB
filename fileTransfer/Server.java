package fileTransfer;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    private static final int PORT = 12345; // Server port

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for client connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for client connection
                System.out.println("Client connected.");

                // Handle client request in a separate thread for concurrent clients
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Read the file request from the client
            String fileName = in.readLine();
            File file = new File("test_files/" + fileName);

            if (file.exists() && !file.isDirectory()) {
                // Send file properties
                out.println("File found!");
                out.println("File Name: " + file.getName());
                out.println("File Size: " + file.length() + " bytes");
                out.println("File Format: " + getFileExtension(file));
                out.println("File accessing time: " + getFileAccessingTime(file));
                out.println("File content:");

                // Send file contents
                try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        out.println(line);
                    }
                }
            } else {
                out.println("File not found!");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Helper method to get file extension
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex > 0) {
            return name.substring(lastIndex + 1);
        }
        return "unknown";
    }

    // Helper method to get formatted file accessing time
    private String getFileAccessingTime(File file) {
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(lastModified));
    }
}