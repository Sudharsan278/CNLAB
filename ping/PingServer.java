package ping;

import java.io.*;
import java.net.*;

public class PingServer {
    public static void main(String[] args) {
        int port = 12345; // Server port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ping Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handling the client in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String input;
            while ((input = in.readLine()) != null) {
                System.out.println("Received input: " + input);

                // Resolve and ping
                String response = resolveAndPingWithSpeed(input);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private static String resolveAndPingWithSpeed(String input) {
        try {
            InetAddress address = InetAddress.getByName(input);
            System.out.println("Resolved " + input + " to " + address.getHostAddress());

            if (address.isReachable(5000)) {
                return measureNetworkSpeed(address.getHostAddress());
            } else {
                return "Host " + input + " (" + address.getHostAddress() + ") is not reachable";
            }
        } catch (UnknownHostException e) {
            return "Error: Unable to resolve " + input;
        } catch (IOException e) {
            return "Error: Unable to ping " + input + " - " + e.getMessage();
        }
    }

    private static String measureNetworkSpeed(String ipAddress) {
        try {
            long startTime = System.nanoTime();

            InetAddress address = InetAddress.getByName(ipAddress);
            boolean reachable = address.isReachable(2000); 

            long endTime = System.nanoTime();

            if (reachable) {
                long roundTripTime = (endTime - startTime) / 1_000_000;
                return "Host " + ipAddress + " is reachable. Round-trip time: " + roundTripTime + " ms.";
            } else {
                return "Host " + ipAddress + " is not reachable.";
            }
        } catch (Exception e) {
            return "Error measuring network speed: " + e.getMessage();
        }
    }
}
