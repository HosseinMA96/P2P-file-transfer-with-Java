/**
 * A Class to send GET request
 */

package Node;

public class GetSender extends Thread {
    private String msg;
    private Node node;

    /**
     * Constructor of this class
     *
     * @param m
     * @param n
     */
    public GetSender(String m, Node n) {
        node = n;
        msg = m;
    }

    @Override
    public void run() {
        for (int i = 0; i < node.cluster.size(); i++)
            Node.sendUDPSignal(node.cluster.get(i).getIp(), node.cluster.get(i).getUDPPort(), msg);
    }
}
