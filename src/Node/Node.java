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
    private String ip, name;
    private ArrayList<Node> cluster;

    public Node(String ip, int port, String name) {
        this.port = port;
        this.ip = ip;
        this.cluster = new ArrayList<>();
        this.name = name;

        Random random = new Random();
        int randomInteger = random.nextInt();

        port = randomInteger % 65536;
    }

    void sendDiscovery() throws Exception {
        String msg = createMessage();

        for (int i = 0; i < cluster.size(); i++)
            sendSignal(cluster.get(i).getIp(), cluster.get(i).getPort(), msg);
    }

    String createMessage() {
        String ans = "";
        for (int i = 0; i < cluster.size(); i++)
            ans = ans + cluster.get(i).getName() + "|" + cluster.get(i).getIp() + "p" + cluster.get(i).getPort() + "\n";

        return ans;

    }

    public String getName() {
        return name;
    }

    void sendSignal(String ip, int port, String msg) throws Exception {
        // byte[] message=(ip+"@"+port).getBytes();
        byte[] message = msg.getBytes();
        InetAddress address = InetAddress.getByName(ip);
        //http://www.java2s.com/Code/Java/Network-Protocol/SendoutUDPpockets.htm
        DatagramPacket packet = new DatagramPacket(message, message.length,
                address, port);

        DatagramSocket dsocket = new DatagramSocket();
        dsocket.send(packet);
        dsocket.close();
    }


    void receiveSignal() {
        try {
            DatagramSocket dsocket = new DatagramSocket(port);
            //http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            dsocket.receive(packet);

            String msg = new String(buffer, 0, packet.getLength());
            System.out.println(packet.getAddress().getHostName() + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }


    void update(String msg) {
        // ans=ans+cluster.get(i).getName()+"|"+cluster.get(i).getIp()+"p"+cluster.get(i).getPort()+"\n";asdasdasdasd;
        int temp = 0, temp2 = 0;

        for (int i = 0; i < msg.length(); i++)
            if (msg.charAt(i) == '|') {
                temp = i;
                break;
            }

        String nodeName = msg.substring(0, temp);

        for (int i = temp + 1; i < msg.length(); i++)
            if (msg.charAt(i) == 'p') {
                temp2 = i;
                break;
            }

        String nodeIp = msg.substring(temp + 1, temp2);

        for (int i = temp2 + 1; i < msg.length(); i++)
            if (msg.charAt(i) == '\n') {
                temp = i;
                break;
            }

        int nodePort = Integer.parseInt(msg.substring(temp2 + 1, temp));

        String m = msg.substring(temp + 1);

        boolean currentlyExists=false;

        for (int i=0;i<cluster.size();i++)
            if(cluster.get(i).getName().equals(nodeName))
            {
                currentlyExists=true;
                break;
            }


        if(!currentlyExists)
            cluster.add(new Node(nodeIp,nodePort,nodeName));


        if(m.length()>2)
             update(m);

    }

    public String getIp() {
        return ip;
    }


    public void comm() {
        while (true) {
            try {
                sendSignal("127.0.0.1", 30000, "PIOP");
                System.out.println("I SENT");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Node node = new Node("127.0.0.1", 30000, "N1");
        node.comm();
    }
}
