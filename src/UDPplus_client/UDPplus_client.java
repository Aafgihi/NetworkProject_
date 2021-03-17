package UDPplus_client;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.lang.*;
import java.util.*;

class UDPplus_client 
{

    public static void main(String args[]) throws Exception 
    {

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Random rand = new Random();
        
        //To make the port number randomly.
        int x = rand.nextInt(65535) + 1024;
        DatagramSocket clientSocket = new DatagramSocket(x);
        
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        //To send handshake and receive
        byte[] handshaking = new byte[1024];
        byte[] ReceiveHandshaking = new byte[1024];
        //The definition of a variable if it is received from server -1 means its busy state.
        String[] Max = {" -1 "};
        //Define a variable that the user sends to "end" to stop working.
        String[] end = {"end"};
        //Define a variable time because time out
        int timeOut = 30;

        //A word "Test" is sent when shaking hands directly .
        String Test = "Test";
        handshaking = Test.getBytes();

        //Here send a handshake to the server to see the status.
        DatagramPacket handshak = new DatagramPacket(handshaking, handshaking.length, IPAddress, 9152);
        clientSocket.send(handshak);

        //Here the reception of the server status during the handshake.
        DatagramPacket ReceiveHandshak = new DatagramPacket(ReceiveHandshaking, ReceiveHandshaking.length);
        clientSocket.receive(ReceiveHandshak);

        //Here the print server status
        String ServerReplay = new String(ReceiveHandshak.getData());
        System.out.println("Server status:" + ServerReplay);

        //Here it will print that the server status is busy because the number received by -1 "Max".
        if (ServerReplay.contains(Max[0])) 
        {
            System.out.println("Sorry, Server is Busy.");

        } 
        else //If the server status is available go here to receive the name from the user.
        {

            while (true) 
            {
                System.out.print("Enter Full Name or “end” to EXIT: ");
                String sentence = inFromUser.readLine();

                //If the user enters the word "end" stands the action.
                if (sentence.contains(end[0])) 
                {
                    break;
                }
                //Encryption Full Name
                // The name the user entered into a byte with the variable "strAsByteArray".
                byte[] strAsByteArray = sentence.getBytes();
                //We defined a store named "result" with the size of the input from the user.
                byte[] result = new byte[strAsByteArray.length];

                //Here we reverse the last byte to the first
                for (int m = 0; m < strAsByteArray.length; m++) 
                {
                    result[m] = strAsByteArray[strAsByteArray.length - m - 1];
                }

                //We have added the inverse value of the "result" variable to which your income is used "sentence".
                sentence = new String(result);
                sendData = sentence.getBytes();

                while (true) 
                {
                    try 
                    {
                        //This function is performed if the "30" time direct out of "Try" to "Catch".
                        clientSocket.setSoTimeout(timeOut);

                        //Send Packet
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9152);
                        clientSocket.send(sendPacket);

                        //Receive Packet
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        //Create a variable(modifiedSentence) and put in what you receive from the server
                        String modifiedSentence = new String(receivePacket.getData());

                        //Here, decryption the account number in the client.
                        //Here convert the number that came into bytes and put it in "strAsByteArray"
                        strAsByteArray = modifiedSentence.getBytes();
                        //We defined a store named "result" with the size of "strAsByteArray".
                        result = new byte[strAsByteArray.length];
                        //Here we reverse the last byte to the first
                        for (int m = 0; m < strAsByteArray.length; m++) 
                        {
                            result[m] = strAsByteArray[strAsByteArray.length - m - 1];
                        }

                        //Here the reverse number is added to "modifiedSentence".
                        modifiedSentence = new String(result);

                        //Print AcountNumber to user.
                        System.out.println("FROM SERVER:" + modifiedSentence);

                        break;
                    } //Here, Time out either during sending or receiving.
                    catch (SocketTimeoutException e) 
                    {
                        System.out.println("Time out reached:" + (timeOut) + " [ " + e + " ] ");
                        timeOut = timeOut + 30;
                    }

                }
            }

            clientSocket.close();

        }
    }
}
