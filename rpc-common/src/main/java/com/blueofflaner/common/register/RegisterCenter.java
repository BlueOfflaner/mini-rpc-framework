package com.blueofflaner.common.register;

import java.util.List;

public interface RegisterCenter {

    List<ServerInstance> getService(String serviceName);
    boolean register(String serviceName, String host, int port, double weight);
    boolean register(String serviceName, String host, int port);
    boolean deRegister(String serviceName, String host, int port);
}
