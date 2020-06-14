package Node;

public class Respond   {
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


}
