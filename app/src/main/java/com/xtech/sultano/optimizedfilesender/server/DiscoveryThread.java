package com.xtech.sultano.optimizedfilesender.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscoveryThread implements Runnable {
    public static final String DISCOVERY_REQUEST_PREFIX = "DISCOVERY_REQUEST";
    public static final String DISCOVERY_RESPONSE_PREFIX = "DISCOVERY_RESPONSE";
    private String serverName;

    public byte[] makeDiscoveryResponse(){
        String response = DISCOVERY_RESPONSE_PREFIX + serverName;
        return response.getBytes();
    }

    public void sendResponse(DatagramSocket socket, byte[] sendData, DatagramPacket packet) throws IOException{
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
        socket.send(sendPacket);
        System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
    }

    public DiscoveryThread(){
        this.serverName = "ServerTest1";
    }

    public DatagramPacket receiveMessage(DatagramSocket socket) throws IOException{
        System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");
        //Receive a packet
        byte[] recvBuf = new byte[15000];
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        socket.receive(packet);
        //Packet received
        System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
        System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));
        return packet;
    }

    @Override
    public void run() {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            DatagramSocket socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                DatagramPacket packet = this.receiveMessage(socket);
                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.startsWith(DISCOVERY_REQUEST_PREFIX)) {
                    System.out.println("Client name:");
                    System.out.println(message.substring(DISCOVERY_REQUEST_PREFIX.length(), message.length()));

                    byte[] sendData = this.makeDiscoveryResponse();
                    this.sendResponse(socket, sendData, packet);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args){
        Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
        discoveryThread.start();
    }

    public static DiscoveryThread getInstance() {
        return DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {
        private static final DiscoveryThread INSTANCE = new DiscoveryThread();
    }
}