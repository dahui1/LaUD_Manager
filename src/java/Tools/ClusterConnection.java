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
package Tools;

import java.io.IOException;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * file:Client.java
 *this class use cassandra client, connect ,disconnect, get keyspace ...
 * created at:11-03-2012
 * @author 林丹
 * */
public class ClusterConnection{
    public static final String DEFAULT_THRIFT_HOST = "localhost";
    public static final int DEFAULT_THRIFT_PORT = 9170;
    public static final int DEFAULT_JMX_PORT = 7199;
  
    private TTransport transport;
    private TProtocol protocol;
    public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	private Client client;
    private NodeProbe probe;

    private boolean connected = false;
    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getThriftPort() {
		return thriftPort;
	}

	public void setThriftPort(int thriftPort) {
		this.thriftPort = thriftPort;
	}

	private String host;
    private int thriftPort;
    private int jmxPort;

 

    public ClusterConnection() {
        this(DEFAULT_THRIFT_HOST, DEFAULT_THRIFT_PORT, DEFAULT_JMX_PORT);
    }

    public ClusterConnection(String host) {
        this(host, DEFAULT_THRIFT_PORT, DEFAULT_JMX_PORT);
    }

    public ClusterConnection(String host, int thriftPort, int jmxPort) {
        this.host = host;
        this.thriftPort = thriftPort;
        this.jmxPort = jmxPort;
    }

    public void connect() throws IOException, InterruptedException, TTransportException{
        if (!connected) {
            // Updating the transport to Framed one as it has been depreciated with Cassandra 0.7.0
            transport = new TFramedTransport(new TSocket(host, thriftPort));
            protocol = new TBinaryProtocol(transport);
            client = new Client(protocol);
            setProbe(new NodeProbe(host, jmxPort));
			transport.open();
           
            connected = true;
        }
    }

    public void disconnect() {
        if (connected) {
            transport.close();
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

  
	public NodeProbe getProbe() {
		return probe;
	}

	public void setProbe(NodeProbe probe) {
		this.probe = probe;
	}
	public int getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(int jmxPort) {
		this.jmxPort =  jmxPort;
	}
  
}
