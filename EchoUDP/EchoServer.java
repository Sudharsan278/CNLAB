package EchoUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EchoServer {
	
	static final int PORT = 12345; 
	public static void main(String [] args) {
		
		DatagramSocket socket = null;
		
		try {
			
			socket = new DatagramSocket(PORT);
			System.out.println("Server is listening to the port :- "+PORT);		//(Connection-less)	
			
			byte buffer [] = new byte[1024];
			
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			
			while(true) {
				
				socket.receive(packet);
		
				String receivedMessage = new String(packet.getData(), 0, packet.getLength());
			
				System.out.println("Message From Client :- "+ receivedMessage);
				
				
				InetAddress clientAddress = packet.getAddress();
				int clientPort = packet.getPort();
				
				DatagramPacket echoPacket = new DatagramPacket(packet.getData(), packet.getLength(), clientAddress, clientPort);
				
				socket.send(echoPacket);
				
				packet.setLength(buffer.length);
			
			}
			
		}catch(Exception ex) {
			System.out.println("Error in EchoServer :- "+ex.getMessage());
		}finally {
			
			if(socket!= null && !socket.isClosed()) {
				socket.close();
			}
		}
	}
}
