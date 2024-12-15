package DNS;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class DNSServer {
    
    // Simulated DNS database
    private static final HashMap<String, String> dnsDatabase = new HashMap<>();
    
    static {
        dnsDatabase.put("example.com", "93.184.216.34");
        dnsDatabase.put("google.com", "172.217.9.142");
        dnsDatabase.put("yahoo.com", "98.137.11.163");
        dnsDatabase.put("openai.com", "142.250.217.14");
    }

    public static void main(String[] args) {
        int port = 12345;  // Port number for the server to listen on
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("DNS Server started. Waiting for clients to connect...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Read the domain name from the client
                    String domainName = in.readLine();
                    System.out.println("Received query for domain: " + domainName);

                    // Look up the IP address in the simulated DNS database
                    String ipAddress = dnsDatabase.get(domainName);

                    if (ipAddress != null) {
                        out.println("The IP address of " + domainName + " is " + ipAddress);
                    } else {
                        out.println("Domain " + domainName + " not found in DNS records");
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }
}
