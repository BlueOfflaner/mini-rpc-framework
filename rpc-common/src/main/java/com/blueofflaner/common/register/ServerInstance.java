package com.blueofflaner.common.register;

import lombok.Data;

@Data
public class ServerInstance {
    private String host;
    private int port;
    private double weight = 1;

    public ServerInstance() {

    }

    public ServerInstance(String host, int port, double weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }
}
