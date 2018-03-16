package com.b1gg33k;

import lombok.Getter;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;


public class AwsLauncher extends Plugin implements Listener {

    public static final String INSTANCE_ID = "instanceId";

    @Getter
    private final Map<String, AwsServerInfo> awsServers = new HashMap<>();

    public AwsLauncher() {
        super();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ProxyServer proxyServer  = super.getProxy();
        if (null != proxyServer) {
            this.addHandler(proxyServer);

            Map<String,Map<String,String>> serverConfig = new HashMap<>();

            Collection<Map<String,String>> rawServerConfig = (Collection<Map<String, String>>) proxyServer.getConfigurationAdapter().getList("servers", null);
            for (Map<String,String> serverMap : rawServerConfig){
                getLogger().log(Level.INFO, serverMap.toString());
            }


            Iterator<String> it = proxyServer.getServers().keySet().iterator();
            while (it.hasNext()){
                String serverName = it.next();

                ServerInfo serverInfo = proxyServer.getServers().get(serverName);
                getLogger().log(Level.INFO,"FOUND: " + serverName);

                if (serverConfig.containsKey(serverName)) {
                    Map<String,String> serverProperties = serverConfig.get(serverName);

                    if (serverProperties.containsKey(INSTANCE_ID)) {
                        //We found an amazon instance!
                        AwsServerInfo awsServerInfo = new AwsServerInfo((BungeeServerInfo) serverInfo);
                        awsServerInfo.setInstanceId(serverProperties.get(INSTANCE_ID));
                        this.awsServers.put(serverName,awsServerInfo);
                    }
                }
            }


//            Map<String,String> awsDefaults = new HashMap();
//            awsDefaults.put("key",null);
//            awsDefaults.put("secret",null);
//            Map<String,String> awsConfig = (Map<String, String>) proxyServer.getConfigurationAdapter().getList("aws", new ArrayList<>());
//
//            for (String name : awsConfig.keySet()){
//                String value = awsConfig.get(name);
//                getLogger().log(Level.INFO, String.format("AWS %s: %s", name, value));
//            }
        }
    }

    private void addHandler(ProxyServer proxyServer){
        AwsServerListener listener = new AwsServerListener(this);

        proxyServer.getPluginManager().registerListener(this, listener);

    }

}
