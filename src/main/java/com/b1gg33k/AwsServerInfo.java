package com.b1gg33k;

import com.amazonaws.services.ec2.model.InstanceState;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BungeeServerInfo;

import java.net.InetSocketAddress;

public class AwsServerInfo extends BungeeServerInfo {
    @Getter
    @Setter
    private String instanceId = null;

    @Getter
    @Setter
    private InstanceState state;

    public AwsServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        super(name, address, motd, restricted);
    }

    public AwsServerInfo(BungeeServerInfo serverInfo) {

        super(serverInfo.getName(), serverInfo.getAddress(), serverInfo.getMotd(), serverInfo.isRestricted());
    }
}
