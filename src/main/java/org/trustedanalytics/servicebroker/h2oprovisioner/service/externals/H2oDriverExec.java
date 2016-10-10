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

package org.trustedanalytics.servicebroker.h2oprovisioner.service.externals;

import java.io.IOException;
import java.util.Map;
import org.trustedanalytics.servicebroker.h2oprovisioner.service.externals.helpers.ExternalProcessExecutor;

public class H2oDriverExec {

  public H2oDriverExec() {}

  public void spawnH2oOnYarn(String[] command, Map<String, String> commandEnvVariables)
      throws ExternalProcessException, IOException {

    int h2oExitCode = ExternalProcessExecutor.runCommand(command, commandEnvVariables);
    if (h2oExitCode != 0) {
      throw new ExternalProcessException("h2odriver.jar exited with code " + h2oExitCode);
    }
  }
}
