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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.db.ColumnFamilyStoreMBean;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.cassandra.tools.NodeCmd;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import Node.NodeInfo;
import Node.RingNode;

/**
 * Contain Cluster Manager function ,such as compression, show ring, join and so on
 * use NodeProbe
 * @author 林丹
 * */
public class ClusterManager {
	 private NodeProbe probe;
	 private int jmxPort;
	 private Client client;
	 /**
	  * Class Constructor
	  * @param probe
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws TTransportException 
	  * */
	 public ClusterManager(Client client, int jmxPort) throws TTransportException, IOException, InterruptedException
	 {
		 this.client =  client;
		 this.jmxPort = jmxPort;
		 if(!client.isConnected())
		 {
			 client.connect();
		 }
		 this.probe =  client.getProbe();
		 
	 }
	 
	/**
	 * get cluster ringNode
	 * @return r cluster ring
	 * */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RingNode listRing() {
		RingNode r = new RingNode();
		r.setRangeMap(probe.getTokenToEndpointMap());
		List<Token> ranges = new ArrayList<Token>(r.getRangeMap().keySet());
		Collections.sort(ranges);
		r.setRanges(ranges);
		return r;
	}
	

    /**
     * get nodeinfo with given Endpoint
     * */
    public NodeInfo getNodeInfo(String primaryEndpoint,Token token) throws IOException, InterruptedException {
        NodeProbe probe = new NodeProbe(primaryEndpoint, jmxPort);
        Collection<String> liveNodes = probe.getLiveNodes();
        Collection<String> deadNodes = probe.getUnreachableNodes();
        Collection<String> joiningNodes = probe.getJoiningNodes();
        Collection<String> leavingNodes = probe.getLeavingNodes();
        Collection<String> movingNodes = probe.getMovingNodes();
        Map<String, String> loadMap = probe.getLoadMap();
        Map<Token, Float> ownerships = probe.getOwnership();

        
        NodeInfo ni = new NodeInfo();
        ni.setEndpoint(primaryEndpoint);
        ni.setUptime(probe.getUptime() / 1000);
        ni.setRange(probe.getToken());
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
    
    /**
     * join ring, command on a node
     * @throws ConfigurationException 
     * @throws IOException 
     * */
    public void join() throws IOException, ConfigurationException
    {
    	 if (probe.isJoined())
         {
             System.err.println("This node has already joined the ring.");
             System.exit(1);
         }
         probe.joinRing();
    }
    
    
//    /**
//	 * some options on columnFamily
//	 * 
//	 * */
//	public void optionalCFs(String keyspace, String[] columnFamilies,NodeCommand nc) throws InterruptedException, IOException
//    {
//    	
//    	List<String> keyspaces = probe.getKeyspaces();
//    	//check if keyspace is valid
//    	if (!probe.getKeyspaces().contains(keyspace))
//    	{
//    		System.err.println("Keyspace [" + keyspace + "] does not exist.");
//            System.exit(1);
//        }
//    	switch (nc)
//            {
//                case REPAIR  :
//               /*     if (cmd.hasOption(PRIMARY_RANGE_OPT.left))
//                        probe.forceTableRepairPrimaryRange(keyspace, columnFamilies);
//                    else*/
//                        probe.forceTableRepair(keyspace, columnFamilies);
//                    break;
//                case INVALIDATEKEYCACHE : probe.invalidateKeyCaches(keyspace, columnFamilies); break;
//                case INVALIDATEROWCACHE : probe.invalidateRowCaches(keyspace, columnFamilies); break;
//                case FLUSH   :
//                    try { probe.forceTableFlush(keyspace, columnFamilies); }
//                    catch (ExecutionException ee) { err(ee, "Error occured during flushing"); }
//                    break;
//                case COMPACT :
//                    try { probe.forceTableCompaction(keyspace, columnFamilies); }
//                    catch (ExecutionException ee) { err(ee, "Error occured during compaction"); }
//                    break;
//                case CLEANUP :
//                    if (keyspace.equals("system")) { break; } // Skip cleanup on system cfs.
//                    try { probe.forceTableCleanup(keyspace, columnFamilies); }
//                    catch (ExecutionException ee) { err(ee, "Error occured during cleanup"); }
//                    break;
//                case SCRUB :
//                    try { probe.scrub(keyspace, columnFamilies); }
//                    catch (ExecutionException ee) { err(ee, "Error occured while scrubbing keyspace " + keyspace); }
//                    break;
//                    //for cassandra 1.0
//          /*      case UPGRADESSTABLES :
//                	 try { probe.upgradeSSTables(keyspace, columnFamilies); }
//                     catch (ExecutionException ee) { err(ee, "Error occured while upgrading the sstables for keyspace " + keyspace); }
//                     break;*/
//                default:
//                    throw new RuntimeException("Unreachable code.");
//            }
//        }
//	
//	  /**
//		 * some options on keyspaces
//	 * @throws InvalidRequestException 
//	 * @throws TException 
//	 * @throws NotFoundException 
//	 * @throws IOException 
//	 * @throws InterruptedException 
//		 * 
//		 * */
//	public void optionalKSs(List<String> keyspaces, NodeCommand nc) throws NotFoundException, TException, InvalidRequestException, InterruptedException, IOException 
//	{
//		if(keyspaces!=null)
//		{
//			for(String keyspace:keyspaces)
//			{
//				Set<String> columnFamilySet = client.getColumnFamilys(keyspace);
//				String[] columnFamilies = columnFamilySet.toArray(new String[0]);
//				
//				optionalCFs(keyspace, columnFamilies,nc);
//			}
//		}
//	}
	
    private void err(ExecutionException ee, String string) {
		System.out.println(ee);
		System.out.println(string);
		
    }
	 
	public NodeProbe getProbe() {
		return probe;
	}

	public void setProbe(NodeProbe probe) {
		this.probe = probe;
	}
	public int getJmxPort()
	{
		return jmxPort;
	}
	public void setJmxPort(int jmxPort){
		this.jmxPort = jmxPort;
	}
	
	 

}
