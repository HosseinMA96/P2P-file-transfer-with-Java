/**
 * This class represents a responder, that has the file that we want to receive
 */
package Node;

public class Respond {
    private Node node;
    private long responseMilliSeconds;

    /**
     * Constructor of this class
     *
     * @param node
     * @param responsMilliSeconds
     */
    public Respond(Node node, long responsMilliSeconds) {
        this.node = node;
        this.responseMilliSeconds = responsMilliSeconds;
    }

    /**
     * Getter for node
     *
     * @return node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Getter for responseMilliSeconds
     *
     * @return responseMilliSeconds
     */
    public long getResponseMilliSeconds() {
        return responseMilliSeconds;
    }


}
