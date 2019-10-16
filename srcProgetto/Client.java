import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {
	public static void main(String[] args) {
		
		InetAddress addr = null; int port = -1; String nomeFile = null;
		
		try {
			if(args.length == 3) {
				
				nomeFile = args[2];
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
				System.out.println(addr.getHostAddress());
				
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
		ByteArrayOutputStream boStream = null; 
		DataOutputStream doStream = null;
		try {
			
			 
			boStream = new ByteArrayOutputStream();
			doStream = new DataOutputStream(boStream);
			doStream.writeUTF(nomeFile);
			data = boStream.toByteArray();
		} catch (IOException ioe) {
			System.err.println("IOException writeUTF");
			System.out.println("errore nella writeUTF");
			System.exit(5);
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
		int portaRS = -1;//Hp:server inviera' sicuramente un intero
		String lineeSwap = null;
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		
			try {
				
				portaRS = Integer.parseInt(diStream.readUTF());
				if(portaRS==-1) //il file non esiste
				{
					System.out.println("il file non esiste");
					System.exit(-1);
				}
				packet.setPort(portaRS);//Setto la porta a cui richiedere il cambio riga
				
				System.out.println("Inserisci le linee da swappare separate da uno spazio");
				
				lineeSwap = stdin.readLine();
				
			} catch (NumberFormatException e) {
				
				System.err.println(portaRS+" non e' un intero");
			     System.out.println("Inserire porta(intero)");
			     System.exit(3);
			     
			} catch (IOException e) {
				
				System.err.println("IOException readUTF");
				System.out.println("errore nella readUTF");
				System.exit(5);
			}
			
			if (lineeSwap != null && !lineeSwap.equals("-1")) {
				
				boStream1 = new ByteArrayOutputStream();
				doStream1 = new DataOutputStream(boStream1);
				try {
					doStream1.writeUTF(lineeSwap);
				} catch (IOException e) {
					System.out.println("Errore writeUTF");
					e.printStackTrace();
					System.exit(5);
					
				}
				linee = boStream1.toByteArray();
				packet.setData(linee);
				try {// invio righe da scambiare
					socket.send(packet);
				} catch (IOException e) {
					System.err.println("IOException socket send");
					System.out.println("errore nell'invio al server");
					System.exit(5);
				}
				String risposta = null;
				try {//Ricezione risposta da RS
					
					socket.receive(packet);
				
				} catch (IOException e) {
					System.err.println("IOException socket receive");
					System.out.println("errore nella ricezione dal server");
					System.exit(5);
				}
				biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
				diStream = new DataInputStream(biStream);
				try {
					risposta = diStream.readUTF();
				} catch (IOException e) {
					System.err.println("IOException readUTF");
					System.out.println("errore nella readUTF");
					System.exit(5);
				}
				if(risposta.equals("0")) {
					System.out.println("Successo");
					System.exit(0);
				}else {
					System.out.println("Errore");
					System.exit(1);
				}
			System.exit(0);	
			}else {
				
				System.out.println("Errore inserimento righe");
				System.exit(1);
				
			}
		
		
		
	}
}
