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
    public int udpPort, tcpPort;
    public static long requestWaitPeriod = 2000, requestTime = -1, trickTimeMillisec = 100;
    public int discoveryIntervalMillisec = 15000;
    public String ip, name, lastFileRequested;
    private String nestPath = "C:\\Users\\erfan\\Desktop\\BASE1";
    public Vector<Node> cluster = new Vector<Node>();
    public File nestFile = new File(nestPath);
    public static int servingLimit = 5;
    public static boolean responded = false;
    public static Vector<String> unfinishedFiles = new Vector<>();
    private Timer timer;


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
        this.name = name;
        tcpPort = createRandomTcpPort();


    }


    /**
     * Second constructor for a node, with a cluster vector
     *
     * @param ip
     * @param udpPort
     * @param name
     * @param n
     */
    public Node(String ip, int udpPort, String name, Vector<Node> n) {
        this.udpPort = udpPort;
        this.ip = ip;
        this.cluster = n;
        //   System.out.println("YIPI "+cluster.get(0).getName());
        //   System.out.println("YIPI "+cluster.get(1).getName());
        // System.out.println("\n\n");


        this.name = name;
        tcpPort = createRandomTcpPort();
        // new Timer(delayInMiliSeconds, syncPerformer)).start();

    }

    /**
     * Loop function, which serves the user by getting command input string
     */
    private void loop() {
        System.out.println("MY TCP PORT IS " + tcpPort);
        DiscoverySender ds = new DiscoverySender(this, discoveryIntervalMillisec);
        ds.start();

        UDPBroadcast udpBroadcast = new UDPBroadcast(this);
        udpBroadcast.start();

        TCPBroadcast tcpBroadcast = new TCPBroadcast(this);
        tcpBroadcast.start();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();
            //  System.out.println(inputString);
            processString(inputString);


        }
    }


    /**
     * A Method to process user command string
     *
     * @param s
     */
    private void processString(String s) {


        if (s.equals("LIST") || s.equals("list")) {
            list();
            // System.out.println("LIST");
            return;
        }


        if (s.length() >= 3 && (s.substring(0, 3).equals("GET"))) {
            //  System.out.println("THIS");
            if (searchIfIhaveTheFile(s.substring(3)))
                System.out.println("You already have this file on your nest path! request was not sent\n\n");

            else
                get(s);

            return;

        }


        System.out.println("Invalid command!\n\n");

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

    /**
     * Returns a free port number on localhost.
     * <p>
     * Heavily inspired from org.eclipse.jdt.launching.SocketUtil (to avoid a dependency to JDT just because of this).
     * Slightly improved with close() missing in JDT. And throws exception instead of returning -1.
     *
     * @return a free port number on localhost
     * @throws IllegalStateException if unable to find a free port
     */
    private int createRandomTcpPort() {

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
        System.out.println("This is node " + this.getName() + " with ip address " + ip + " udp port " + udpPort);

        System.out.println("And its cluster is : ");

        for (int i = 0; i < cluster.size(); i++)
            System.out.println("Node " + cluster.get(i).getName() + " ip address " + cluster.get(i).getIp() + " udp port " + cluster.get(i).getUDPPort());

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

    //MSG = GETfileName/IP#udpPort
    private void get(String msg) {
        responded = true;
        requestTime = System.currentTimeMillis();
        lastFileRequested = msg.substring(3);
        String added = msg;
        added = added + "/" + ip + "#" + udpPort + "%" + name;
        GetSender gs = new GetSender(added, this);
        gs.start();
        TCPReceiver tcpReceiver = new TCPReceiver(msg.substring(3), this, requestTime);
        tcpReceiver.start();

        try {
            tcpReceiver.join();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Getter for udpPort
     *
     * @return
     */
    public int getUDPPort() {
        return udpPort;
    }


    public String getIp() {
        return ip;
    }


    public static void main(String[] args) {
        Vector<Node> n = new Vector<Node>();
        n.add(new Node("127.0.0.1", 62000, "N2"));
        n.add(new Node("127.0.0.1", 63000, "N3"));
        ;

        Node node = new Node("127.0.0.1", 30000, "N1", n);
        node.loop();

    }
}

