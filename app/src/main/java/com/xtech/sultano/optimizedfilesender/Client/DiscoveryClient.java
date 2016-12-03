package com.xtech.sultano.optimizedfilesender.Client;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;

public class DiscoveryClient{
    public static final String DISCOVERY_REQUEST_PREFIX = "DISCOVERY_REQUEST";
    public static final String DISCOVERY_RESPONSE_PREFIX = "DISCOVERY_RESPONSE";
    public static final String BRDCAST_ADDR = "255.255.255.255";
    public static final int BRDCAST_PORT = 8888;
    private String clientName;
    private String mServerName;

    public DiscoveryClient(String serverName){
        this.mServerName = serverName;
        this.clientName = "testClient1";
    }

    public void broadcastDiscoveryRequest(DatagramSocket c, byte[] sendData) throws IOException{
        c.setBroadcast(true);

        try { //Try with 255.255.255.255 first
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(BRDCAST_ADDR), BRDCAST_PORT);
            c.send(sendPacket);
            Log.d("LOG19", getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
        } catch (Exception e) {}

        // Broadcast the message over all the network interfaces
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue; // Don't want to broadcast to the loopback interface
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();

                if (broadcast == null) {
                    continue;
                }

                // Send the broadcast package
                try {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, BRDCAST_PORT);
                    c.send(sendPacket);
                } catch (Exception e) {
                }

                Log.d("LOG19", getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
            }
        }
        Log.d("LOG19", getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
    }

    public byte[] makeDiscoveryRequest(){
        String request = DISCOVERY_REQUEST_PREFIX + this.clientName;
        return request.getBytes();
    }

    public DatagramPacket waitForDiscoveryResponse(DatagramSocket c) throws IOException{
        //Wait for a response
        byte[] recvBuf = new byte[15000];

        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
        c.receive(receivePacket);

        //We have a response
        Log.d("LOG19", getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
        return receivePacket;
    }

    public HashMap<String, InetAddress> findServer(){
        Log.d("LOG19", "findServer");
        HashMap<String, InetAddress> discoveryResult = new HashMap<>();
        try { // Find the server using UDP broadcast
            DatagramSocket c = new DatagramSocket(); //Open a random port to send the package

            byte[] sendData = this.makeDiscoveryRequest();
            this.broadcastDiscoveryRequest(c, sendData);

            int found = 0;
            while(true){
                DatagramPacket discoveryResponse = this.waitForDiscoveryResponse(c);

                //Check if the message is correct
                String message = new String(discoveryResponse.getData()).trim();

                if (message.startsWith(DISCOVERY_RESPONSE_PREFIX)) {
                    //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)

                    InetAddress serverAddr = discoveryResponse.getAddress();
                    Log.d("LOG19", "findServer --> serverAddr : " + serverAddr.toString());
                    String serverName = message.substring(DISCOVERY_RESPONSE_PREFIX.length(), message.length());
                    Log.d("LOG19", "findServer --> serverName: " + serverName);
                    if(serverName.equals(mServerName)){
                        continue;
                    }
                    found++;
                    discoveryResult.put(serverName, serverAddr);

                    if(found == 1){
                        break;
                    }
                }
            }

            //Close the port!
            c.close();
        } catch (IOException ex) {
            //Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return discoveryResult;
    }
}
