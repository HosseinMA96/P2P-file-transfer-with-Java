/**
 * A Method to handle TCP connections as a server, that is, accepts connections and initializes TCPsender
 */

package Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPBroadcast extends Thread {
    private OutputStream output;
    private DataOutputStream dos;
    private ServerSocket commonSocket;
    private Socket receiveSocket;
    private String command;
    private InputStream input;
    private BufferedReader br;
    private PrintWriter bp;
    private DataInputStream dis;
    private Node node;
    public static int servingClients = 0;

    /**
     * Constructor of this class
     *
     * @param n
     */
    public TCPBroadcast(Node n) {
        try {
            node = n;
            commonSocket = new ServerSocket(node.tcpPort);
            //    identify();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                //TIME STUFF
                Socket receiveSocket = commonSocket.accept();

                if (servingClients < Node.servingLimit) {
                    TCPSender tcpSender = new TCPSender(receiveSocket, node);
                    tcpSender.start();
                    servingClients++;
                }

                else
                {
                    input = receiveSocket.getInputStream();
                    br = new BufferedReader(new InputStreamReader(this.input));

                    this.output = receiveSocket.getOutputStream();
                    bp = new PrintWriter(new OutputStreamWriter(this.output));

                    String dummy=br.readLine();

                    bp.println("BUSY");
                    bp.flush();

                    bp.close();
                    output.close();
                    receiveSocket.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
