package labexpts;

import java.util.Random;
import java.util.Scanner;

public class OneBitSlidingWindow {

	
	 // Method to simulate the 1-bit Sliding Window Protocol (Stop-and-Wait ARQ)
    public static void sendFrames(int[] frames) {
        Random rand = new Random(); // To simulate random frame loss
        int frameCount = frames.length;
        int frameSent = 0; // Track the current frame being sent
        int ack; // Acknowledgment for the frame

        while (frameSent < frameCount) {
            System.out.println("Sending Frame: " + frames[frameSent]);

            // Simulating acknowledgment or loss (1 means frame received, 0 means lost)
            ack = rand.nextInt(2); // Randomly generates 0 or 1

            if (ack == 1) {
                // Acknowledgment received
                System.out.println("Frame " + (frameSent + 1) + " acknowledged.\n");
                frameSent++; // Move to the next frame
            } else {
                // Simulate frame loss
                System.out.println("Frame " + (frameSent + 1) + " lost. Resending...\n");
                // No increment here, as the frame will be resent
            }
        }

        System.out.println("All frames sent successfully.");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Taking input from the user for the number of frames to send
        System.out.print("Enter number of frames to send: ");
        int frameCount = sc.nextInt();

        // Create an array of frames to simulate frame numbers
        int[] frames = new int[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = i + 1; // Frame numbers start from 1 to frameCount
        }

        // Start sending frames using 1-bit sliding window protocol (Stop-and-Wait ARQ)
        sendFrames(frames);
        sc.close();
    }

}
