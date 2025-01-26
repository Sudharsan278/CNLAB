package labexpts;

import java.io.*;
import java.util.Scanner;
import java.util.Random;

public class SelectiveRepeatFile {
    static boolean[] received;  // Track received frames
    static int[] retries;       // Count retries for each frame
    static int[] timer;         // Timer for each frame

    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();

        // Get user input
        System.out.println("Enter the source file name (with extension): ");
        String sourceFileName = scanner.nextLine();

        System.out.println("Enter the destination file name (with extension): ");
        String destinationFileName = scanner.nextLine();

        System.out.println("Enter the window size: ");
        int windowSize = scanner.nextInt();

        System.out.println("Enter the size of each chunk (in bytes): ");
        int chunkSize = scanner.nextInt();

        System.out.println("Enter the timeout duration (in simulation steps): ");
        int timeout = scanner.nextInt();

        File sourceFile = new File("src/" + sourceFileName);
        File destinationFile = new File("src/" + destinationFileName);

        // Ensure source file exists
        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist!");
            return;
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {
            
            // Calculate the total number of frames (chunks) based on file size
            long fileSize = sourceFile.length();
            int totalFrames = (int) Math.ceil((double) fileSize / chunkSize);

            // Initialize arrays for tracking frames
            received = new boolean[totalFrames];
            retries = new int[totalFrames];
            timer = new int[totalFrames];

            for (int i = 0; i < totalFrames; i++) {
                timer[i] = timeout;  // Set initial timeout for all frames
            }

            int base = 0;  // Base of the sliding window
            byte[] buffer = new byte[chunkSize];

            System.out.println("\nStarting Selective Repeat File Transfer...\n");

            while (base < totalFrames) {
                for (int i = base; i < base + windowSize && i < totalFrames; i++) {
                    if (!received[i] && retries[i] < 3) {
                        if (timer[i] == 0) {  // Timeout, resend
                            System.out.println("Timeout! Resending Frame " + i);

                            inputStream.mark(chunkSize);  // Mark the current position
                            inputStream.skip((long) i * chunkSize);  // Move to the frame position
                            int bytesRead = inputStream.read(buffer, 0, chunkSize);  // Read chunk

                            if (bytesRead > 0) {
                                if (rand.nextInt(10) < 7) {  // 70% chance of success
                                    System.out.println("Frame " + i + " sent successfully.");
                                    outputStream.write(buffer, 0, bytesRead);  // Write chunk to destination
                                    received[i] = true;  // Mark frame as received
                                } else {
                                    System.out.println("Frame " + i + " lost.");
                                    retries[i]++;
                                }
                            }
                            inputStream.reset();  // Reset to the marked position
                            timer[i] = timeout;  // Reset timer
                        }
                    }
                }

                Thread.sleep(1000);  // Simulate time delay
                updateTimers();

                // Slide the window forward
                while (base < totalFrames && received[base]) {
                    System.out.println("Sliding window. New base = " + (base + 1));
                    base++;
                }

                printReceivedFrames(totalFrames);
            }

            System.out.println("\nFile transfer completed successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    // Update timers for each frame
    private static void updateTimers() {
        for (int i = 0; i < timer.length; i++) {
            if (!received[i] && retries[i] < 3 && timer[i] > 0) {
                timer[i]--;
            }
        }
    }

    // Print the status of received frames
    private static void printReceivedFrames(int totalFrames) {
        System.out.print("Current received frames: [");
        for (int i = 0; i < totalFrames; i++) {
            System.out.print(received[i] ? "true" : "false");
            if (i < totalFrames - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}
