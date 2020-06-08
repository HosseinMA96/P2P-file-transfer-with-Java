package Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static Node.Node.cluster;

public class DiscoveryReceiver extends Thread {
    public DiscoveryReceiver() {

    }

   @Override
   public void run()
   {

   }

//    void receiveSignal() {
//        try {
//            DatagramSocket dsocket = new DatagramSocket(Node.udpPort);
//            //http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
//            byte[] buffer = new byte[2048];
//            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//            dsocket.receive(packet);
//
//            String msg = new String(buffer, 0, packet.getLength());
//            System.out.println(packet.getAddress().getHostName() + ": " + msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    public static void update(String msg) {
//        // ans=ans+cluster.get(i).getName()+"|"+cluster.get(i).getIp()+"p"+cluster.get(i).getUDPPort()+"\n";asdasdasdasd;
//        int temp = 0, temp2 = 0;
//
//        for (int i = 0; i < msg.length(); i++)
//            if (msg.charAt(i) == '|') {
//                temp = i;
//                break;
//            }
//
//        String nodeName = msg.substring(0, temp);
//
//        for (int i = temp + 1; i < msg.length(); i++)
//            if (msg.charAt(i) == 'p') {
//                temp2 = i;
//                break;
//            }
//
//        String nodeIp = msg.substring(temp + 1, temp2);
//
//        for (int i = temp2 + 1; i < msg.length(); i++)
//            if (msg.charAt(i) == '\n') {
//                temp = i;
//                break;
//            }
//
//        int nodePort = Integer.parseInt(msg.substring(temp2 + 1, temp));
//
//        String m = msg.substring(temp + 1);
//
//        boolean currentlyExists=false;
//
//        for (int i=0;i<cluster.size();i++)
//            if(cluster.get(i).getName().equals(nodeName))
//            {
//                currentlyExists=true;
//                break;
//            }
//
//
//        if(!currentlyExists)
//            cluster.add(new Node(nodeIp,nodePort,nodeName));
//
//
//        if(m.length()>2)
//            update(m);
//
//    }


}
