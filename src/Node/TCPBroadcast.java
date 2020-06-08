package Node;

import javax.swing.*;
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
    public static String destinationIP, requestedFileName;
    public static int destinationPort;

    public TCPBroadcast() {
        try {
            commonSocket = new ServerSocket(Node.tcpPort);
        //    identify();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socketReader1 = commonSocket.accept();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
//
//    void getFile() {
//        try {
//            this.receiveSocket = new Socket(destinationIP, destinationPort);
//            output = receiveSocket.getOutputStream();
//            input = receiveSocket.getInputStream();
//            bp = new PrintWriter(new OutputStreamWriter(this.output));
//            identify();
//            requestFile(requestedFileName);
//            receiveSocket.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    private void identify() {
//        bp.println(requestedFileName);
//        bp.flush();
//    }
//
//    private void requestFile(String fileName) throws Exception {
//        BufferedInputStream bis = new BufferedInputStream(this.input);
//        DataInputStream dis = new DataInputStream(bis);
//
//
//        File file = new File(Node.nestFile.getAbsolutePath() + "\\" + fileName);
//
//
//        long fileLength = dis.readLong();
//
//
//        FileOutputStream fos = new FileOutputStream(file);
//        BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//        for (int j = 0; (long) j < fileLength; ++j) {
//            bos.write(bis.read());
//        }
//
//        bos.close();
//
//
//        dis.close();
//    }


//    public void sendFile(String fileName) throws Exception {
//        File[] f = Node.nestFile.listFiles();
//        boolean found = false;
//        File ans = null;
//
//        for (int i = 0; i < f.length; i++)
//            if (f[i].getName().equals(fileName)) {
//                found = true;
//                ans = f[i];
//                break;
//            }
//
//
//        if (!found) {
//            JOptionPane.showMessageDialog(null, "Error, a file which was intended to be sent was deleted!");
//            return;
//        }
//
//        BufferedOutputStream bos = new BufferedOutputStream(this.output);
//
//        long length = ans.length();
//        dos.writeLong(length);
//
//        FileInputStream fis = new FileInputStream(ans);
//        BufferedInputStream bis = new BufferedInputStream(fis);
//
//
//        int theByte;
//        while ((theByte = bis.read()) != -1) {
//            bos.write(theByte);
//        }
//
//        bis.close();
//        dos.close();
//
//    }

//
//    public static void setDestination(String ip, int port, String fName) {
//        destinationIP = ip;
//        destinationPort = port;
//        requestedFileName = fName;
//    }
}
