import java.io.Serializable;

public class Message implements Serializable {
	private String content;
	private long generatedTime;
    private String senderIP;
    private int senderPort;
    private Category category;
    private int ttl;
    
    // Initially set TTL to be maxTTL in the constructor
    public Message(String content, Category category, 
                    int maxTTL, String senderIP, int senderPort) {
        this.content = content;
        this.category = category;
        this.ttl = maxTTL;
        this.senderIP = senderIP;
        this.senderPort = senderPort;
        this.generatedTime = System.nanoTime();
    }

    public Message(int maxTTL, String senderIP, int senderPort) {
        this.ttl = maxTTL;
        this.content = null;
        this.category = null;
        this.senderIP = senderIP;
        this.senderPort = senderPort;
        this.generatedTime = System.nanoTime();
    }

    // Get the value of TTL
	public int getTTL() {
        return this.ttl;
    }

    // Decrease TTL by 1 at each hop
    // If TTL is less or equal to 0 before decreasing, this function will return -1 and abort without decreasing
    public int decreaseTTL() {
        if (this.ttl <= 0) {
            return -1;
        }
        this.ttl -= 1;
        return this.ttl;
    }

    // Get content of this message
    public String getContent() {
        return this.content;
    }

    // Get category of this message
    public Category getCategory() {
        return this.category;
    }

    // Get timestamp of this message
    public long getTimeStamp() {
        return this.generatedTime;
    }

    // Get sender IP of this message
    public String getSenderIP() {
        return this.senderIP;
    }

    // Get sender port of this message
    public int getSenderPort() {
        return this.senderPort;
    }
    
	@Override
    public int hashCode() {
        return (this.senderIP + this.senderPort + this.generatedTime).hashCode();
    }

	@Override
    public boolean equals(Object other) {
        return ((Message)other).generatedTime == this.generatedTime && 
                ((Message)other).senderIP.equals(this.senderIP) && 
                ((Message)other).senderPort == this.senderPort;
    }

}