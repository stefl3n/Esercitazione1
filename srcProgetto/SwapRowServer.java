import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class SwapRowServer extends Thread {
	DatagramSocket socket = null;
	DatagramPacket packet = null;
	byte[] buf = null;
	String filename = null;
	int port;
	
	public SwapRowServer (String filename, int port) {
		this.filename = filename;
		this.port = port;
		
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buf = new byte[256];
		this.packet = new DatagramPacket(buf, buf.length);
	}
	
	public void run() {
		
		while(true) {
			int result = -1;
			byte[] data = null;
			
			packet.setData(buf);
			try {
				socket.receive(packet);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				ByteArrayInputStream biStream = new ByteArrayInputStream(packet.getData(),0, packet.getLength());
				DataInputStream diStream = new DataInputStream(biStream);
				String richiesta = null;
				
				try {
					richiesta = diStream.readUTF();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				StringTokenizer st = new StringTokenizer(richiesta);
				int riga1 = Integer.parseInt(st.nextToken());
				int riga2 = Integer.parseInt(st.nextToken());
				
				try {
					result = scambiaRighe(riga1, riga2);
				} catch (IOException e) {
					System.out.println("Errore BufferedReader/BufferedWriter.");
				}
				
				ByteArrayOutputStream boStream = new ByteArrayOutputStream();
				DataOutputStream doStream = new DataOutputStream(boStream);
				
				try {
					doStream.writeUTF(Integer.toString(result));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				data = boStream.toByteArray();
				packet.setData(data);
				
				try {
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private int scambiaRighe (int nRiga1, int nRiga2) throws IOException {
		String riga1 = null;
		String riga2= null;
		String line = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		
		//Leggo le due righe da scambiare
		try {
			br = new BufferedReader(new FileReader(this.filename));
		} catch (FileNotFoundException e1) {
			System.out.println("File non trovato durante l'apertura del BufferedReader per la LETTURA delle due righe.");
			return 1;
		}
		
		for(int i=1; i<=(nRiga1>nRiga2 ? nRiga1 : nRiga2); i++) {
			
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("Errore nella lettura della linea n° "+ i);
				br.close();
				return 2;
			}
			
			if(i==nRiga1)
				riga1 = line;
			else if(i==nRiga2)
				riga2 = line;
		}
		
		try {
			br.close();
		} catch (IOException e1) {
			System.out.println("Errore chiusura BufferedReader per la LETTURA delle due righe.");
			return 2;
		}
		
		
		//Apro un file temp e copio il file originale con le righe scambiate
		File temp = new File(this.filename+"Temp.txt");
		try {
			br = new BufferedReader(new FileReader(this.filename));
		} catch (FileNotFoundException e) {
			System.out.println("File non trovato durante l'apertura del BufferedReader per la LETTURA di tutte le righe. ");
			return 1;
		}
		try {
			bw = new BufferedWriter(new FileWriter(temp));
		} catch (IOException e) {
			System.out.println("Errore apertura BufferedReader per la SCRITTURA del file temporaneo.");
			br.close();
			return 2;
		}
		
		int i=1;
		try {
			while((line=br.readLine())!=null) {
				if(i==nRiga1) {
					bw.write(riga2);
					bw.newLine();
				}
				else if(i==nRiga2) {
					bw.write(riga1);
					bw.newLine();
				}
				else {
					bw.write(line);
					bw.newLine();
				}
				i++;
			}
		} catch (IOException e) {
			System.out.println("Errore nella SCRITTURA delle linee sul file temporaneo.");
			bw.close();
			br.close();
			return 2;
		}
		
		try {
			br.close();
			bw.close();
		} catch (IOException e1) {
			System.out.println("Errore chiusura BufferedReader/BufferedWriter lettura dal file sul file temporaneo.");
			return 2;
		}
		
		
		//Elimino il file originale e rinomino il file temp
		try {
			Files.delete(Paths.get(this.filename));
		} catch (IOException e) {
			System.out.println("Errore durante l'eliminazione del file originale.");
			return 2;
		}
		temp.renameTo(new File(this.filename));	
		return 0;
	}
}
