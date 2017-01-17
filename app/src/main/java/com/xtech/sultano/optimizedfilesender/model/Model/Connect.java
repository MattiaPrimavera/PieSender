package com.xtech.sultano.optimizedfilesender.model.Model;

import java.net.InetAddress;

public class Connect implements Comparable<Connect>{
    private String serverName;

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerAddress(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    private InetAddress serverAddress;

    public String getServerName() {
        return serverName;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public Connect(String serverName, InetAddress serverAddress){
        this.serverName = serverName;
        this.serverAddress = serverAddress;
    }


    @Override
    public int compareTo(Connect c) {
/*        if(d.getProgress() == this.progress)
            return d.getDimension() > this.dimension ? -1 : 1;
        return d.getProgress() > this.progress ? -1 : 1;*/
        return 0;
    }
}
