import java.io.*;
import java.net.*;
import java.util.*;

/*
 * UCID: 30046038
 * Course: CPSC 441
 * Assignment 3
 * PingClient.java to be ran after PingServer.java is running
 * This program sends a UDP packet to the server and expects a 
 * reply, calculating the delay of each individual ping and the
 * average delay of the 10 requests
 * 
 */

public class PingClient
{
	private static final int MAX_TIMEOUT = 1000;
	private static long[] delays = new long[10];
	
	public static void main(String[] args) throws Exception
	{
		// Get command line arguments.
		if (args.length != 2) {
			System.out.println("Required arguments: Server port");
			return;
		}
		// Port number
		int port = Integer.parseInt(args[1]);
		// Server address
		InetAddress serverAddr;
		serverAddr = InetAddress.getByName(args[0]);

		// Create a datagram socket for sending and receiving UDP packets
		// through the port specified on the command line.
		DatagramSocket datagramSock = new DatagramSocket(port);

		int sequence_number = 0;
		// Processing loop.
		while (sequence_number < 10) {
			// Timestamp in ms when PING is sent
			Date now = new Date();
			long sendTime = now.getTime();

			String str = "PING " + sequence_number + " " + sendTime + " \n";
			byte[] buf = new byte[1024];
			buf = str.getBytes();
			// Create a datagram packet to send as an UDP packet.
			DatagramPacket ping = new DatagramPacket(buf, buf.length, serverAddr, port);

			// Send the Ping datagram to the specified server
			datagramSock.send(ping);
			// Try to receive the packet
			try {
				// Set up the timeout 1000 ms
				datagramSock.setSoTimeout(MAX_TIMEOUT);
				// Set up an UPD packet for recieving
				DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
				// Try to receive the response from the ping
				datagramSock.receive(response);				
				// Timestamp when we received packet back
				now = new Date();
				long receivedTime = now.getTime();
				
				delays[sequence_number] = receivedTime-sendTime;
				
				// Print the packet and the delay
				printData(response, receivedTime - sendTime);
			} catch (IOException e) {
				// Print which packet has timed out
				delays[sequence_number] = MAX_TIMEOUT;
				System.out.println("Timeout for packet " + sequence_number);
			}
			
			sequence_number ++;
		}
		printFooter(delays);
	}

	
	private static void printFooter(long[] del) {
		long total = 0;
		long min = 1000;
		long max = 0;
		for (int i = 0; i < del.length; i++) {
			total = total + del[i];
			if (del[i] > max) {
				max = del[i];
			} else if (del[i] < min) {
				min = del[i];
			}
		}
		long avg = total / del.length;
		System.out.println("RTT: minDelay: " + min + "ms / maxDelay: " + max + "ms / averageDelay: " + avg + "ms");
	}
	
	
   /* 
    * Print ping data to the standard output stream.
    * slightly changed from PingServer
    */
   private static void printData(DatagramPacket request, long delayTime) throws Exception
   {
      // Obtain references to the packet's array of bytes.
      byte[] buf = request.getData();

      ByteArrayInputStream bais = new ByteArrayInputStream(buf);

      InputStreamReader isr = new InputStreamReader(bais);

      BufferedReader br = new BufferedReader(isr);

      String line = br.readLine();
      
      System.out.println("Delay " + delayTime + " ms: Received from " + request.getAddress().getHostAddress() + ": "+ new String(line));
   }
}