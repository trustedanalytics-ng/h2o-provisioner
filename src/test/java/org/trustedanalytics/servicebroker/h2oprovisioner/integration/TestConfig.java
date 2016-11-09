/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.trustedanalytics.servicebroker.h2oprovisioner.integration;

import static org.mockito.Mockito.mock;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.DeprovisionerYarnClientProvider;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.KerberosClient;
import org.trustedanalytics.servicebroker.h2oprovisioner.config.KerberosProperties;
import org.trustedanalytics.servicebroker.h2oprovisioner.credentials.CredentialsSupplier;
import org.trustedanalytics.servicebroker.h2oprovisioner.ports.PortsPool;
import org.trustedanalytics.servicebroker.h2oprovisioner.service.H2oDeprovisioner;
import org.trustedanalytics.servicebroker.h2oprovisioner.service.externals.H2oDriverExec;
import org.trustedanalytics.servicebroker.h2oprovisioner.service.externals.H2oUiFileParser;
import org.trustedanalytics.servicebroker.h2oprovisioner.service.externals.KinitExec;

@Configuration
@Profile("test")
public class TestConfig {

  public static final int FAKE_DRIVER_CALLBACK_PORT = 1410;
  public static final String FAKE_H2O_INSTANCE_USERNAME = "username";
  public static final String FAKE_H2O_INSTANCE_PASSWORD = "p4$sw0rd";

  @Bean
  public PortsPool portsPool() {
    return () -> FAKE_DRIVER_CALLBACK_PORT;
  }

  @Bean
  public CredentialsSupplier usernameSupplier() {
    return () -> FAKE_H2O_INSTANCE_USERNAME;
  }

  @Bean
  public CredentialsSupplier passwordSupplier() {
    return () -> FAKE_H2O_INSTANCE_PASSWORD;
  }

  @Bean
  public KinitExec kinitExec() {
    return mock(KinitExec.class);
  }

  @Bean
  public H2oDriverExec h2oDriverExec() {
    return mock(H2oDriverExec.class);
  }

  @Bean
  public H2oUiFileParser h2oUiFileParser() {
    return mock(H2oUiFileParser.class);
  }

  @Bean
  public org.apache.hadoop.conf.Configuration hadoopConf() {
    return new org.apache.hadoop.conf.Configuration(false);
  }

  @Bean
  @Autowired
  public H2oDeprovisioner getH2oDeprovisioner(KerberosProperties kerberosProperties,
                                              Optional<KerberosClient> kerberosClient,
                                              DeprovisionerYarnClientProvider deprovisionerYarnClientProvider,
                                              org.apache.hadoop.conf.Configuration hadoopConf) {
    return new H2oDeprovisioner(kerberosProperties.getUser(), kerberosClient,
        deprovisionerYarnClientProvider, hadoopConf);
  }

  @Bean
  public Optional<KerberosClient> kerberosClient() {
    return Optional.of(mock(KerberosClient.class));
  }

  @Bean
  public DeprovisionerYarnClientProvider deprovisionerYarnClientProvider() {
    return mock(DeprovisionerYarnClientProvider.class);
  }
}
