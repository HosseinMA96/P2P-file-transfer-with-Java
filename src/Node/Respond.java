package Node;

public class Respond implements Comparable {
    private Node node;
    private long responseMilliSeconds;

    public Respond(Node node, long responsMilliSeconds) {
        this.node = node;
        this.responseMilliSeconds = responsMilliSeconds;
    }

    public Node getNode() {
        return node;
    }

    public long getResponseMilliSeconds() {
        return responseMilliSeconds;
    }

    @Override
    public int compareTo(Object o) {
        if(this.responseMilliSeconds>((Respond)o).getResponseMilliSeconds())
            return 1;

        else
            return -1;
    }
}
