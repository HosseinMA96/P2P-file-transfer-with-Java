package Node;

public class GetSender extends Thread {
    private String msg;

    public GetSender(String m)
    {
        msg=m;
    }

    @Override
    public void run()
    {
        for (int i=0;i<Node.cluster.size();i++)
            Node.sendUDPSignal(Node.cluster.get(i).getIp(),Node.cluster.get(i).getUDPPort(),msg);
    }
}
