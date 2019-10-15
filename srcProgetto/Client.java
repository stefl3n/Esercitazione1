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
			
			
		}catch (UnknownHostException e) { System.err.println("UnknownHostException for "+args[0]);
										  System.out.println("Host sconosciuto, esco");
										  System.exit(2);}
		catch( NumberFormatException e) {System.err.println(args[1]+" non e' un intero");
									     System.out.println("Inserire host e porta(intero)");
									     System.exit(3);}
		
		DatagramSocket socket = null; DatagramPacket packet = null;
		byte[] buf = new byte[256];
		
		try {
			socket = new DatagramSocket();
			packet = new DatagramPacket(buf, buf.length, addr, port);
		} catch (SocketException e) {
			
			System.err.println("SocketException ");
			System.out.println("errore nella creazione della socket");
			e.printStackTrace();
			System.exit(4);
		}
		
		
		//richiesta file al server
		byte[] data = null;
		try {
			ByteArrayOutputStream boStream = null; 
			DataOutputStream doStream = null;
			 
			String nomeFile = args[2]; 
			boStream = new ByteArrayOutputStream();
			doStream = new DataOutputStream(boStream);
			doStream.writeUTF(nomeFile);
			data = boStream.toByteArray();
		} catch (IOException ioe) {
			System.err.println("IOException writeUTF");
			System.out.println("errore nella writeUTF");
			System.exit(4);
		}
		
		try {
			packet.setData(data);
			socket.send(packet);
			
		}
		catch(IOException ioe) {
			System.err.println("IOException socket send");
			System.out.println("errore nell'invio al server");
			System.exit(5);
		}
		
		try {//ricezione porta
			
			packet.setData(buf); 
			socket.receive(packet);
		}catch(IOException e) {
			System.err.println("IOException socket receive");
			System.out.println("errore nella ricezione dal server");
			System.exit(5);
		}
		
		ByteArrayOutputStream boStream1 = null;
		DataOutputStream doStream1 = null;
		ByteArrayInputStream biStream = null; 
		DataInputStream diStream = null;
		byte[] linee;
		biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
		diStream = new DataInputStream(biStream);
		int portaRS;//Hp:server inviera' sicuramente un intero
		String lineeSwap = null;
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		portaRS = Integer.parseInt(diStream.readUTF());
		
		//ARRIVATO QUI, TERMINO ENTRO DOMANI
		
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