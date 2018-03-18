package com.b1gg33k;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import lombok.Getter;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwsInstanceService {
    final AmazonEC2 client = AmazonEC2ClientBuilder.defaultClient();
    @Getter
    private Map<String, AwsServerInfo> awsServers = new HashMap<>();

    private static AwsLauncher plugin;


    private static AwsInstanceService service = null;

    public static AwsInstanceService getService(){
        if (null == service){
            service = new AwsInstanceService();
        }
        return service;
    }

    private AwsInstanceService() {

    }

    public static AwsLauncher getPlugin() {
        return plugin;
    }

    public static void setPlugin(AwsLauncher plugin) {
        AwsInstanceService.plugin = plugin;
    }

    public void start(AwsServerInfo serverInfo, ProxiedPlayer initiatingPlayer){

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(serverInfo.getInstanceId());

        StartInstancesResult result = client.startInstances(request);
        //Fake it
        /*
        List<InstanceStateChange> fauxList = Arrays.asList(new InstanceStateChange().withInstanceId(serverInfo.getInstanceId()).withCurrentState(new InstanceState().withName("pending")));
        StartInstancesResult result = new StartInstancesResult().withStartingInstances(fauxList);
        */


        for (InstanceStateChange stateChange : result.getStartingInstances()){
            if (stateChange.getInstanceId().equals(serverInfo.getInstanceId())){
                TargetServerState targetServerState = new TargetServerState(serverInfo, InstanceStateName.Running,System.currentTimeMillis() + 120*1000L);
                InstanceStateName currentState = InstanceStateName.fromValue(stateChange.getCurrentState().getName());
                ServerStateEvent serverStateEvent = new ServerStateEvent(initiatingPlayer, targetServerState,currentState);
                sendServerStateEvent(serverStateEvent);
            }
        }
    }

    public InstanceStateName state(AwsServerInfo serverInfo){
        DescribeInstanceStatusRequest statusRequest = new DescribeInstanceStatusRequest().withInstanceIds(serverInfo.getInstanceId());

        DescribeInstanceStatusResult statusResult = client.describeInstanceStatus(statusRequest);
        for (InstanceStatus status : statusResult.getInstanceStatuses()){
            if (status.getInstanceId().equals(serverInfo.getInstanceId())){
                return InstanceStateName.fromValue(status.getInstanceState().getName());
            }
        }
        return null;
    }
//
//    public Map<String,AwsServerInfo> findInstances(List<BungeeServerInfo> serverInfos){
//        Map<String,BungeeServerInfo> serverInfosMap = new HashMap<>();
//        serverInfos.forEach(serverInfo -> serverInfosMap.put(serverInfo.getName(),serverInfo));
//
//        Filter tagFilter = new Filter("tag:name", new ArrayList<>(serverInfosMap.keySet()));
//        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withFilters(tagFilter);
//
//        DescribeInstancesResult describeInstancesResult = client.describeInstances(describeInstancesRequest);
//        for (Reservation reservation : describeInstancesResult.getReservations()){
//
//            for (Instance instance : reservation.getInstances()){
//                Map<String,String> tags = new HashMap<>();
//                instance.getTags().forEach(tag -> tags.put(tag.getKey(),tag.getValue()));
//
//                if (tags.containsKey("name") && serverInfosMap.containsKey(tags.get("name"))){
//                    String name = tags.get("name");
//                    BungeeServerInfo serverInfo = serverInfosMap.get(name);
//                    String motd = (tags.containsKey("motd")) ? tags.get("motd") : serverInfo.getMotd();
//                    boolean restricted = tags.containsKey("restricted") ? Boolean.parseBoolean(tags.get("restricted")) : serverInfo.isRestricted();
//                    String privateAddress = instance.getPrivateIpAddress();
//                    Integer port = (tags.containsKey("port")) ? Integer.parseInt(tags.get("port")) : serverInfo.getAddress().getPort();
//                    InetSocketAddress address = new InetSocketAddress(privateAddress,port);
//                    AwsServerInfo awsServerInfo = new AwsServerInfo(name,address,motd,restricted);
//                    awsServers.put(name,awsServerInfo);
//                }
//            }
//
//        }
//        return awsServers;
//    }

    public Map<String,AwsServerInfo> findInstances(){
        Filter typeFilter = new Filter("tag:type", Arrays.asList("worldhost","world"));
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withFilters(typeFilter);

        client.describeInstances(describeInstancesRequest).getReservations().forEach(reservation -> {
            reservation.getInstances().forEach(instance -> {
                Map<String, String> tags = new HashMap<>();
                instance.getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));

                String name = tags.get("name");
                String motd = (tags.containsKey("motd")) ? tags.get("motd") : instance.getInstanceType();
                boolean restricted = tags.containsKey("restricted") ? Boolean.parseBoolean(tags.get("restricted")) : Boolean.FALSE;
//                String privateAddress = instance.getPrivateIpAddress();
                String privateAddress = instance.getPublicIpAddress();
                Integer port = (tags.containsKey("port")) ? Integer.parseInt(tags.get("port")) : Util.DEFAULT_PORT;
                InetSocketAddress address = new InetSocketAddress(privateAddress, port);
                AwsServerInfo awsServerInfo = new AwsServerInfo(name, address, motd, restricted);
                awsServerInfo.setInstanceId(instance.getInstanceId());

                awsServers.put(name, awsServerInfo);
            });
        });

        return awsServers;
    }

    public InstanceStateName getState(AwsServerInfo serverInfo){
        InstanceStateName state = InstanceStateName.Terminated;

        DescribeInstanceStatusRequest statusRequest = new DescribeInstanceStatusRequest().withInstanceIds(Arrays.asList(serverInfo.getInstanceId()));
        DescribeInstanceStatusResult statusResult =  client.describeInstanceStatus(statusRequest);

        for (InstanceStatus instanceStatus : statusResult.getInstanceStatuses()){
            if(instanceStatus.getInstanceId().equals(serverInfo.getInstanceId())){
                state = InstanceStateName.fromValue(instanceStatus.getInstanceState().getName());
                break;
            }
        }
        return state;
    }

    public void sendServerStateEvent(ServerStateEvent stateEvent){
        if (System.currentTimeMillis() < stateEvent.getTargetServerState().getDeadline()){
            getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    //wait a bit...
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //getProxy().getPluginManager().callEvent(new MyAwesomeEvent("I'm awesome!"));
                    getPlugin().getProxy().getPluginManager().callEvent(stateEvent);
                }
            });
        } else {
            //We have reashed the deadline for this check without reaching our target. do we care???
        }
    }

}
