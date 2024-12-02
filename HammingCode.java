package labexpts;

import java.util.Scanner;

public class HammingCode {
	
	public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the data bits (e.g., 1011): ");
        String data = sc.next();

        // Calculate and display the Hamming Code
        String hammingCode = encodeHammingCode(data);
        System.out.println("Hamming Code: " + hammingCode);

        System.out.println("Enter received code (Hamming Code) to check for errors: ");
        String receivedCode = sc.next();
        decodeHammingCode(receivedCode);
        
        sc.close();
    }

    // Function to calculate Hamming Code
    public static String encodeHammingCode(String data) {
        // Calculate the number of parity bits required
        int m = data.length(); // Number of data bits
        int r = 0; // Number of parity bits
        while (Math.pow(2, r) < (m + r + 1)) {
            r++;
        }

        // Create an array to hold the codeword
        char[] hammingCode = new char[m + r];

        // Place the data bits in the positions that are not powers of 2
        int j = 0, k = 0;
        for (int i = 0; i < hammingCode.length; i++) {
            if (isPowerOfTwo(i + 1)) {
                hammingCode[i] = '0'; // Placeholder for parity bits
            } else {
                hammingCode[i] = data.charAt(j++);
            }
        }

        // Calculate the parity bits
        for (int i = 0; i < r; i++) {
            int parityIndex = (int) Math.pow(2, i) - 1; // Position of parity bit
            int parityValue = 0;

            // Calculate the parity value for this parity bit
            for (int x = 1; x <= hammingCode.length; x++) {
                if (((x >> i) & 1) == 1) {
                    parityValue ^= hammingCode[x - 1] - '0';
                }
            }

            hammingCode[parityIndex] = (char) (parityValue + '0');
        }

        return new String(hammingCode);
    }

    // Function to decode the Hamming Code and check for errors
    public static void decodeHammingCode(String receivedCode) {
        int n = receivedCode.length();
        int r = 0; // Number of parity bits
        int[] parityPositions = new int[n];

        // Calculate number of parity bits
        while (Math.pow(2, r) < n + 1) {
            r++;
        }

        // Check for errors using the parity bits
        int errorPosition = 0;
        for (int i = 0; i < r; i++) {
            int parityValue = 0;
            int parityIndex = (int) Math.pow(2, i) - 1; // Position of parity bit

            // Calculate the parity value for this parity bit
            for (int j = 1; j <= n; j++) {
                if (((j >> i) & 1) == 1) {
                    parityValue ^= receivedCode.charAt(j - 1) - '0';
                }
            }

            if (parityValue != 0) {
                errorPosition += (int) Math.pow(2, i);
            }
        }

        if (errorPosition == 0) {
            System.out.println("No error detected in the received code.");
        } else {
            System.out.println("Error detected at position: " + errorPosition);
            System.out.println("Corrected Hamming Code: " + correctError(receivedCode, errorPosition));
        }
    }

    // Function to correct the error in the received code
    public static String correctError(String receivedCode, int errorPosition) {
        char[] correctedCode = receivedCode.toCharArray();
        correctedCode[errorPosition - 1] = (correctedCode[errorPosition - 1] == '0') ? '1' : '0';
        return new String(correctedCode);
    }

    // Helper function to check if a number is a power of two
    public static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

}
