package labexpts;

import java.util.Scanner;

public class HammingDistance {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

        // Input two binary strings
        System.out.print("Enter the first binary string: ");
        String str1 = sc.nextLine();

        System.out.print("Enter the second binary string: ");
        String str2 = sc.nextLine();

        // Check if both strings have the same length
        if (str1.length() != str2.length()) {
            System.out.println("Error: Strings must be of equal length");
            return;
        }

        // Calculate the Hamming distance
        int hammingDistance = calculateHammingDistance(str1, str2);
        System.out.println("Hamming Distance: " + hammingDistance);
        
        sc.close();
	}
	
	// Function to calculate Hamming Distance
    public static int calculateHammingDistance(String str1, String str2) {
        int distance = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

	
	
}
