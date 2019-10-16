import java.io.*;
import java.net.*;
import java.util.*;

public class DiscoveryServer {
	
	
	public static void main(String args[])  {
		
		Hashtable<String, Integer> table=new Hashtable<String, Integer>(args.length-1);
		InputStreamReader in= new InputStreamReader(System.in);
		BufferedReader InRd= new BufferedReader(in);
		String fileName=new String();
		String nomeFile=new String();
		String richiesta=new String();
		
		
		int numLinea;
		int portNum;
		int hostPort;
		
		if((args.length%2)!=0)//controllo numero argomenti
		{
			System.out.println("Numero argomenti errato");
			System.exit(-1);
		}
		
		try 
		{
		hostPort=Integer.parseInt(args[0]);
		if(hostPort<1024)//controllo numero porta
			{
				System.out.println("Inserire un numero di porta valido per il server");
				System.exit(-1);
			}
			
		
		
		for(int j=1; j<args.length-1; j+=2)
		{
			fileName=args[j];
			
			portNum=Integer.parseInt(args[j+1]);
				if(portNum==hostPort||portNum<1024) //controllo porte del Thread
				{
					System.out.println("inserire numeri di porta validi"); 
					System.exit(-1);
				}
				else if(table.containsValue(portNum))//controllo unicita  porte del Thread
					{
						System.out.println("Porta gia inserita:"+portNum);
						System.exit(-1);
						
					}
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
			
			while(true) {
				
			packet.setData(buf);
			socket.receive(packet);
			ByteArrayInputStream biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
			DataInputStream diStream = new DataInputStream(biStream); 
			richiesta = diStream.readUTF();
			StringTokenizer st = new StringTokenizer(richiesta);
			nomeFile = st.nextToken();
			
			
			
			ByteArrayOutputStream boStream = new ByteArrayOutputStream();
			DataOutputStream doStream = new DataOutputStream(boStream); 
			if(table.get(nomeFile)==null)//invio messaggio di errore al client
			{
				System.out.println("file non esistente");
				doStream.writeUTF("-1"); 
			}
			
			else
			{
				doStream.writeUTF(table.get(nomeFile).toString());
				System.out.println(table.get(nomeFile).toString());
			}
			
			data = boStream.toByteArray();
			
			
			packet.setData(data);
			socket.send(packet);
	
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		catch(SocketException e )
		{
			System.err.println("SocketException ");
			System.out.println("errore nella creazione della socket");
			e.printStackTrace();
			System.exit(-2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-3);
		}
		
		
	}
}
