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
package org.trustedanalytics.servicebroker.h2oprovisioner.service;

import java.io.IOException;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.DeprovisionerYarnClient;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.DeprovisionerYarnClientProvider;
import org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients.KerberosClient;
import org.trustedanalytics.servicebroker.h2oprovisioner.rest.H2oDeprovisioningException;
import org.trustedanalytics.servicebroker.h2oprovisioner.rest.JobNotFoundException;

public class H2oDeprovisioner {

  private static final Logger LOGGER = LoggerFactory.getLogger(H2oDeprovisioner.class);

  private final String kerberosUser;
  private final Optional<KerberosClient> kerberos;
  private final DeprovisionerYarnClientProvider yarnClientProvider;
  private final Configuration hadoopConf;

  public H2oDeprovisioner(String kerberosUser, Optional<KerberosClient> kerberos,
                          DeprovisionerYarnClientProvider yarnClientProvider,
                          Configuration hadoopConf) {
    this.kerberos = kerberos;
    this.yarnClientProvider = yarnClientProvider;
    this.kerberosUser = kerberosUser;
    this.hadoopConf = hadoopConf;
  }

  public String deprovisionInstance(String serviceInstanceId)
      throws H2oDeprovisioningException, JobNotFoundException {

    LOGGER
        .warn("Please be informed that hadoopConfiguration passed from broker is not used anymore" +
            "Config loaded from environment will be in operation for now");

    try {
      DeprovisionerYarnClient yarnClient;
      LOGGER.debug("Creating yarn client...");
      Configuration conf = getConfiguration(hadoopConf);

      yarnClient = yarnClientProvider.getClient(kerberosUser, conf);
      LOGGER.debug("Yarn client created.");
      return deprovisionH2o(yarnClient, serviceInstanceId);
    } catch (IOException e) {
      throw new H2oDeprovisioningException(
          "Unable to create yarn client." + e.getMessage(), e);
    }
  }

  private Configuration getConfiguration(Configuration hadoopConf)
      throws H2oDeprovisioningException {
    if (kerberos.isPresent()) {
      LOGGER.debug("Logging in to Kerberos is necessary to acquire config...");
      try {
        return kerberos.get().logInToKerberos(hadoopConf);
      } catch (LoginException | IOException e) {
        throw new H2oDeprovisioningException("Unable to log in: " + e.getMessage(), e);
      }
    }
    return hadoopConf;
  }

  private String deprovisionH2o(DeprovisionerYarnClient yarnClient, String serviceInstanceId)
      throws H2oDeprovisioningException, JobNotFoundException {
    try {
      LOGGER.debug("Starting yarn client...");
      yarnClient.start();
      LOGGER.debug("Yarn client started.");

      LOGGER.debug("Extracting job Id...");
      ApplicationId h2oServerJobId = yarnClient.getH2oJobId(serviceInstanceId);
      LOGGER.debug("Concluded id of job to kill: " + h2oServerJobId.toString());
      yarnClient.killApplication(h2oServerJobId);
      LOGGER.debug("Job " + h2oServerJobId + " killed.");
      return h2oServerJobId.toString();
    } catch (YarnException | IOException e) {
      throw new H2oDeprovisioningException("Unable to deprovision H2O " + e.getMessage(), e);
    }
  }
}
