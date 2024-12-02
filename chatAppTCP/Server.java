package chatAppTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {

    // Thread-safe set to store active client handlers
    private static final Set<ClientHandler> clientHandlers = 
        Collections.synchronizedSet(new HashSet<>());
    
    // Port number for the server to listen on
    private static final int PORT = 12345;
    
    // Flag to determine if the server is running
    private static boolean isRunning = true;

    public static void main(String[] args) {
        System.out.println("Server started and listening to the port: " + PORT);

        // Thread to listen for server commands like "Shutdown"
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String command;
                while ((command = reader.readLine()) != null) {
                    // If the command is "Shutdown", stop the server
                    if (command.equalsIgnoreCase("Shutdown")) {
                        shutDownServer();
                        break;
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error in command listener: " + ex.getMessage());
            }
        }).start();

        // Main server loop to accept client connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept(); // Accepts a client connection
                System.out.println("New client is trying to connect to the server...");

                // Create a new ClientHandler for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler); // Add the handler to the active set

                // Start a new thread for the client handler
                new Thread(clientHandler).start();
            }
        } catch (IOException ex) {
            if (isRunning) {
                System.out.println("Error in Server: " + ex.getMessage());
            } else {
                System.out.println("Server Shut Down!");
            }
        }
    }

    // Broadcast a message to all clients except the sender
    public static synchronized void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    // Remove a client from the active clientHandlers set
    public static synchronized void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client " + clientHandler.getClientName() + " has disconnected!");
    }

    // Shutdown the server and disconnect all clients
    public static synchronized void shutDownServer() {
        System.out.println("Shutting down the server...");
        isRunning = false;

        // Notify all clients and close their connections
        for (ClientHandler client : clientHandlers) {
            client.sendMessage("Server is shutting down. You have been disconnected!");
            client.closeConnection();
        }

        clientHandlers.clear(); // Clear the clientHandlers set
    }
}

class ClientHandler implements Runnable {
    
    private Socket socket; // The client's socket connection
    private PrintWriter out; // Output stream to the client
    private BufferedReader in; // Input stream from the client
    private String clientName; // Name of the client

    // Constructor to initialize the client socket
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Initialize input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask the client for their name
            out.println("Enter your name: ");
            clientName = in.readLine();

            // Validate client name
            if (clientName == null || clientName.trim().isEmpty()) {
                out.println("Invalid name. Connection lost!");
                closeConnection();
                return;
            }

            // Notify other clients about the new client
            Server.broadcast("Client " + clientName + " has joined the chat", this);
            out.println("Welcome to the chat, " + clientName + ". Type 'exit' to leave the chat.");

            String message;

            // Main loop to read and broadcast messages from the client
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) { // Client wants to leave the chat
                    break;
                }
                Server.broadcast(clientName + ": " + message, this);
            }
        } catch (IOException ex) {
            if (ex.getMessage().equals("Connection reset")) {
                System.out.println("Client " + clientName + " disconnected unexpectedly.");
            } else {
                System.out.println("Error in client handler: " + ex.getMessage());
            }
        } finally {
            closeConnection(); // Ensure the connection is closed properly
        }
    }

    // Send a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Get the name of the client
    public String getClientName() {
        return clientName;
    }

    // Close the client's connection and remove them from the server
    public void closeConnection() {
        try {
            Server.broadcast("Client " + clientName + " has left the chat!", this);
            socket.close(); // Close the socket connection
        } catch (IOException ex) {
            System.out.println("Error in closing the connection: " + ex.getMessage());
        } finally {
            Server.removeClient(this); // Remove the client from the server's active clients
        }
    }
}
