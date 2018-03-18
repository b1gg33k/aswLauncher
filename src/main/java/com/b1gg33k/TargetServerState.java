package com.b1gg33k;

import com.amazonaws.services.ec2.model.InstanceStateName;
import lombok.Getter;

public class TargetServerState {
    @Getter
    private AwsServerInfo serverInfo;

    @Getter
    private InstanceStateName stateName;

    @Getter
    private Long deadline;

    public TargetServerState(AwsServerInfo serverInfo, InstanceStateName stateName, Long deadline) {
        this.serverInfo = serverInfo;
        this.stateName = stateName;
        this.deadline = deadline;
    }


}
