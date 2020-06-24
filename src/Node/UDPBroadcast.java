/**
 * A class to handle udp packets received
 */

package Node;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;


public class UDPBroadcast extends Thread {
    private int requesterPort;
    private InetAddress requesterIP;
    public static Vector<Respond> responders = new Vector<>();
    public static Vector<Node> nodesAlreadyGotFileFrom = new Vector<Node>();
    private Node node;

    /**
     * Constructor of this class
     *
     * @param n
     */
    public UDPBroadcast(Node n) {
        node = n;
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
            if (temp.equals("TCP") && Node.responded)
                registerResponder(msg);


        }
    }

    /**
     * A Method to register a responder with ideal characteristics, less than requestWaitPeriod delay and with the last file requested name
     *
     * @param responder
     */
    private void registerResponder(String responder) {
        //System.out.println("RESP : "+responder);
        String ip = "", fName = "";
        int port = 0, temp = 0, ir = 0;

        for (int i = 0; i < responder.length(); i++)
            if (responder.charAt(i) == '%') {
                ir = i;
                break;
            }


        for (int i = 0; i < responder.length(); i++)
            if (responder.charAt(i) == '/') {
                ip = responder.substring(3, i);
                temp = i;
                break;
            }


        for (int i = temp + 1; i < responder.length(); i++)
            if (responder.charAt(i) == '/') {
                port = Integer.parseInt(responder.substring(temp + 1, i));
                fName = responder.substring(i + 1, ir);
                temp = i;
                break;
            }

        String name = "";

        for (int i = temp + 1; i < responder.length(); i++)
            if (responder.charAt(i) == '%') {
                name = responder.substring(i + 1);
                break;
            }


        System.out.println("REGISTERED A RESPONDER " + ip + " " + port);
        System.out.println(fName);
        System.out.println(System.currentTimeMillis() - node.requestTime);
        System.out.println(node.requestWaitPeriod);
        System.out.println();

        //If the request was within
        if (fName.equals(node.lastFileRequested) && System.currentTimeMillis() - node.requestTime <= node.requestWaitPeriod)
            responders.add(new Respond(new Node(ip, port, name), System.currentTimeMillis() - node.requestTime));

    }

    /**
     * A Method to handle request of files sent by other nodes to thsi node. Checks if the file exists and check whether this node should make a fake delay or not
     *
     * @param msg
     */
    private void handleFileRequest(String msg) {
        //MSG = fileName/IP#pudpPort%Name
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

        int ir = 0;

        for (int i = 0; i < msg.length(); i++)
            if (msg.charAt(i) == '%') {
                ir = i;
                requester = msg.substring(i + 1);

                break;
            }

        for (int i = 0; i < msg.length(); i++)
            if (msg.charAt(i) == '#') {

                requesterIp = msg.substring(temp + 1, i);
                requesterPort = Integer.parseInt(msg.substring(i + 1, ir));

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

        for (int i = 0; i < Node.unfinishedFiles.size(); i++)
            if (Node.unfinishedFiles.get(i).equals(fileName)) {
                found = false;
                break;
            }

        System.out.println(requester);
        if (found) {
            boolean receivedFileBefore = false;

            System.out.println("CONTROLL");
            System.out.println(requester);

            if (nodesAlreadyGotFileFrom.size() > 0) {

                System.out.println(nodesAlreadyGotFileFrom.size());
                System.out.println(nodesAlreadyGotFileFrom.get(0).name);
                System.out.println(nodesAlreadyGotFileFrom.get(0).getIp());
                System.out.println(nodesAlreadyGotFileFrom.get(0).udpPort);


            } else
                System.out.println(0);


            System.out.println();

            synchronized (this) {
                for (int i = 0; i < nodesAlreadyGotFileFrom.size(); i++)
                    if (nodesAlreadyGotFileFrom.get(i).name.equals(requester)) {
                        receivedFileBefore = true;
                        break;
                    }


                if (receivedFileBefore == false)
                    trick();

                Node.sendUDPSignal(requesterIP.getHostAddress(), requesterPort, "TCP" + node.ip + "/" + node.tcpPort + "/" + fileName + "%" + node.name);
            }

            if (found)
                System.out.println("I HAVE THE FILE WITH NAME " + fileName);
        }
    }

    /**
     * A Method to trick the requester
     */
    private void trick() {
        try {
            sleep(Node.trickTimeMillisec);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
