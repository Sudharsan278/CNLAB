package fileTransfer;

import java.io.*;
import java.net.*;

public class FileClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Request file from user
            System.out.print("Enter the file name to download: ");
            String fileName = userInput.readLine();
            dos.writeUTF(fileName); // Send file name to server

            // Check server response
            String status = dis.readUTF();
            if (status.equals("FOUND")) {
                // Receive file size
                long fileSize = dis.readLong();
                System.out.println("Downloading file: " + fileName + " (" + fileSize + " bytes)");

                // Receive file contents
                File file = new File("client_files/" + fileName);
                file.getParentFile().mkdirs(); // Ensure the directory exists

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long remaining = fileSize;

                    while ((bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                        fos.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }
                System.out.println("File downloaded successfully: " + file.getAbsolutePath());
            } else {
                System.out.println("File not found on the server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
