package Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class TCPReceiver extends Thread{
    private Socket socket;
    private String destinationIP, requestedFileName;
    private int destinationPort;
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



    public TCPReceiver(String ip, int port, String fName, Node n, Vector<Respond> r)
    {
        destinationIP=ip;
        destinationPort=port;
        requestedFileName=fName;
        node=n;
    }

    @Override
    public void run()
    {
        getFile();
    }

    void getFile() {
        try {
            socket = new Socket(destinationIP, destinationPort);
            output = socket.getOutputStream();
            input = receiveSocket.getInputStream();
            bp = new PrintWriter(new OutputStreamWriter(this.output));
            identify();
            requestFile(requestedFileName);
            receiveSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void identify() {
        bp.println(requestedFileName);
        bp.flush();
    }

    private void requestFile(String fileName) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(this.input);
        DataInputStream dis = new DataInputStream(bis);


        File file = new File(node.nestFile.getAbsolutePath() + "\\" + fileName);


        long fileLength = dis.readLong();


        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        for (int j = 0; (long) j < fileLength; ++j) {
            bos.write(bis.read());
        }

        bos.close();


        dis.close();
    }



}
