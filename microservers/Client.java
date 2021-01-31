import java.io.*;
import java.net.*;

class Client 
{
   public static void main(String args[]) throws Exception
   {
      String sentence;
      String IPAddress="";
      int port=0;
      String commands;
      String modifiedSentence;

      try {
		IPAddress = args[0];
		port = Integer.parseInt(args[1]);	
      } catch (IllegalArgumentException e) {
		System.err.println("First argument (IP Address) " + args[0] + " must be a string");
		System.err.println("Second argument (Port number) " + args[1] + " must be an integer");
		System.exit(-1);
      }

      boolean flag = true;
      System.out.println("Welcome! I am the client end of the client-server application!! \n \n ");

	  while(flag) {
		System.out.println("Please choose from the following selections: \n   1 - Enter a message \n   2 - Enter a command \n   0 - Exit program \nYour desired menu selection? ");
		
		BufferedReader userMenuChoice = new BufferedReader(new InputStreamReader(System.in));
		String userChoice = userMenuChoice.readLine().toString();
		
		if (userChoice.compareTo("0") == 0) { 
			flag = false;
			System.out.println(" Quitting process... ");
			System.exit(0);
		}	
		
		if (userChoice.compareTo("1") == 0) {
			System.out.println("Enter your message: ");
			BufferedReader aMessage = new BufferedReader(new InputStreamReader(System.in));
			sentence = aMessage.readLine();
			
			Socket clientSocket = new Socket(IPAddress, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(sentence + '\n');
	
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String serverResponse = inFromServer.readLine().toString();
			System.out.println("\nAnswer from SERVER: " + serverResponse + "\n");
		}
		
		if (userChoice.compareTo("2") == 0) {
			System.out.println("Enter your numerical command(s): ");
			BufferedReader aCommand = new BufferedReader(new InputStreamReader(System.in));
			commands = aCommand.readLine();
	
			Socket clientSocket = new Socket(IPAddress, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(commands + '\n');
	
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			modifiedSentence = inFromServer.readLine().toString();
			System.out.println("Answer from SERVER: " + modifiedSentence);
		}	
	}
      
    }
}

