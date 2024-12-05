package labexpts;

import java.util.Scanner;

public class CRC {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			Scanner sc = new Scanner(System.in);
	        System.out.println("Enter data bits : ");
	        String data = sc.next();
	        
	        System.out.println("Enter the generator bits :- ");
	        String generator = sc.next();
	        
	        int generatorLength = generator.length();

	        String dataAppended = data + "0".repeat(generatorLength - 1);
	            
	        String crc = DivideDataWithGenerator(dataAppended, generator);
	        
	        String transmittedData = data + crc;
	        
	        System.out.println("Transmitted Data (data + crc) = "+ transmittedData);
	        
	        System.out.println("Enter Receiver Data :- ");
	        String receiverData = sc.next();
	        
	        String verification = DivideDataWithGenerator(receiverData, generator);
	        
	        // Checking the remainder after division
	        if(verification.equals("0".repeat(generatorLength - 1))){
	            System.out.println("No error detected!");
	        } else {
	            System.out.println("Error Detected in received data");
	        }
	        
	        // Close the scanner object
	        sc.close();
	    }
	    
	    
	    public static String DivideDataWithGenerator(String data, String generator) {
	       
	    	int generatorLength = generator.length();
	        
	    	String temp = data.substring(0, generatorLength);
	        
	        for (int i = generatorLength; i < data.length(); i++) {
	            if (temp.charAt(0) == '1') {
	                temp = xor(temp, generator);  // XOR when the leading bit is 1
	            } else {
	                temp = xor(temp, "0".repeat(generatorLength));  // Otherwise, XOR with zeros
	            }

	            // Append the next bit from the data
	            temp = temp.substring(1) + data.charAt(i);
	        }

	        // Perform the last XOR after reaching the end of data
	        if (temp.charAt(0) == '1') {
	            temp = xor(temp, generator);
	        } else {
	            temp = xor(temp, "0".repeat(generatorLength));
	        }

	        // Return the remainder (ignore the leading bit after division)
	        return temp.substring(1);
	    }

	    // XOR operation between two binary strings
	    public static String xor(String a, String b) {
	        StringBuilder result = new StringBuilder();
	        for (int i = 0; i < a.length(); i++) {
	            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
	        }
	        return result.toString();
	    }

}
