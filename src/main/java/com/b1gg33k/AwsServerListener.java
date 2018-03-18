package com.b1gg33k;

import com.amazonaws.services.ec2.model.InstanceStateName;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
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
        event.getTarget().ping(new Callback<ServerPing>() {
            @Override
            public void done(ServerPing result, Throwable error) {
                if (null != error){
                    //io.netty.channel.ConnectTimeoutException: connection timed out: /172.31.22.200:25565
                    //Is it down?
                    event.getPlayer().chat("Avast! It be down in davy jones locker!");
                    event.getPlayer().chat("Attempting to start server");
                    AwsInstanceService.getService().start((AwsServerInfo) event.getTarget(), event.getPlayer());
                } else {
                    System.out.println(String.format("Players online: %d", result.getPlayers().getOnline()));
                }
            }
        });
    }

    @EventHandler
    public void handleStateCheck(ServerStateEvent event){
        InstanceStateName newState = AwsInstanceService.getService().getState(event.getTargetServerState().getServerInfo());
        if (!event.getCurrentState().equals(newState)){
            //State has changed, tell the player about it!
            if (newState.equals(event.getTargetServerState().getStateName())){
                //We have reached are target, Mission accomplished. Everyone gets a trophy!
                event.getPlayer().chat(String.format("World %s is now ready.", event.getTargetServerState().getServerInfo().getName()));
            } else {
                //Not there yet, give a status update.
                event.getPlayer().chat(
                        String.format("World host for %s has changed from %s to %s.",
                                event.getTargetServerState().getServerInfo().getName(),
                                event.getCurrentState().name(),
                                newState.name()));
                ServerStateEvent retryServerStateEvent = new ServerStateEvent(event,newState);
                AwsInstanceService.getService().sendServerStateEvent(retryServerStateEvent);
            }
        } else {
            //No change, check again.
            if (!newState.equals(event.getTargetServerState().getStateName())) {
                event.getPlayer().chat(String.format("World Host %s has not changed; status: %s", event.getTargetServerState().getServerInfo().getName(), newState.name()));
                ServerStateEvent retryServerStateEvent = new ServerStateEvent(event, newState);
                AwsInstanceService.getService().sendServerStateEvent(retryServerStateEvent);
            }
        }
    }

}
