package chatAppUDPSingleFile;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServer {

    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, String> clients = new ConcurrentHashMap<>();
    private static final String LOG_FILE = "chat_log.txt";

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running on port: " + PORT);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet); // Receive a message from a client

                String message = new String(packet.getData(), 0, packet.getLength());
                String clientKey = packet.getAddress() + ":" + packet.getPort();

                // Check if the client is new
                if (!clients.containsKey(clientKey)) {
                    clients.put(clientKey, message); // First message is the client name
                    System.out.println("New client joined: " + message + " (" + clientKey + ")");
                    logMessageToFile(message + " has joined the chat!");
                    broadcast(message + " has joined the chat!", serverSocket, clientKey);
                    continue;
                }

                // Client exits
                if (message.equalsIgnoreCase("exit")) {
                    String clientName = clients.remove(clientKey);
                    System.out.println(clientName + " has left the chat!");
                    logMessageToFile(clientName + " has left the chat!");
                    broadcast(clientName + " has left the chat!", serverSocket, clientKey);
                    continue;
                }

                // Broadcast message
                String clientName = clients.get(clientKey);
                String formattedMessage = clientName + ": " + message;
                System.out.println(formattedMessage);
                logMessageToFile(formattedMessage);
                broadcast(formattedMessage, serverSocket, clientKey);
            }

        } catch (Exception ex) {
            System.out.println("Error in UDP Server: " + ex.getMessage());
        }
    }

    private static void broadcast(String message, DatagramSocket serverSocket, String excludeClient) {
        byte[] buffer = message.getBytes();

        clients.forEach((key, clientName) -> {
            if (!key.equals(excludeClient)) { // Don't send the message back to the sender
                try {
                    String[] clientDetails = key.split(":");
                    String address = clientDetails[0].replace("/", ""); // Remove leading slash if present
                    int port = Integer.parseInt(clientDetails[1]);

                    DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            java.net.InetAddress.getByName(address),
                            port
                    );
                    serverSocket.send(packet); // Send the message
                } catch (Exception ex) {
                    System.out.println("Error broadcasting message to " + key + ": " + ex.getMessage());
                }
            }
        });
    }

    // Log all messages to a single central file
    private static void logMessageToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }
}
