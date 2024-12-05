package fileTransfer;

import java.io.*;
import java.net.*;

public class FileServerUDP {
    private static final int PORT = 12345;
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Server is running and waiting for client requests...");

            while (true) {
                // Receive file name request from client
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                String fileName = new String(requestPacket.getData(), 0, requestPacket.getLength());
                System.out.println("Client requested file: " + fileName);

                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                File file = new File("server_files/" + fileName);
                if (file.exists() && !file.isDirectory()) {
                    // Send "FOUND" status
                    byte[] foundMessage = "FOUND".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(foundMessage, foundMessage.length, clientAddress, clientPort);
                    socket.send(responsePacket);

                    // Send file contents
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] fileBuffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = fis.read(fileBuffer)) > 0) {
                            DatagramPacket filePacket = new DatagramPacket(fileBuffer, bytesRead, clientAddress, clientPort);
                            socket.send(filePacket);
                        }
                    }

                    // Send "END" message
                    byte[] endMessage = "END".getBytes();
                    DatagramPacket endPacket = new DatagramPacket(endMessage, endMessage.length, clientAddress, clientPort);
                    socket.send(endPacket);

                    System.out.println("File sent to client: " + fileName);
                } else {
                    // Send "NOT_FOUND" status
                    byte[] notFoundMessage = "NOT_FOUND".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(notFoundMessage, notFoundMessage.length, clientAddress, clientPort);
                    socket.send(responsePacket);

                    System.out.println("Requested file not found: " + fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

