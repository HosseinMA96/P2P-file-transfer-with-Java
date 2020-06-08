package Node;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

import static Node.Node.cluster;
import static Node.Node.nodesAlreadyGotFileFrom;

public class UDPBroadcast extends Thread{
    private int requesterPort;
    private InetAddress requesterIP;
    public static Vector<Respond>responders=new Vector<>();
    @Override
    public void run()
    {
        while(true)
        {
            String s=receiveUdpSignal();
            multiplexUDPpacket(s);
        }
    }

    /**
     * Receive a UDP packet. This can be either Get command or discovery message
     */
    private String receiveUdpSignal() {
        try {
            DatagramSocket dsocket = new DatagramSocket(Node.udpPort);
            //http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            dsocket.receive(packet);
            requesterIP=packet.getAddress();
            requesterPort=packet.getPort();



            String msg = new String(buffer, 0, packet.getLength());
            return msg;


           // System.out.println(packet.getAddress().getHostName() + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * Here we guide a received string depending on being a discovery message or GET message?
     * @param msg
     */
    void multiplexUDPpacket(String msg)
    {
        if(msg.length()>=3)
        {
            String temp=msg.substring(0,3);

            if(temp.equals("DIS"))
            updateCluster(msg.substring(3));

            //GETMYNAME/FILENAME
            if(temp.equals("GET"))
                handleFileRequest(msg.substring(3));

            //TCPip/port
            if(temp.equals("TCP"))
                registerResponder(msg);


        }
    }
    private void registerResponder(String responder)
    {
        String ip="";
        int port=0;

        for (int i=0;i<responder.length();i++)
            if(responder.charAt(i)=='/')
            {
                ip=responder.substring(3,i);
                Integer.parseInt(responder.substring(i+1));
                break;
            }

        responders.add(new Respond(new Node(ip,port,"."),System.currentTimeMillis()-Node.requestTime));

    }
    private void handleFileRequest(String msg)
    {
        //First find the name of sender
        String requester="",fileName="";

        for (int i=0;i<msg.length();i++)
            if(msg.charAt(i)=='/')
            {
                requester=msg.substring(0,i);
                fileName=msg.substring(i+1);
                break;
            }

        File []f=Node.nestFile.listFiles();
        boolean found=false;
        File answer;

        for (int i=0;i<f.length;i++)
            if(f[i].getName().equals(fileName))
            {
                found=false;
                answer=f[i];
                break;
            }

        if(found) {
            boolean receivedFileBefore=false;

            for(int i=0;i<nodesAlreadyGotFileFrom.size();i++)
                if(nodesAlreadyGotFileFrom.get(i).getName().equals(requester))
                {
                    receivedFileBefore=true;
                    break;
                }


            if(receivedFileBefore==false)
                trick();

            Node.sendUDPSignal(requesterIP.getHostAddress(),requesterPort,"TCP"+Node.ip+"/"+Node.tcpPort);
        }


    }

    /**
     * Piggyback
     */
    private void trick()
    {

    }


    /**
     * Update our cluster with regard to discovery message
     * @param msg
     */
    public static void updateCluster(String msg) {
        // ans=ans+cluster.get(i).getName()+"|"+cluster.get(i).getIp()+"p"+cluster.get(i).getUDPPort()+"\n";asdasdasdasd;
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
            if(cluster.get(i).getName().equals(nodeName) || cluster.get(i).getName().equals(Node.name))
            {
                currentlyExists=true;
                break;
            }


        if(!currentlyExists)
            cluster.add(new Node(nodeIp,nodePort,nodeName));


        if(m.length()>2)
            updateCluster(m);

    }
}
