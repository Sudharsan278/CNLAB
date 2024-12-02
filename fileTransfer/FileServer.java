package fileTransfer;

import java.io.*;
import java.net.*;

public class FileServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(new FileTransferHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class FileTransferHandler implements Runnable {
    private final Socket clientSocket;

    public FileTransferHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            // Read the file name requested by the client
            String fileName = dis.readUTF();
            File file = new File("server_files/" + fileName);

            if (file.exists() && !file.isDirectory()) {
                dos.writeUTF("FOUND"); // Send status to the client

                // Send file size
                dos.writeLong(file.length());

                // Send file contents
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) > 0) {
                        dos.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File " + fileName + " sent to client.");
            } else {
                dos.writeUTF("NOT_FOUND"); // Notify client the file does not exist
                System.out.println("Requested file not found: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

