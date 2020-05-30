package Node;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Node {
    private int port;
    private String ip,name;
    private ArrayList<Node>cluster;

    public Node( String ip, int port,String name) {
        this.port = port;
        this.ip = ip;
        this.cluster = new ArrayList<>();
        this.name=name;

        Random random = new Random();
        int randomInteger = random.nextInt();

        port=randomInteger%65536;
    }

    void sendDiscovery () throws Exception
    {
        for (int i=0;i<cluster.size();i++)
            sendSignal(cluster.get(i).getIp(),cluster.get(i).getPort());
    }


    void sendSignal(String ip,int port) throws Exception
    {
       // byte[] message=(ip+"@"+port).getBytes();
        byte[] message=("HELLO UDP").getBytes();
        InetAddress address= InetAddress.getByName(ip);
        //http://www.java2s.com/Code/Java/Network-Protocol/SendoutUDPpockets.htm
        DatagramPacket packet = new DatagramPacket(message, message.length,
                address, port);

        DatagramSocket dsocket = new DatagramSocket();
        dsocket.send(packet);
        dsocket.close();
    }


    void receiveSignal()
    {
        try {
            DatagramSocket dsocket = new DatagramSocket(port);
            //http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            dsocket.receive(packet);

            String msg = new String(buffer, 0, packet.getLength());
            System.out.println(packet.getAddress().getHostName() + ": " + msg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }


    public void comm()
    {
        while(true)
        {
            try {
                sendSignal("127.0.0.1", 30000);
                System.out.println("I SENT");
                break;
                }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) {
        Node node=new Node("127.0.0.1",30000,"N1");
        node.comm();
    }
}
