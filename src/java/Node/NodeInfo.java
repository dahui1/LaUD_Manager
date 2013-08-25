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

import org.apache.cassandra.dht.Token;

public class NodeInfo implements Serializable {
    private static final long serialVersionUID = -6585600091642457499L;

    private Token token;
    private String endpoint;
    private String Range;
    private long uptime;
    private double memMax;
    private double memUsed;
    private String dataCenter;
    private String rack;
    private String status;
    private String state;
    private String load;
    private String owns;

    public Token getToken() {
            return token;
    }

    public void setToken(Token token) {
            this.token = token;
    }


    public String getLoad() {
            return load;
    }

    public void setLoad(String string) {
            this.load = string;
    }

    public String getEndpoint() {
            return endpoint;
    }

    public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
    }

    public long getUptime() {
            return uptime;
    }

    public void setUptime(long uptime) {
            this.uptime = uptime;
    }

    public double getMemMax() {
            return memMax;
    }

    public void setMemMax(double memMax) {
            this.memMax = memMax;
    }

    public String getDataCenter() {
            return dataCenter;
    }

    public void setDataCenter(String dataCenter) {
            this.dataCenter = dataCenter;
    }

    public String getRack() {
            return rack;
    }

    public void setRack(String rack) {
            this.rack = rack;
    }

    public String getStatus() {
            return status;
    }

    public void setStatus(String status) {
            this.status = status;
    }

    public String getState() {
            return state;
    }

    public void setState(String state) {
            this.state = state;
    }

    public static long getSerialversionuid() {
            return serialVersionUID;
    }

    public String getRange() {
            return Range;
    }

    public void setRange(String range) {
            this.Range = range;
    }

    public double getMemUsed() {
            return memUsed;
    }

    public void setMemUsed(double memUsed) {
            this.memUsed = memUsed;
    }

    public String getOwns() {
            return owns;
    }

    public void setOwns(String owns) {
            this.owns = owns;
    }

}
