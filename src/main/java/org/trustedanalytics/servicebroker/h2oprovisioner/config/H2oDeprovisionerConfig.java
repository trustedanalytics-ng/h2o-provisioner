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
package org.trustedanalytics.servicebroker.h2oprovisioner.config;

import java.util.Optional;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.apache.hadoop.conf.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.DeprovisionerYarnClientProvider;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.KerberosClient;
import org.trustedanalytics.servicebroker.h2oprovisioner.service.H2oDeprovisioner;

@org.springframework.context.annotation.Configuration
@Profile({"cloud", "default"})
public class H2oDeprovisionerConfig {

  @Autowired
  private ExternalConfiguration config;

  @Bean
  public Configuration hadoopConfig() {
    Configuration toReturn = new Configuration();
    String configurationPath = config.getYarnConfDir();
    toReturn.addResource(new Path(configurationPath + "/core-site.xml"));
    toReturn.addResource(new Path(configurationPath + "/hdfs-site.xml"));
    toReturn.addResource(new Path(configurationPath + "/mapred-site.xml"));
    toReturn.addResource(new Path(configurationPath + "/yarn-site.xml"));
    return toReturn;
  }

  @Bean
  @Autowired
  public H2oDeprovisioner getH2oDeprovisioner(KerberosProperties kerberosProperties, Configuration hadoopConfig) {
    return new H2oDeprovisioner(kerberosProperties.getUser(), createKerberos(kerberosProperties),
        new DeprovisionerYarnClientProvider(), hadoopConfig);
  }

  private Optional<KerberosClient> createKerberos(KerberosProperties properties) {
    if(!properties.isEnabled()) {
      return Optional.empty();
    }
    return Optional.of(new KerberosClient(properties));
  }
}
