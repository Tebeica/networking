import java.io.*;
import java.net.*;

class Caesar 
{
   public static void main(String args[]) throws Exception
   {

      DatagramSocket serverSocket = 
         new DatagramSocket(8085);

      byte[] receiveData = new byte[1024];
      byte[] sendData;

      while(true)
      {
         DatagramPacket receivePacket =
            new DatagramPacket(receiveData, receiveData.length);

         serverSocket.receive(receivePacket);

         String sentence = new String(receivePacket.getData(),
                                      0, receivePacket.getLength());

         InetAddress IPAddress = receivePacket.getAddress();

         int port = receivePacket.getPort();

         String CaesarSentence = Caesar.cipher(sentence, 2);

         sendData = CaesarSentence.getBytes();

         DatagramPacket sendPacket =
            new DatagramPacket(sendData, sendData.length,
                               IPAddress, port);

         serverSocket.send(sendPacket);
      }
   }
//https://stackoverflow.com/questions/19108737/java-how-to-implement-a-shift-cipher-caesar-cipher
    public static String cipher(String msg, int shift){
        StringBuilder result = new StringBuilder();
	for (char character : msg.toCharArray()) {
	    if (Character.isLetter(character)) { 
	        int originalAlphabetPosition = character - 'a';
      	        int newAlphabetPosition = (originalAlphabetPosition + shift) % 26;
		char newCharacter = (char) ('a' + newAlphabetPosition);
		result.append(newCharacter);
	    } else {
		result.append(character);
	    }
	}
	return result.toString();    
    }
}
