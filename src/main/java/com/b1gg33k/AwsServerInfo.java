package com.b1gg33k;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BungeeServerInfo;

import java.net.InetSocketAddress;

public class AwsServerInfo extends BungeeServerInfo {
    @Getter
    @Setter
    private String instanceId = null;

    public AwsServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        super(name, address, motd, restricted);
    }

    public AwsServerInfo(BungeeServerInfo serverInfo) {

        super(serverInfo.getName(), serverInfo.getAddress(), serverInfo.getMotd(), serverInfo.isRestricted());
    }
}
