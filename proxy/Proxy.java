//Name: Teodor Tebeica
//UCID: 30046038

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;


public class Proxy {
    /** Port for the proxy */
    private static int port;
    /** Socket for client connections */
    private static ServerSocket socket;
    
    //private static Map cache;


    /** Create the Proxy object and the socket */
    public static void init(int p) {
	port = p;
	try {
	    socket = new ServerSocket(port);
	} catch (IOException e) {
	    System.out.println("Error creating socket: " + e);
	    System.exit(-1);
	}
    }

    public static void handle(Socket client) {
        Socket server = null;
        HttpRequest request = null;
        HttpResponse response = null;

        /* Process request. If there are any exceptions, then simply
        * return and end this request. This unfortunately means the
        * client will hang for a while, until it timeouts. */

        /* Read request */
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader (client.getInputStream()));
            request = new HttpRequest(fromClient);
            // replacing the URI's "NBA" attribute to "TBA" in order to have access to the updated pictures
            request.URI = request.URI.replace("NBA", "TBA");
        } catch (IOException e) {
            System.out.println("Error reading request from client: " + e);
            return;
        }
        /* Send request to server */
        try {
            /* Open socket and write request to socket */
            server = new Socket(request.getHost(), request.getPort());
            DataOutputStream toServer = new DataOutputStream(server.getOutputStream());
            toServer.writeBytes(request.toString());

        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + request.getHost());
            System.out.println(e);
            return;
        } catch (IOException e) {
            System.out.println("Error writing request to server: " + e);
            return;
        }
        /* Read response and forward it to client */
        try {
            DataInputStream fromServer = new DataInputStream (server.getInputStream());
            response = new HttpResponse(fromServer);
            
            // differentiate text from images in order to edit it
            if (response.toString().contains("Content-Type: text")) {

                String temp = new String(response.body, StandardCharsets.UTF_8);
                temp = temp.replaceAll("\\sNBA", " TBA");
                temp = temp.replaceAll("NBA<", "TBA<");
                temp = temp.replaceAll(">NBA", ">TBA");
                
                temp = temp.replaceAll("\\s2019", " 2219");
                temp = temp.replaceAll("2019<", "2219<");
                temp = temp.replaceAll(">2019", ">2219");

                temp = temp.replaceAll("\\sWorld", " Titan");
                temp = temp.replaceAll("World<", "Titan<");
                temp = temp.replaceAll(">World", ">Titan");
                temp = temp.replaceAll("world.", "Titan.");

                temp = temp.replaceAll("\\Drummond", " Kobe-B24");
                temp = temp.replaceAll("Drummond<", "Kobe-B24<");
                temp = temp.replaceAll(">Drummond", ">Kobe-B24");
                
                response.body = temp.getBytes(StandardCharsets.UTF_8);
            }
            // initialize new output stream
            DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
            // Send header
            toClient.writeBytes(response.toString().toUpperCase());     
            // Send body
            toClient.write(response.body);                              
            

            /* Write response to client. First headers, then body */
            client.close();
            server.close();

            //cache.put(request, response);
            /* Insert object into the cache */
            /* Fill in (optional exercise only) */
        } catch (IOException e) {
            System.out.println("Error writing response to client: " + e);
        }
    }


    /** Read command line arguments and start proxy */
    public static void main(String args[]) {
        int myPort = 0;
        
        try {
            myPort = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) { 
            System.out.println("Need port number as argument");
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.out.println("Please give port number as integer.");
            System.exit(-1);
        }
        
        init(myPort);

        /** Main loop. Listen for incoming connections and spawn a new
         * thread for handling them */
        Socket client = null;
        
        while (true) {
            try {
            client = socket.accept();
            handle(client);
            } catch (IOException e) {
            System.out.println("Error reading request from client: " + e);
            /* Definitely cannot continue processing this request,
            * so skip to next iteration of while loop. */
            continue;
            }
        }

    }
}