package com.b1gg33k;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class AwsServerListener implements Listener {
    @Getter
    @Setter
    AwsLauncher launcher;

    public AwsServerListener(AwsLauncher launcher) {
        this.launcher = launcher;
    }

    @EventHandler
    public void handleServerConnectEvent(ServerConnectEvent event){

    }
}
