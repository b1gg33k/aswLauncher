package com.b1gg33k;

import com.amazonaws.services.ec2.model.InstanceStateName;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ServerStateEvent extends Event {
    @Getter
    private final ProxiedPlayer player;

    @Getter
    private TargetServerState targetServerState;

    @Getter
    private InstanceStateName currentState;

    public ServerStateEvent(ServerStateEvent other, InstanceStateName currentState) {
        this.player = other.getPlayer();
        this.targetServerState = other.targetServerState;
        this.currentState = currentState;
    }

    public ServerStateEvent(ProxiedPlayer player, TargetServerState targetServerState, InstanceStateName currentState) {
        this.player = player;
        this.targetServerState = targetServerState;
        this.currentState = currentState;
    }

    public ServerStateEvent setCurrentState(InstanceStateName currentState) {
        this.currentState = currentState;
        return this;
    }

    public ServerStateEvent setTargetServerState(TargetServerState targetServerState) {
        this.targetServerState = targetServerState;
        return this;
    }


}
