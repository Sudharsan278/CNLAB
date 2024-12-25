package labexpts;

import java.util.Random;
import java.util.Scanner;

public class OneBitSlidingWindow {

	
	 private static boolean ackReceived; // To check if acknowledgment was received

	    // Method to simulate the Stop-and-Wait ARQ protocol
	    public static void sendFrames(int[] frames, int timeout, double lossRate) {
	        Random rand = new Random(); // To simulate random frame loss
	        int frameCount = frames.length; // Total number of frames
	        int frameSent = 0; // Track the current frame being sent

	        while (frameSent < frameCount) {
	            System.out.println("Sending Frame: " + frames[frameSent]);
	            ackReceived = false; // Reset acknowledgment flag

	            // Create a final variable for the current frame
	            final int currentFrame = frameSent;

	            // Start the timer in a separate thread
	            new Thread(() -> {
	                try {
	                    Thread.sleep(timeout);
	                    if (!ackReceived) {
	                        System.out.println("Timeout occurred for Frame " + (currentFrame + 1) + ". Resending...\n");
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
	                System.out.println("Frame " + (currentFrame + 1) + " acknowledged.\n");
	                frameSent++; // Move to the next frame
	            } else {
	                // Simulate frame loss
	                System.out.println("Frame " + (currentFrame + 1) + " lost. Waiting for timeout...\n");
	            }

	            // Wait for acknowledgment reception or timeout before continuing
	            try {
	                Thread.sleep(500); // Simulate time delay between attempts
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	        }

	        System.out.println("All frames sent successfully.");
	    }

	    public static void main(String[] args) {
	        Scanner sc = new Scanner(System.in);

	        // Taking input from the user for the number of frames to send
	        System.out.print("Enter number of frames to send: ");
	        int frameCount = sc.nextInt();

	        // Taking input for timeout duration
	        System.out.print("Enter timeout duration (in milliseconds): ");
	        int timeout = sc.nextInt();

	        // Taking input for loss rate
	        System.out.print("Enter loss rate (0.0 to 1.0, e.g., 0.1 for 10% loss): ");
	        double lossRate = sc.nextDouble();

	        // Create an array of frames to simulate frame numbers
	        int[] frames = new int[frameCount];
	        for (int i = 0; i < frameCount; i++) {
	            frames[i] = i + 1; // Frame numbers start from 1 to frameCount
	        }

	        // Start sending frames using Stop-and-Wait ARQ
	        sendFrames(frames, timeout, lossRate);
	        
	        // Close the scanner
	        sc.close();
	    }
}
