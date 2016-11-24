/**
 * Copyright (c) 2016 Intel Corporation
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
package org.trustedanalytics.servicebroker.h2oprovisioner.cdhclients;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;

public class DeprovisionerYarnClientProviderTest {

  @Test
  public void getClient_SetProperUgiLoginUser() throws IOException {
    DeprovisionerYarnClientProvider sut = new DeprovisionerYarnClientProvider();
    sut.getClient("someUser", new Configuration());

    assertEquals("someUser", UserGroupInformation.getLoginUser().getUserName());
  }
}
