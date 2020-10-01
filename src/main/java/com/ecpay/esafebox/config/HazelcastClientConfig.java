package com.ecpay.esafebox.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hazelcast")
@Data
public class HazelcastClientConfig {

  private String groupName;

  private String groupPassword;

  private String[] networkAddress;

  private String instanceName;

  @Bean
  public HazelcastInstance hazelcastInstance() {
    return HazelcastClient.newHazelcastClient(hazelcastClientConfig());
  }

  private ClientConfig hazelcastClientConfig() {
    ClientConfig config = new ClientConfig();
    config.setGroupConfig(new GroupConfig(groupName, groupPassword));
    config.getNetworkConfig().addAddress(networkAddress);
    config.setInstanceName(instanceName);

    return config;
  }
}
