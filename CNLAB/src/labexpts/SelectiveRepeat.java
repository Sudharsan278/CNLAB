package labexpts;

import java.util.Random;

public class SelectiveRepeat {
    static final int TOTAL_FRAMES = 10;  // Total number of frames to send
    static final int WINDOW_SIZE = 4;    // Size of the sliding window
    static final int MAX_RETRIES = 3;    // Maximum retransmission attempts for each frame
    static final int TIMEOUT = 3;        // Timeout duration (in simulation steps)
    
    static boolean[] received = new boolean[TOTAL_FRAMES];  // Track received frames
    static int[] retries = new int[TOTAL_FRAMES];           // Count retries for each frame
    static int[] timer = new int[TOTAL_FRAMES];             // Timer for each frame

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();
        int base = 0;  // Base of the sliding window

        System.out.println("Starting Selective Repeat Protocol Simulation with Timers...\n");

        // Initialize timers to TIMEOUT for each frame
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            timer[i] = TIMEOUT;
        }

        // Continue until all frames are sent and acknowledged
        while (base < TOTAL_FRAMES) {
            // Attempt to send frames within the current window
            for (int i = base; i < base + WINDOW_SIZE && i < TOTAL_FRAMES; i++) {
                // Check if frame needs to be resent due to timeout
                if (!received[i] && retries[i] < MAX_RETRIES) {
                    if (timer[i] == 0) {  // Timer expired, so resend
                        System.out.println("Timeout! Resending Frame " + i);
                        if (rand.nextInt(10) < 7) {  // 70% chance of success
                            System.out.println("Resending Frame " + i + ": Success");
                            received[i] = true;  // Mark frame as received
                            System.out.println("Receiver: Frame " + i + " received. Sending ACK " + i);
                        } else {
                            System.out.println("Resending Frame " + i + ": Lost");
                            retries[i]++;  // Increment retry count
                        }
                        timer[i] = TIMEOUT;  // Reset timer after sending
                    }
                }
            }

            // Pause for a short duration to simulate time delay (optional)
            Thread.sleep(1000);

            // Update timers for frames that are still pending acknowledgment
            updateTimers();

            // Slide the window forward if possible
            while (base < TOTAL_FRAMES && received[base]) {
                System.out.println("Sliding window. New base = " + (base + 1));
                base++;
            }

            // Display the status of received frames
            printReceivedFrames();
            System.out.println();
        }

        System.out.println("All frames successfully transmitted!");
    }

    // Update timers for each frame
    private static void updateTimers() {
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            if (!received[i] && retries[i] < MAX_RETRIES && timer[i] > 0) {
                timer[i]--;  // Decrease the timer if the frame is not yet acknowledged
            }
        }
    }

    // Utility function to print the status of received frames
    private static void printReceivedFrames() {
        System.out.print("Current received frames: [");
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            System.out.print(received[i] ? "true" : "false");
            if (i < TOTAL_FRAMES - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}
