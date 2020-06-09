package Node;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;


public class UDPBroadcast extends Thread {
    private int requesterPort;
    private InetAddress requesterIP;
    public static Vector<Respond> responders = new Vector<>();
    private Vector<Node> nodesAlreadyGotFileFrom;
    private Node node;

    public UDPBroadcast(Node n) {
        node = n;
        nodesAlreadyGotFileFrom = new Vector<Node>();
    }

    @Override
    public void run() {
        while (true) {
            String s = receiveUdpSignal();
            //     System.out.println("PAST IN RECEIVE, HERE IS THE MESSAGE : ");
            //    System.out.println(s);
            //   System.out.println("\n\n");

            multiplexUDPpacket(s);
            //     System.out.println("PAST IN RECEIVE");
            try {
                //      sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //   public void gun()
    //  {
    //     System.out.println("GUN");
    //  }

    /**
     * Receive a UDP packet. This can be either Get command or discovery message
     */
    private String receiveUdpSignal() {
        try {
            // System.out.println("THE UDP PORT IS : "+node.udpPort);
            DatagramSocket dsocket = new DatagramSocket(node.udpPort);
            //http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            dsocket.receive(packet);
            requesterIP = packet.getAddress();
            requesterPort = packet.getPort();


            String msg = new String(buffer, 0, packet.getLength());
            dsocket.close();
            return msg;


            // System.out.println(packet.getAddress().getHostName() + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * Here we guide a received string depending on being a discovery message or GET message?
     *
     * @param msg
     */
    void multiplexUDPpacket(String msg) {
        if (msg.length() >= 3) {
            String temp = msg.substring(0, 3);

            if (temp.equals("DIS"))
                updateCluster(msg.substring(3));

            //GETMYNAME/FILENAME
            if (temp.equals("GET"))
                handleFileRequest(msg.substring(3));

            //TCPip/port
            if (temp.equals("TCP"))
                registerResponder(msg);


        }
    }

    private void registerResponder(String responder) {
        String ip = "", fName = "";
        int port = 0, temp = 0;

        for (int i = 0; i < responder.length(); i++)
            if (responder.charAt(i) == '/') {
                ip = responder.substring(3, i);
                temp = i;
                break;
            }


        for (int i = temp + 1; i < responder.length(); i++)
            if (responder.charAt(i) == '/') {
                port = Integer.parseInt(responder.substring(temp + 1, i));
                fName = responder.substring(i + 1);
                temp = i;
                break;
            }

        System.out.println("REGISTERED A RESPONDER " + ip + " " + port);

        if (fName.equals(node.lastFileRequested) && System.currentTimeMillis()-node.requestTime<=node.requestWaitPeriod)
            responders.add(new Respond(new Node(ip, port, "."), System.currentTimeMillis() - node.requestTime));

    }

    private void handleFileRequest(String msg) {
        //MSG = fileName/IPpudpPort
        System.out.println("RECEIVED GET REQUEST :: " + msg);
        String requesterIp = "";
        int requesterPort = 0, temp = 0;

        //First find the name of sender
        String requester = "", fileName = "";

        for (int i = 0; i < msg.length(); i++)
            if (msg.charAt(i) == '/') {

                fileName = msg.substring(0, i);
                temp = i;
                break;
            }

        for (int i = 0; i < msg.length(); i++)
            if (msg.charAt(i) == '#') {

                requesterIp = msg.substring(temp + 1, i);
                requesterPort = Integer.parseInt(msg.substring(i + 1));

                break;
            }
        System.out.println("FNAME : " + fileName + " IP " + requesterIp + " port " + requesterPort);

        File[] f = node.nestFile.listFiles();
        boolean found = false;
        File answer;

        for (int i = 0; i < f.length; i++)
            if (f[i].getName().equals(fileName)) {
                found = true;
                answer = f[i];
                break;
            }

        if (found) {
            boolean receivedFileBefore = false;

            for (int i = 0; i < nodesAlreadyGotFileFrom.size(); i++)
                if (nodesAlreadyGotFileFrom.get(i).getName().equals(requester)) {
                    receivedFileBefore = true;
                    break;
                }


            if (receivedFileBefore == false)
                trick();

            Node.sendUDPSignal(requesterIP.getHostAddress(), requesterPort, "TCP" + node.ip + "/" + node.tcpPort + "/" + fileName);
        }

        if (found)
            System.out.println("I HAVE THE FILE WITH NAME " + fileName);
    }

    /**
     * Piggyback
     */
    private void trick() {

    }


    /**
     * Update our cluster with regard to discovery message
     *
     * @param msg
     */
    public void updateCluster(String msg) {
        // ans=ans+cluster.get(i).getName()+"|"+cluster.get(i).getIp()+"p"+cluster.get(i).getUDPPort()+"\n";asdasdasdasd;
        int temp = 0, temp2 = 0;
        //    System.out.println("In update cluster, msg = "+msg);

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

        boolean currentlyExists = false;


        for (int i = 0; i < node.cluster.size(); i++)
            if (node.cluster.get(i).getName().equals(nodeName) || node.name.equals(nodeName)) {
                currentlyExists = true;
                break;
            }


        if (!currentlyExists)
            node.cluster.add(new Node(nodeIp, nodePort, nodeName));


        if (m.length() > 2)
            updateCluster(m);

    }
}
