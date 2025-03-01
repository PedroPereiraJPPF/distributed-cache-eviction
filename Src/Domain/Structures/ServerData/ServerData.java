package Src.Domain.Structures.ServerData;

import java.io.Serializable;

public class ServerData implements Serializable {
    public String IP;
    public Integer port;

    public ServerData(String serverIP, Integer serverPort) {
        this.IP = serverIP;
        this.port = serverPort;
    }
}
