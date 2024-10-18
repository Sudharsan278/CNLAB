package labexpts;

import java.util.Random;
import java.util.Scanner;

public class GoBackN {

	
	
	  static int frameCount, windowSize, framesSent = 0;

	    // Method to send frames based on sliding window protocol
	    public static void sendFrames(int[] frames) {
	        Random rand = new Random(); // To simulate random events like frame loss
	        int framesReceived = 0; // Counter to keep track of how many frames are successfully received

	        // Loop until all frames are successfully transmitted
	        while (framesReceived < frameCount) {
	            System.out.println("\nSending frames...");

	            // Transmit up to window size frames at a time
	            for (int i = 0; i < windowSize && framesSent < frameCount; i++) {
	                System.out.println("Sent Frame: " + frames[framesSent]); // Sending a frame
	                framesSent++; // Increment the frames sent counter
	            }

	            // Simulating the acknowledgment process
	            // Randomly simulating the number of frames acknowledged (or a loss)
	            int ackFrame = rand.nextInt(windowSize + 1); // Generates a random number between 0 and windowSize

	            if (ackFrame == windowSize) {
	                // Simulating the case where all frames sent in the window are acknowledged
	                System.out.println("All frames acknowledged for this window.");
	                framesReceived += windowSize; // Move the received counter forward by window size
	            } else {
	                // Simulating a scenario where a frame is lost and Go-Back-N is triggered
	                System.out.println("Frame " + (framesReceived + ackFrame + 1) + " lost. Go-Back-N triggered.");
	                
	                // Reset the framesSent counter to the last unacknowledged frame
	                // Resend from the frame where loss occurred
	                framesSent = framesReceived + ackFrame; // Resending starts from lost frame
	                framesReceived += ackFrame; // Increment received counter by how many were acknowledged
	            }
	        }

	        // When all frames are transmitted successfully
	        System.out.println("All frames sent successfully.");
	        
	    }

	    public static void main(String[] args) {
	        Scanner sc = new Scanner(System.in);
	        

	        // Taking input from the user for the number of frames and window size
	        System.out.print("Enter number of frames to send: ");
	        frameCount = sc.nextInt(); // Total number of frames to be sent

	        System.out.print("Enter window size: ");
	        windowSize = sc.nextInt(); // Size of the sliding window (how many frames can be sent at once)

	        // Creating an array of frames to simulate frame numbers
	        int[] frames = new int[frameCount];
	        for (int i = 0; i < frameCount; i++) {
	            frames[i] = i + 1; // Frame numbers start from 1 to frameCount
	        }

	        // Start sending the frames using the Go-Back-N sliding window protocol
	        sendFrames(frames);
	        sc.close();
	    }
}
