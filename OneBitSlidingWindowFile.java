package labexpts;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class OneBitSlidingWindowFile {

    private static boolean ackReceived; // To check if acknowledgment was received

    // Method to simulate the Stop-and-Wait ARQ protocol for sending file chunks
    public static void sendFileChunks(byte[][] chunks, int timeout, double lossRate, String outputFile) {
        Random rand = new Random(); // To simulate random frame loss
        int chunkCount = chunks.length; // Total number of chunks
        int chunkSent = 0; // Track the current chunk being sent

        // Create an output stream to write received data into a separate file
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            while (chunkSent < chunkCount) {
                System.out.println("Sending Chunk: " + (chunkSent + 1));
                ackReceived = false; // Reset acknowledgment flag

                // Create a final variable for the current chunk
                final int currentChunk = chunkSent;

                // Start the timer in a separate thread
                new Thread(() -> {
                    try {
                        Thread.sleep(timeout);
                        if (!ackReceived) {
                            System.out.println("Timeout occurred for Chunk " + (currentChunk + 1) + ". Resending...\n");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

                // Simulate acknowledgment or loss based on the loss rate
                double randValue = rand.nextDouble(); // Random value between 0.0 and 1.0
                if (randValue > lossRate) {
                    // Acknowledgment received
                    ackReceived = true;
                    System.out.println("Chunk " + (currentChunk + 1) + " acknowledged.\n");
                    fileOutputStream.write(chunks[currentChunk]); // Write chunk data to the file
                    chunkSent++; // Move to the next chunk
                } else {
                    // Simulate chunk loss
                    System.out.println("Chunk " + (currentChunk + 1) + " lost. Waiting for timeout...\n");
                }

                // Wait for acknowledgment reception or timeout before continuing
                try {
                    Thread.sleep(500); // Simulate time delay between attempts
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("All chunks sent successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to output file: " + e.getMessage());
        }
    }

    // Method to read a file and split it into chunks
    public static byte[][] readFileInChunks(String fileName, int chunkSize) throws IOException {
        File file = new File(fileName);
        byte[] fileBytes = new byte[(int) file.length()];
        
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(fileBytes);
        }

        // Calculate the number of chunks
        int chunkCount = (int) Math.ceil((double) fileBytes.length / chunkSize);
        byte[][] chunks = new byte[chunkCount][];

        for (int i = 0; i < chunkCount; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, fileBytes.length);
            chunks[i] = new byte[end - start];
            System.arraycopy(fileBytes, start, chunks[i], 0, end - start);
        }

        return chunks;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Taking input for the file name to send
        System.out.print("Enter the file name to send: ");
        String fileName = sc.nextLine();

        // Taking input for timeout duration
        System.out.print("Enter timeout duration (in milliseconds): ");
        int timeout = sc.nextInt();

        // Taking input for the chunk size
        System.out.print("Enter chunk size (in bytes): ");
        int chunkSize = sc.nextInt();

        // Taking input for the output file name to store received chunks
        System.out.print("Enter the output file name to store received chunks: ");
        sc.nextLine();  // Consume the newline character
        String outputFile = sc.nextLine();

        // Set a default loss rate (e.g., 10%)
        double lossRate = 0.1;

        try {
            // Read the file and split it into chunks
            byte[][] chunks = readFileInChunks(fileName, chunkSize);

            // Start sending chunks using Stop-and-Wait ARQ
            sendFileChunks(chunks, timeout, lossRate, outputFile);

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        // Close the scanner
        sc.close();
    }
}
