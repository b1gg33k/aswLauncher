package com.b1gg33k;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;


public class AwsLauncher extends Plugin implements Listener {

    public static final String INSTANCE_ID = "instanceId";

    public AwsLauncher() {
        super();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ProxyServer proxyServer  = super.getProxy();
        if (null != proxyServer) {
            this.addHandler(proxyServer);

            AwsInstanceService.setPlugin(this);
            AwsInstanceService.getService().findInstances().forEach((name,serverInfo) -> {
                getLogger().log(Level.INFO, "Found server: " + serverInfo.getInstanceId());
                proxyServer.getServers().put(serverInfo.getName(), serverInfo);
            });

//            Map<String,Map<String,String>> serverConfig = new HashMap<>();
//
//            Collection<Map<String,String>> rawServerConfig = (Collection<Map<String, String>>) proxyServer.getConfigurationAdapter().getList("servers", null);
//            for (Map<String,String> serverMap : rawServerConfig){
//                getLogger().log(Level.INFO, serverMap.toString());
//            }
//
//
//            Iterator<String> it = proxyServer.getServers().keySet().iterator();
//            while (it.hasNext()){
//                String serverName = it.next();
//
//                ServerInfo serverInfo = proxyServer.getServers().get(serverName);
//                getLogger().log(Level.INFO,"FOUND: " + serverName);
//
//                if (serverConfig.containsKey(serverName)) {
//                    Map<String,String> serverProperties = serverConfig.get(serverName);
//
//                    if (serverProperties.containsKey(INSTANCE_ID)) {
//                        //We found an amazon instance!
//                        AwsServerInfo awsServerInfo = new AwsServerInfo((BungeeServerInfo) serverInfo);
//                        awsServerInfo.setInstanceId(serverProperties.get(INSTANCE_ID));
//                        InstanceStateName state = instanceService.state(awsServerInfo);
//                        if (null != state){
//                            this.awsServers.put(serverName,awsServerInfo);
//                            getLogger().log(Level.INFO, "Find instance " + awsServerInfo.getInstanceId() + " in AWS.");
//                        } else {
//                            getLogger().log(Level.SEVERE, "Could not find instance " + awsServerInfo.getInstanceId() + " in AWS.");
//                        }
//                    }
//                }
//            }
        }
    }

    private void addHandler(ProxyServer proxyServer){
        AwsServerListener listener = new AwsServerListener(this);

        proxyServer.getPluginManager().registerListener(this, listener);

    }

}
