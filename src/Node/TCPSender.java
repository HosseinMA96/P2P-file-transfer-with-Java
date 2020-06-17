package Node;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class TCPSender extends Thread {
    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private DataOutputStream dos;
    private BufferedReader br;
    private String fileName, hostName;
    private PrintWriter bp;
    private BufferedOutputStream bos;
    private Node node;


    public TCPSender(Socket s, Node n) {
        socket = s;
        node = n;
    }

    @Override
    public void run() {
        try {

            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(this.input));
            bos = new BufferedOutputStream(this.output);
            dos = new DataOutputStream(bos);
            bp = new PrintWriter(new OutputStreamWriter(this.output));

            fileName = this.br.readLine();
            bp.println(node.getName());
            bp.flush();

            sendFile(fileName);

            bp.close();
            br.close();
            input.close();
            output.close();
            socket.close();
            socket.close();
            TCPBroadcast.servingClients--;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String fileName) throws Exception {
        File[] f = node.nestFile.listFiles();
        boolean found = false;
        File ans = null;

        for (int i = 0; i < f.length; i++)
            if (f[i].getName().equals(fileName)) {
                found = true;
                ans = f[i];
                break;
            }


        if (!found) {
            System.out.println("Error, a file which was intended to be sent was deleted!");
            return;
        }

        // BufferedOutputStream bos = new BufferedOutputStream(this.output);

        long length = ans.length();
        dos.writeLong(length);

        FileInputStream fis = new FileInputStream(ans);
        BufferedInputStream bis = new BufferedInputStream(fis);


        int theByte;
        while ((theByte = bis.read()) != -1) {
            bos.write(theByte);
        }

        bis.close();
        dos.close();

    }

}
