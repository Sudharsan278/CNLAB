package fileTransfer;

import java.io.*;
import java.net.*;

public class FileClientUDP {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

            // Request file from user
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the file name to download: ");
            String fileName = userInput.readLine();

            // Send file name to server
            byte[] fileNameBytes = fileName.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(fileNameBytes, fileNameBytes.length, serverAddress, SERVER_PORT);
            socket.send(requestPacket);

            // Receive server response
            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            String status = new String(responsePacket.getData(), 0, responsePacket.getLength());
            if (status.equals("FOUND")) {
                System.out.println("File found on the server. Downloading...");

                // Create directory for saving the file
                File file = new File("client_files/" + fileName);
                file.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    while (true) {
                        DatagramPacket filePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                        socket.receive(filePacket);

                        String endMessage = new String(filePacket.getData(), 0, filePacket.getLength());
                        if (endMessage.equals("END")) {
                            break;
                        }

                        fos.write(filePacket.getData(), 0, filePacket.getLength());
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
