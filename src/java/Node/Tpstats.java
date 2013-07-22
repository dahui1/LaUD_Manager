/**
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package Node;

import java.io.Serializable;

public class Tpstats implements Serializable {
    private static final long serialVersionUID = -7848179032971193937L;

    private String poolName;
    private int activeCount;
    private long pendingTasks;
    private long completedTasks;

    /**
     * @return the poolName
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * @param poolName the poolName to set
     */
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * @return the activeCount
     */
    public int getActiveCount() {
        return activeCount;
    }

    /**
     * @param activeCount the activeCount to set
     */
    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    /**
     * @return the pendingTasks
     */
    public long getPendingTasks() {
        return pendingTasks;
    }

    /**
     * @param pendingTasks the pendingTasks to set
     */
    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    /**
     * @return the completedTasks
     */
    public long getCompletedTasks() {
        return completedTasks;
    }

    /**
     * @param completedTasks the completedTasks to set
     */
    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }
}
