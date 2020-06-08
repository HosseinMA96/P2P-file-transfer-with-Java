/**
 * This class represents a node.
 */
package Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.Vector;


public class Node {
    public static int udpPort, tcpPort;
    public static long requestWaitPeriod = 1000, requestTime;
    public static int discoveryIntervalMillisec;
    public static String ip, name;
    public static Vector<Node> cluster, nodesAlreadyGotFileFrom;
    public static File nestFile;
    public static int servingCount = 0;
    private Timer timer;

    private String nestPath = "C:\\Users\\erfan\\Desktop\\BASE";

    /**
     * Constructor for this class
     *
     * @param ip
     * @param udpPort
     * @param name
     */
    public Node(String ip, int udpPort, String name) {
        this.udpPort = udpPort;
        this.ip = ip;
        this.cluster = new Vector<Node>();
        this.nodesAlreadyGotFileFrom = new Vector<Node>();
        this.name = name;
        tcpPort = createRandomTcpPort();
       // new Timer(delayInMiliSeconds, syncPerformer)).start();


        timer=new Timer(discoveryIntervalMillisec, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DiscoverySender ds=new DiscoverySender(cluster);
            }
        });


        loop();
    }



    private void loop() {
        UDPBroadcast udpBroadcast=new UDPBroadcast();
        udpBroadcast.start();

        TCPBroadcast tcpBroadcast=new TCPBroadcast();
        tcpBroadcast.start();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();
            processString(inputString);

            System.out.println(inputString);


        }
    }


    private void processString(String s) {
        if (s.equals("LIST") || s.equals("list"))
            list();


        if (s.substring(0, 3).equals("GET") || s.substring(0, 3).equals("get")) {
            if (searchIfIhaveTheFile(s.substring(3)))
                JOptionPane.showMessageDialog(null, "You already have this file on your nest path! request was not sent");

            else
                get(s.substring(3));

        }

    }

    /**
     * Given string s which is a file name, see if this host has the file so that it won't send request to other nodes
     *
     * @param s
     * @return
     */
    private boolean searchIfIhaveTheFile(String s) {
        File[] f = nestFile.listFiles();

        for (int i = 0; i < f.length; i++)
            if (f[i].getName().equals(s))
                return true;

        return false;
    }

    private int createRandomTcpPort() {
        /**
         * Returns a free port number on localhost.
         *
         * Heavily inspired from org.eclipse.jdt.launching.SocketUtil (to avoid a dependency to JDT just because of this).
         * Slightly improved with close() missing in JDT. And throws exception instead of returning -1.
         *
         * @return a free port number on localhost
         * @throws IllegalStateException if unable to find a free port
         */
        //   private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }


    private void list() {
        System.out.println("This is node " + this.getName() + " with ip addres " + ip + " udp port " + udpPort);

        System.out.println("And its cluster is : ");

        for (int i = 0; i < cluster.size(); i++)
            System.out.println("Node " + cluster.get(i).getName() + " ip addres " + cluster.get(i).getIp() + " udp port " + cluster.get(i).getUDPPort());

        System.out.println("\n");
    }

    public String getName() {
        return name;
    }

    private void generateNestFile() {
        nestFile = new File(nestPath);
        if (!nestFile.isDirectory() || !nestFile.exists())
            JOptionPane.showMessageDialog((Component) null, "Your base directory does not exist!", "Error", 1);
    }

    private void get(String msg) {
        requestTime = System.currentTimeMillis();
        GetSender gs = new GetSender(msg);
        gs.start();
    }

    public void deleteFile(File f) {
        int i;


        if (f.isFile()) {
            try {
                f.delete();
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        if (f.isDirectory()) {
            File[] temp = f.listFiles();

            for (i = 0; i < temp.length; ++i) {
                this.deleteFile(temp[i]);
            }

            try {
                if (!f.delete()) {
                    System.out.println("ERROR IN DELETING");
                }
            } catch (Exception var4) {
            }
        }

    }


    /**
     * Send a string through udp to specific ip and port
     *
     * @param ip
     * @param port
     * @param msg
     * @throws Exception
     */
    public static void sendUDPSignal(String ip, int port, String msg) {
        // byte[] message=(ip+"@"+udpPort).getBytes();
        byte[] message = msg.getBytes();
        try {
            InetAddress address = InetAddress.getByName(ip);
            //http://www.java2s.com/Code/Java/Network-Protocol/SendoutUDPpockets.htm
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, port);

            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(packet);
            dsocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getUDPPort() {
        return udpPort;
    }


    public String getIp() {
        return ip;
    }


    public void comm() {
        while (true) {
            try {
                sendUDPSignal("127.0.0.1", 30000, "PIOP");
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

