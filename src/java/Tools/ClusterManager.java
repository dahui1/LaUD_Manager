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
import java.lang.management.MemoryUsage;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.db.ColumnFamilyStoreMBean;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.transport.TTransportException;

import Node.NodeInfo;
import Node.RingNode;
import Unit.KsStats;

/**
 * Contain Cluster Manager function ,such as compression, show ring, join and so on
 * use NodeProbe
 * @author 林丹
 * */
public class ClusterManager {
    private NodeProbe probe;
    private Client client;
    
    public static enum NodeCommand
    {
        CFHISTOGRAMS,
        CFSTATS,
        CLEANUP,
        CLEARSNAPSHOT,
        COMPACT,
        COMPACTIONSTATS,
        DECOMMISSION,
        DISABLEGOSSIP,
        DISABLETHRIFT,
        DRAIN,
        ENABLEGOSSIP,
        ENABLETHRIFT,
        FLUSH,
        GETCOMPACTIONTHRESHOLD,
        GETENDPOINTS,
        GOSSIPINFO,
        INFO,
        INVALIDATEKEYCACHE,
        INVALIDATEROWCACHE,
        JOIN,
        MOVE,
        NETSTATS,
        REFRESH,
        REMOVETOKEN,
        REPAIR,
        RING,
        SCRUB,
        SETCACHECAPACITY,
        SETCOMPACTIONTHRESHOLD,
        SETCOMPACTIONTHROUGHPUT,
        SETSTREAMTHROUGHPUT,
        SNAPSHOT,
        STATUSTHRIFT,
        TPSTATS,
        UPGRADESSTABLES,
        VERSION,
        DESCRIBERING,
    }

    public ClusterManager(ClusterConnection clusterConnection, String host,int jmxPort ) throws TTransportException, IOException, InterruptedException
    {
        if(!clusterConnection.isConnected())
        {
            clusterConnection.connect();
        }
        this.client = clusterConnection.getClient();
        this.probe = new NodeProbe(host, jmxPort);

    }
	 
    public ClusterManager(ClusterConnection clusterConnection) 
    {
        if(!clusterConnection.isConnected())
        {
            try {
                clusterConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        this.client = clusterConnection.getClient();
        this.probe =   clusterConnection.getProbe();
    }
	 
    // 获取环中的所有节点
    public RingNode listRing() {
        RingNode r = new RingNode();
        r.setRangeMap(probe.getTokenToEndpointMap());
        List<Token> ranges = new ArrayList<Token>(r.getRangeMap().keySet());
        Collections.sort(ranges);
        r.setRanges(ranges);
        return r;
    }
	
    public boolean isRunning()
    {
        return probe.isThriftServerRunning();
    }
	

    // 根据给定IP获取对应节点信息
    public NodeInfo getNodeInfo(String primaryEndpoint,Token token) throws IOException, InterruptedException {
        Collection<String> liveNodes = probe.getLiveNodes();
        Collection<String> deadNodes = probe.getUnreachableNodes();
        Collection<String> joiningNodes = probe.getJoiningNodes();
        Collection<String> leavingNodes = probe.getLeavingNodes();
        Collection<String> movingNodes = probe.getMovingNodes();
        Map<String, String> loadMap = probe.getLoadMap();
        Map<Token, Float> ownerships = probe.getOwnership();
       
        NodeInfo ni = new NodeInfo();
        ni.setToken(token);
        ni.setEndpoint(primaryEndpoint);
        ni.setUptime(probe.getUptime() / 1000);
        ni.setRange(token.toString());
        MemoryUsage heapUsage = probe.getHeapMemoryUsage();
        ni.setMemUsed((double) heapUsage.getUsed() / (1024 * 1024));
        ni.setMemMax((double) heapUsage.getMax() / (1024 * 1024));
        
        String rack;
        try
        {
            rack = probe.getEndpointSnitchInfoProxy().getRack(primaryEndpoint);
        }
        catch (UnknownHostException e)
        {
            rack = "Unknown";
        }
        
        
        String dataCenter= "";
        try
        {
            dataCenter = probe.getEndpointSnitchInfoProxy().getDatacenter(primaryEndpoint);
        }
        catch (UnknownHostException e)
        {
            dataCenter = "Unknown";
        }
        
        String status = liveNodes.contains(primaryEndpoint)
                        ? "Up"
                        : deadNodes.contains(primaryEndpoint)
                          ? "Down"
                          : "?";

        String state = "Normal";

        if (joiningNodes.contains(primaryEndpoint))
            state = "Joining";
        else if (leavingNodes.contains(primaryEndpoint))
            state = "Leaving";
        else if (movingNodes.contains(primaryEndpoint))
            state = "Moving";

        String load = loadMap.containsKey(primaryEndpoint)
                      ? loadMap.get(primaryEndpoint)
                      : "?";
        String owns = new DecimalFormat("##0.00%").format(ownerships.get(token));
        ni.setDataCenter(dataCenter);
        ni.setRack(rack);
        ni.setState(state);
        ni.setStatus(status);
        ni.setOwns(owns);
        ni.setLoad(load);
        return ni;
        
    }
    
    public int ringSize()
    {
    	return listRing().getRanges().size();
    }
     
    // 获取环中所有节点的信息列表
    public ArrayList<NodeInfo> getNodeInfoList() throws IOException, InterruptedException {
        ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
        RingNode ringNode = listRing(); // get all the nodes on the ring
        Map<Token, String> rangeMap = ringNode.getRangeMap(); // get the token
                                                                                                                        // and IP maps
        List<Token> ranges = ringNode.getRanges(); // get the token of all nodes
        for (Token<String> range : ranges) {
            String primaryEndpoint = rangeMap.get(range); // get IP address from the token
            NodeInfo nodeinfo = getNodeInfo(primaryEndpoint, range);
            nodes.add(nodeinfo);
        }
        return nodes;
    }
	 
    public NodeProbe getProbe() {
        return probe;
    }

    public void setProbe(NodeProbe probe) {
        this.probe = probe;
    }
    
    public void close()
    {
        try {
            probe.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取一个给定KS中指定的CF信息
    public ColumnFamilyStoreMBean getColumnFamilyStatics(String keyspace,String columnFamily )
    {
        ArrayList<KsStats> ksStatsList = printColumnFamilyStats();
        for(KsStats ksStats: ksStatsList)
        {
            if(keyspace.equals(ksStats.getKsName()))
            {
                List<ColumnFamilyStoreMBean> columnFamilies =  ksStats.getColumnFamilies();
                for (ColumnFamilyStoreMBean cfstore : columnFamilies)
                {
                    if(columnFamily.endsWith(cfstore.getColumnFamilyName()))
                    {
                        return cfstore;
                    }
                }
            }
        }
        return null;
    }

    // 获取给定Keyspace的信息
    public Map<String,String> getKeyspaceStatics(String keyspace)
    {
        ArrayList<KsStats> ksStatsList = printColumnFamilyStats();
        for(KsStats ksStats: ksStatsList)
        {
            if(keyspace.equals(ksStats.getKsName()))
            {
                return ksStats.getStatics();
            }
        }
        //if the keyspace did not have columns , then there is not keyspace statics , return ""
        Map<String,String> staticForNothing = new  HashMap<String,String> ();
        staticForNothing.put("Read Count", "");
        staticForNothing.put("Read Latency", " ");
        staticForNothing.put("Write Count", "");
        staticForNothing.put("Write Latency"," ");
        staticForNothing.put("Pending Tasks","");
        return staticForNothing;
    }

    // 获取所有Keyspace的数据信息列表
    public ArrayList<KsStats> printColumnFamilyStats()
    {
        ArrayList<KsStats> ksStatsList = new ArrayList<KsStats>();
        Map <String, List <ColumnFamilyStoreMBean>> cfstoreMap = new HashMap <String, List <ColumnFamilyStoreMBean>>();

        // 获取ColumnFamilyStoreMbean列表
        Iterator<Map.Entry<String, ColumnFamilyStoreMBean>> cfamilies = probe.getColumnFamilyStoreMBeanProxies();

        while (cfamilies.hasNext())
        {
            Entry<String, ColumnFamilyStoreMBean> entry = cfamilies.next();
            String tableName = entry.getKey();
            ColumnFamilyStoreMBean cfsProxy = entry.getValue();

            if (!cfstoreMap.containsKey(tableName))
            {
                List<ColumnFamilyStoreMBean> columnFamilies = new ArrayList<ColumnFamilyStoreMBean>();
                columnFamilies.add(cfsProxy);
                cfstoreMap.put(tableName, columnFamilies);
            }
            else
            {
                cfstoreMap.get(tableName).add(cfsProxy);
            }
        }

        // one keyspace statistics
        for (Entry<String, List<ColumnFamilyStoreMBean>> entry : cfstoreMap.entrySet())
        {
            String tableName = entry.getKey();
            List<ColumnFamilyStoreMBean> columnFamilies = entry.getValue();
            long tableReadCount = 0;
            long tableWriteCount = 0;
            int tablePendingTasks = 0;
            double tableTotalReadTime = 0.0f;
            double tableTotalWriteTime = 0.0f;

            KsStats ksStats = new KsStats(tableName);   
            //统计一个Keyspace的数据信息，将其中所有CF的数据加和
            for (ColumnFamilyStoreMBean cfstore : columnFamilies)
            {
                long writeCount = cfstore.getWriteCount();
                long readCount = cfstore.getReadCount();

                if (readCount > 0)
                {
                    tableReadCount += readCount;
                    tableTotalReadTime += cfstore.getTotalReadLatencyMicros();
                }
                if (writeCount > 0)
                {
                    tableWriteCount += writeCount;
                    tableTotalWriteTime += cfstore.getTotalWriteLatencyMicros();
                }
                tablePendingTasks += cfstore.getPendingTasks();
            }
            double tableReadLatency = tableReadCount > 0 ? tableTotalReadTime / tableReadCount / 1000 : Double.NaN;
            double tableWriteLatency = tableWriteCount > 0 ? tableTotalWriteTime / tableWriteCount / 1000 : Double.NaN;
            Map<String,String> statics = new HashMap<String,String>();
            statics.put("Read Count", tableReadCount+"");
            statics.put("Read Latency", String.format("%s", tableReadLatency));
            statics.put("Write Count", tableWriteCount+"");
            statics.put("Write Latency", String.format("%s", tableWriteLatency));
            statics.put("Pending Tasks",tablePendingTasks+"");
            ksStats.setStatics(statics);

            //get cfstatics
            ksStats.setColumnFamilies(columnFamilies);
            ksStatsList.add(ksStats);
        }
        return ksStatsList;
    }
}
