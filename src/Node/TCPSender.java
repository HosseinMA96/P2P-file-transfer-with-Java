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
    private String fileName;
    private BufferedOutputStream bos;


    public TCPSender(Socket s) {
        socket = s;
    }

    @Override
    public void run() {
        try {
            Node.servingCount++;
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(this.input));
            bos = new BufferedOutputStream(this.output);
            dos = new DataOutputStream(bos);

            fileName = this.br.readLine();
            sendFile(fileName);
            Node.servingCount--;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String fileName) throws Exception {
        File[] f = Node.nestFile.listFiles();
        boolean found = false;
        File ans = null;

        for (int i = 0; i < f.length; i++)
            if (f[i].getName().equals(fileName)) {
                found = true;
                ans = f[i];
                break;
            }


        if (!found) {
            JOptionPane.showMessageDialog(null, "Error, a file which was intended to be sent was deleted!");
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
