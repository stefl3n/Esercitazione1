import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		InetAddress addr = null; int port = -1;
		
		try {
			if(args.length == 3) {
				
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
				
			}else {
				
				System.exit(1);
				
			}
			
			
		}catch (UnknownHostException e) {}
		
		DatagramSocket socket = null; DatagramPacket packet = null;
		byte[] buf = new byte[256];
		
		try {
			socket = new DatagramSocket();
			packet = new DatagramPacket(buf, buf.length, addr, port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ByteArrayOutputStream boStream = null; 
			DataOutputStream doStream = null;
			byte[] data = null; String nomeFile = args[2]; 
			boStream = new ByteArrayOutputStream();
			doStream = new DataOutputStream(boStream);
			doStream.writeUTF(nomeFile);
			data = boStream.toByteArray();
			
			packet.setData(data);
			socket.send(packet);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			
			packet.setData(buf); socket.receive(packet);
		}catch(IOException e) {
			
			e.printStackTrace();
		}
		
		ByteArrayOutputStream boStream1 = null;
		DataOutputStream doStream1 = null;
		ByteArrayInputStream biStream = null; 
		DataInputStream diStream = null;
		byte[] linee;
		biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
		diStream = new DataInputStream(biStream);
		int portaRS;
		String lineeSwap = null;
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		portaRS = Integer.parseInt(diStream.readUTF());
		
		packet.setPort(portaRS);
		
		System.out.println("Inserisci le linee da swappare separate da uno spazio");
		
		lineeSwap = stdin.readLine();
		
		if (lineeSwap != null || lineeSwap.equals("-1")) {
			
			boStream1 = new ByteArrayOutputStream();
			doStream1 = new DataOutputStream(boStream1);
			doStream1.writeUTF(lineeSwap);
			linee = boStream1.toByteArray();
			packet.setData(linee);
			socket.send(packet);
			String risposta;
			socket.receive(packet);
			biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
			diStream = new DataInputStream(biStream);
			risposta = diStream.readUTF();
			if(risposta.equals("0")) {
				System.exit(0);
			}else {
				System.exit(1);
			}
			
		}else {
			
			System.exit(1);
			
		}
		
		
	}
}
