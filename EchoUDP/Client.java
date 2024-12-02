package EchoUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	
	static final int PORT = 12345;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DatagramSocket socket = null;
		
		try {
			
			String hostName = "localhost";
			
			socket = new DatagramSocket();
			
			
			InetAddress serverAddress = InetAddress.getByName(hostName);
			
			try (Scanner sc = new Scanner(System.in)){
				
				System.out.println("Enter the message that you want to send to the server or exit to disconnect! ");

				while(true) {
					String clientMessage = sc.nextLine();
					
					if(clientMessage.equalsIgnoreCase("exit")) {
						break;
					}
					
					byte buffer [] = clientMessage.getBytes();
					
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PORT);
					socket.send(packet);
					
					byte receivedMessage [] = new byte[1024];
					DatagramPacket receivedPacket = new DatagramPacket(receivedMessage, receivedMessage.length);
					socket.receive(receivedPacket);
					
					String serverMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
					System.out.println("Message from Server :- "+ serverMessage);
					
				}	
			}
			
		}catch(Exception ex) {
			System.out.println("Error in Client :- " + ex.getMessage());
		}finally {
			
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		}
		
	}

}
