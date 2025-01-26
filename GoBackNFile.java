package labexpts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class GoBackNFile {

    static int frameCount, windowSize, framesSent = 0;
    static boolean[] received;  // Track received chunks

    // Method to simulate the Go-Back-N ARQ protocol for sending file chunks
    public static void sendFileChunks(byte[][] chunks, int timeout, String outputFile) {
        Random rand = new Random(); // To simulate random chunk loss
        int chunkCount = chunks.length; // Total number of chunks
        int framesReceived = 0; // Counter to keep track of how many frames are successfully received
        received = new boolean[chunkCount]; // Initialize received array

        // Create an output stream to write received data into a separate file
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

            // Loop until all chunks are successfully transmitted
            while (framesReceived < chunkCount) {
                System.out.println("\nSending chunks...");

                // Transmit up to window size chunks at a time
                for (int i = 0; i < windowSize && framesSent < chunkCount; i++) {
                    System.out.println("Sent Chunk: " + (framesSent + 1)); // Sending a chunk
                    framesSent++; // Increment the chunks sent counter
                }

                // Simulating the acknowledgment process
                // Randomly simulate the number of chunks acknowledged (or a loss)
                int ackFrame = rand.nextInt(windowSize + 1); // Random number between 0 and windowSize

                if (ackFrame == windowSize) {
                    // Simulating the case where all chunks in the window are acknowledged
                    System.out.println("All chunks acknowledged for this window.");
                    framesReceived += windowSize; // Move the received counter forward by window size
                } else {
                    // Simulating a scenario where a chunk is lost and Go-Back-N is triggered
                    System.out.println("Chunk " + (framesReceived + ackFrame + 1) + " lost. Go-Back-N triggered.");

                    // Reset the framesSent counter to the last unacknowledged chunk
                    // Resend from the chunk where loss occurred
                    framesSent = framesReceived + ackFrame; // Resending starts from lost chunk
                    framesReceived += ackFrame; // Increment received counter by how many were acknowledged
                }

                // Simulating the successful reception and writing to file
                for (int i = 0; i < framesReceived; i++) {
                    if (!received[i]) {
                        received[i] = true; // Mark chunk as received
                        fileOutputStream.write(chunks[i]); // Write chunk data to the file
                    }
                }

                // Simulate some delay to represent processing time
                Thread.sleep(1000);

                // Display the status of received chunks
                printReceivedChunks();
                System.out.println();
            }

            System.out.println("All chunks successfully transmitted!");

        } catch (IOException | InterruptedException e) {
            System.out.println("Error handling the file: " + e.getMessage());
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

    // Utility function to print the status of received chunks
    private static void printReceivedChunks() {
        System.out.print("Current received chunks: [");
        for (int i = 0; i < received.length; i++) {
            System.out.print(received[i] ? "true" : "false");
            if (i < received.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            // Taking input from the user for the number of frames (chunks) and window size
            System.out.print("Enter chunk size (in bytes): ");
            int chunkSize = sc.nextInt();

            System.out.print("Enter timeout duration (in milliseconds): ");
            int timeout = sc.nextInt();

            System.out.print("Enter window size: ");
            windowSize = sc.nextInt();

            System.out.print("Enter the input file name: ");
            sc.nextLine(); // Consume the newline character
            String inputFile = sc.nextLine();

            System.out.print("Enter the output file name to store received chunks: ");
            String outputFile = sc.nextLine();

            // Read the file and split it into chunks
            byte[][] chunks = readFileInChunks(inputFile, chunkSize);

            // Set the total number of frames (chunks)
            frameCount = chunks.length;

            // Start sending chunks using Go-Back-N ARQ
            sendFileChunks(chunks, timeout, outputFile);

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
}
