# P2P-file-transfer-with-Java
This is my final project for my computer network course. It is a Java-based program for handling a p2p file transfer network.
This program consists of several nodes. Each node has a directory with several files in it (not any folder is allowed in that directory). Our nodes represent a 
bi-directional graph that at the beginning one they know some other nodes' UDP port. Later throughout the time nodes communicate each other and form a strongly connected network of 
nodes. Each node can send a request for a file and other nodes will respond if they have the file and then they'll send back the file if the requestor asks them to.
