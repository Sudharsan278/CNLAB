package chatAppUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            // Prompt user for their name
            System.out.print("Enter your name: ");
            String clientName = scanner.nextLine().trim();

            if (clientName.isEmpty()) {
                System.out.println("Name cannot be empty. Exiting...");
                return;
            }

            // Send the name as the first message to the server
            byte[] nameBuffer = clientName.getBytes();
            DatagramPacket namePacket = new DatagramPacket(nameBuffer, nameBuffer.length, serverAddress, SERVER_PORT);
            clientSocket.send(namePacket);

            System.out.println("Welcome to the UDP Chat, " + clientName + "!");
            System.out.println("Type your messages below. Type 'exit' to leave the chat.");

            // Start a thread to listen for incoming messages
            new Thread(() -> listenForMessages(clientSocket)).start();

            // Send messages to the server
            while (true) {
                String message = scanner.nextLine();
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
                clientSocket.send(packet);

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("You have left the chat.");
                    break;
                }
            }

        } catch (Exception ex) {
            System.out.println("Error in UDP Client: " + ex.getMessage());
        }
    }

    private static void listenForMessages(DatagramSocket clientSocket) {
        byte[] buffer = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(packet); // Receive messages from the server
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message); // Print the received message
            }
        } catch (Exception ex) {
            System.out.println("Connection closed: " + ex.getMessage());
        }
    }
}
