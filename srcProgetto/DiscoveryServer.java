import java.io.*;
import java.net.*;
import java.util.*;

public class DiscoveryServer {
	
	
	public static void main(String args[]) throws IOException {
		
		Hashtable<String, Integer> table=new Hashtable<String, Integer>(args.length-1);
		InputStreamReader in= new InputStreamReader(System.in);
		BufferedReader InRd= new BufferedReader(in);
		String fileName=new String();
		String nomeFile=new String();
		String richiesta=new String();
		
		
		int numLinea;
		int portNum;
		int hostPort;
		
		hostPort=Integer.parseInt(args[0]);
		for(int j=1; j<args.length-1; j+=2)
		{
			fileName=args[j];
			portNum=Integer.parseInt(args[j+1]);
			
			table.put(fileName, portNum);
		}
		for( String key : table.keySet())
		{
			SwapRowServer server=new SwapRowServer(key, table.get(key));
			server.start();
		}
		
		
		
		System.out.println(table.toString());
		DatagramSocket socket = new DatagramSocket(hostPort);
		byte[] buf = new byte[256];
		byte[] data=new byte[256];
		DatagramPacket packet = new DatagramPacket(buf,buf.length);
		
		packet.setData(buf);
		while(true) {
		socket.receive(packet);
		ByteArrayInputStream biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
		DataInputStream diStream = new DataInputStream(biStream); 
		richiesta = diStream.readUTF();
		StringTokenizer st = new StringTokenizer(richiesta);
		nomeFile = st.nextToken();
		
		
		
		ByteArrayOutputStream boStream = new ByteArrayOutputStream();
		DataOutputStream doStream = new DataOutputStream(boStream); 
		doStream.writeUTF(table.get(fileName).toString()); 
		data = boStream.toByteArray();
		
		
		packet.setData(data);
		socket.send(packet);
		
		
		System.out.println(table.get(fileName).toString());
		System.out.println(data.toString());
		
	
		}
		
		
	}

}
