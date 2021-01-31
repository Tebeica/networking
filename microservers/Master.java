import java.io.*;
import java.net.*;


class Master 
{
   public static String clientMessage;
   public static void main(String argv[]) throws Exception
   {
      String clientRequest;
      String clientCommands;
      String modifiedMessage; 
      ServerSocket welcomeSocket = new ServerSocket(8080);
      System.out.println("Master server is listening on TCP port 8080...");
      while(true) 
      {
        	Socket connectionSocket = welcomeSocket.accept();

        	BufferedReader inFromClient =
            	new BufferedReader(
           		new InputStreamReader(
            		connectionSocket.getInputStream()));

		clientRequest = inFromClient.readLine().toString();

		if (!isNumeric(clientRequest)) {
			System.out.println("Alphanumeric characters received from client, parsing as message");
			System.out.println(clientRequest);
			clientMessage = clientRequest;
			
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes("OK! Command? \n");
		} else {
			System.out.println("Numeric characters only received from client, parsing as commands");
			System.out.println(clientRequest);
			clientCommands = clientRequest;
			modifiedMessage = clientMessage;
			for (char ch: clientCommands.toCharArray()) {
				int com = Character.getNumericValue(ch);
				modifiedMessage = handleCommands(com, modifiedMessage);
				System.out.println("\t From microserver: " + modifiedMessage);
			}
			// this is where the final string is returned to client
			System.out.println("FINALLY: " + modifiedMessage);
			
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes(modifiedMessage + "\n");
		}
      }
   }

   public static String handleCommands(int command, String message) throws Exception{
	int[] servers = {8081, 8082, 8083, 8084, 8085, 8086};
	DatagramSocket microSocket = new DatagramSocket();
	//take IPaddress from global variable
	InetAddress IPAddress = InetAddress.getByName("localhost");

	byte[] sendData;
	byte[] receiveData = new byte[1024];
	sendData = message.getBytes();

	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, servers[command-1]);
	microSocket.send(sendPacket);
	

	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	microSocket.setSoTimeout(10000);
	
	while(true) {
		try {
			microSocket.receive(receivePacket);
			//Thread.sleep(1000);
			String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
			return modifiedSentence;
		}
		catch (SocketTimeoutException e) {
			System.out.println("Timeout reached!" + e);
			String modifiedSentence = "TIMEOUT";
			microSocket.close();
			return modifiedSentence;
		}
	}

   }

   public static boolean isNumeric(String str) {
	if (str == null || str.length() == 0) {
		return false;
	}
	try {
		Double.parseDouble(str);
		return true;
   	} catch (NumberFormatException e) {
		return false;
	}
   }
}
