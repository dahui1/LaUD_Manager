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
import java.io.PrintStream;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.cache.InstrumentingCacheMBean;
import org.apache.cassandra.concurrent.JMXEnabledThreadPoolExecutorMBean;
import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.db.ColumnFamilyStoreMBean;
import org.apache.cassandra.db.compaction.CompactionInfo;
import org.apache.cassandra.db.compaction.CompactionManagerMBean;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.net.MessagingServiceMBean;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.cassandra.tools.NodeCmd;
import org.apache.cassandra.utils.EstimatedHistogram;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.google.common.collect.Maps;

import Node.NodeInfo;
import Node.RingNode;
import Unit.CfStats;
import Unit.KsStats;

/**
 * Contain Cluster Manager function ,such as compression, show ring, join and so on
 * use NodeProbe
 * @author 林丹
 * */
public class ClusterManager {
	 private NodeProbe probe;
	 //wille be changed latter, to be setted i xml
//	 private int jmxPort =7199;
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

	 /**
	  * Class Constructor
	  * @param probe
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws TTransportException 
	  * */
	 public ClusterManager(ClusterConnection clusterConnection, String host,int jmxPort ) throws TTransportException, IOException, InterruptedException
	 {
		 if(!clusterConnection.isConnected())
		 {
			 clusterConnection.connect();
		 }
		 this.client = clusterConnection.getClient();
		 this.probe =   new NodeProbe(host, jmxPort);
		 
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
	
	public boolean isRunning()
	{
		return probe.isThriftServerRunning();
	}
	

    /**
     * get nodeinfo with given Endpoint
     * */
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
    
    public int ringSize()
    {
    	return listRing().getRanges().size();
    }
    
/**
 * get node Info list on the ring
 * @throws InterruptedException 
 * @throws IOException 
 * */    

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
    
    
    /**
	 * some options on columnFamily
	 * 
	 * */
	public void optionalCFs(String keyspace, String[] columnFamilies,NodeCommand nc) throws InterruptedException, IOException
    {
    	
    	List<String> keyspaces = probe.getKeyspaces();
    	//check if keyspace is valid
    	if (!probe.getKeyspaces().contains(keyspace))
    	{
    		System.err.println("Keyspace [" + keyspace + "] does not exist.");
            System.exit(1);
        }
    	switch (nc)
            {
                case REPAIR  :
               /*     if (cmd.hasOption(PRIMARY_RANGE_OPT.left))
                        probe.forceTableRepairPrimaryRange(keyspace, columnFamilies);
                    else*/
                        probe.forceTableRepair(keyspace, columnFamilies);
                    break;
                case INVALIDATEKEYCACHE : probe.invalidateKeyCaches(keyspace, columnFamilies); break;
                case INVALIDATEROWCACHE : probe.invalidateRowCaches(keyspace, columnFamilies); break;
                case FLUSH   :
                    try { probe.forceTableFlush(keyspace, columnFamilies); }
                    catch (ExecutionException ee) { err(ee, "Error occured during flushing"); }
                    break;
                case COMPACT :
                    try { probe.forceTableCompaction(keyspace, columnFamilies); }
                    catch (ExecutionException ee) { err(ee, "Error occured during compaction"); }
                    break;
                case CLEANUP :
                    if (keyspace.equals("system")) { break; } // Skip cleanup on system cfs.
                    try { probe.forceTableCleanup(keyspace, columnFamilies); }
                    catch (ExecutionException ee) { err(ee, "Error occured during cleanup"); }
                    break;
                case SCRUB :
                    try { probe.scrub(keyspace, columnFamilies); }
                    catch (ExecutionException ee) { err(ee, "Error occured while scrubbing keyspace " + keyspace); }
                    break;
                default:
                    throw new RuntimeException("Unreachable code.");
            }
        }
	
	  /**
		 * some options on keyspaces
	 * @throws InvalidRequestException 
	 * @throws TException 
	 * @throws NotFoundException 
	 * @throws IOException 
	 * @throws InterruptedException 
		 * 
		 * */
	public void optionalKSs(List<String> keyspaces, NodeCommand nc) throws NotFoundException, TException, InvalidRequestException, InterruptedException, IOException 
	{
		if(keyspaces!=null)
		{
			for(String keyspace:keyspaces)
			{
				Set<String> s = new TreeSet<String>();
				for (Iterator<CfDef> cfIterator = client.describe_keyspace(keyspace)
						.getCf_defsIterator(); cfIterator.hasNext();) {
					CfDef next = cfIterator.next();
					s.add(next.getName());
				}
				String[] columnFamilies = s.toArray(new String[0]);
				
				optionalCFs(keyspace, columnFamilies,nc);
			}
		}
	}
	
	/**
	 * move token
	 * @param newToken
	 * @throws ConfigurationException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	public void move(String newToken) throws IOException, InterruptedException, ConfigurationException
	{
		probe.move(newToken);
	
	}
	
	/**
	 * drain
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	public void drain() throws IOException, InterruptedException, ExecutionException
	{
		probe.drain();
	}
	/**
	 * decommission
	 * @throws InterruptedException 
	 * */
	public void decommission() throws InterruptedException
	{
		probe.decommission();
	}
	/**
	 * gc, java will decide if to do gc based on the memory state 
	 * */
	public void gc()
	{
		System.gc();
	}
	/**
	 * close cassandra node
	 * */
	public void close()
	{
		try {
			probe.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
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
	
/*	public int getJmxPort()
	{
		return jmxPort;
	}
	public void setJmxPort(int jmxPort){
		this.jmxPort = jmxPort;
	}
*/
	/*print a columnFamily estimate row number */
	public int getEstimateRowNumber(String keyspace,String columnFamily)
	{
		int number=-1;
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
						number = (int) cfstore.estimateKeys();
					}
	            }
			}
		}
		return number;
	}
	
	/*get column family statics for one column*/
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
	/*get keyspace statics for one keyspace*/
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

	/*print cfstates*/
	 public ArrayList<KsStats> printColumnFamilyStats()
	    {
		    ArrayList<KsStats> ksStatsList = new ArrayList<KsStats>();
	        Map <String, List <ColumnFamilyStoreMBean>> cfstoreMap = new HashMap <String, List <ColumnFamilyStoreMBean>>();

	        // get a list of column family stores
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
	            //get statics for keyspace
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
	    
	/*
	public String printReleaseVersion()
    {
        return  probe.getReleaseVersion();
    }
    public void printNetworkStats(final InetAddress addr, PrintStream outs)
    {
        outs.printf("Mode: %s%n", probe.getOperationMode());
        Set<InetAddress> hosts = addr == null ? probe.getStreamDestinations() : new HashSet<InetAddress>(){{add(addr);}};
        if (hosts.size() == 0)
            outs.println("Not sending any streams.");
        for (InetAddress host : hosts)
        {
            try
            {
                List<String> files = probe.getFilesDestinedFor(host);
                if (files.size() > 0)
                {
                    outs.printf("Streaming to: %s%n", host);
                    for (String file : files)
                        outs.printf("   %s%n", file);
                }
                else
                {
                    outs.printf(" Nothing streaming to %s%n", host);
                }
            }
            catch (IOException ex)
            {
                outs.printf("   Error retrieving file data for %s%n", host);
            }
        }

        hosts = addr == null ? probe.getStreamSources() : new HashSet<InetAddress>(){{add(addr); }};
        if (hosts.size() == 0)
            outs.println("Not receiving any streams.");
        for (InetAddress host : hosts)
        {
            try
            {
                List<String> files = probe.getIncomingFiles(host);
                if (files.size() > 0)
                {
                    outs.printf("Streaming from: %s%n", host);
                    for (String file : files)
                        outs.printf("   %s%n", file);
                }
                else
                {
                    outs.printf(" Nothing streaming from %s%n", host);
                }
            }
            catch (IOException ex)
            {
                outs.printf("   Error retrieving file data for %s%n", host);
            }
        }

        MessagingServiceMBean ms = probe.msProxy;
        outs.printf("%-25s", "Pool Name");
        outs.printf("%10s", "Active");
        outs.printf("%10s", "Pending");
        outs.printf("%15s%n", "Completed");

        int pending;
        long completed;

        pending = 0;
        for (int n : ms.getCommandPendingTasks().values())
            pending += n;
        completed = 0;
        for (long n : ms.getCommandCompletedTasks().values())
            completed += n;
        outs.printf("%-25s%10s%10s%15s%n", "Commands", "n/a", pending, completed);

        pending = 0;
        for (int n : ms.getResponsePendingTasks().values())
            pending += n;
        completed = 0;
        for (long n : ms.getResponseCompletedTasks().values())
            completed += n;
        outs.printf("%-25s%10s%10s%15s%n", "Responses", "n/a", pending, completed);
    }

    public void printCompactionStats(PrintStream outs)
    {
        CompactionManagerMBean cm = probe.getCompactionManagerProxy();
        outs.println("pending tasks: " + cm.getPendingTasks());
        if (cm.getCompactions().size() > 0)
            outs.printf("%25s%16s%16s%16s%16s%10s%n", "compaction type", "keyspace", "column family", "bytes compacted", "bytes total", "progress");
        for (CompactionInfo c : cm.getCompactions())
        {
            String percentComplete = c.getTotalBytes() == 0
                                   ? "n/a"
                                   : new DecimalFormat("0.00").format((double) c.getBytesComplete() / c.getTotalBytes() * 100) + "%";
            outs.printf("%25s%16s%16s%16s%16s%10s%n", c.getTaskType(), c.getKeyspace(), c.getColumnFamily(), c.getBytesComplete(), c.getTotalBytes(), percentComplete);
        }
    }

   
    public void printRemovalStatus(PrintStream outs)
    {
        outs.println("RemovalStatus: " + probe.getRemovalStatus());
    }

    private void printCfHistograms(String keySpace, String columnFamily, PrintStream output)
    {
        ColumnFamilyStoreMBean store = this.probe.getCfsProxy(keySpace, columnFamily);

        // default is 90 offsets
        long[] offsets = new EstimatedHistogram().getBucketOffsets();

        long[] rrlh = store.getRecentReadLatencyHistogramMicros();
        long[] rwlh = store.getRecentWriteLatencyHistogramMicros();
        long[] sprh = store.getRecentSSTablesPerReadHistogram();
        long[] ersh = store.getEstimatedRowSizeHistogram();
        long[] ecch = store.getEstimatedColumnCountHistogram();

        output.println(String.format("%s/%s histograms", keySpace, columnFamily));

        output.println(String.format("%-10s%10s%18s%18s%18s%18s",
                                     "Offset", "SSTables", "Write Latency", "Read Latency", "Row Size", "Column Count"));

        for (int i = 0; i < offsets.length; i++)
        {
            output.println(String.format("%-10d%10s%18s%18s%18s%18s",
                                         offsets[i],
                                         (i < sprh.length ? sprh[i] : ""),
                                         (i < rwlh.length ? rwlh[i] : ""),
                                         (i < rrlh.length ? rrlh[i] : ""),
                                         (i < ersh.length ? ersh[i] : ""),
                                         (i < ecch.length ? ecch[i] : "")));
        }
    }

    private void printEndPoints(String keySpace, String cf, String key, PrintStream output)
    {
        List<InetAddress> endpoints = this.probe.getEndpoints(keySpace, cf, key);

        for (InetAddress anEndpoint : endpoints)
        {
           output.println(anEndpoint.getHostAddress());
        }
    }

    private void printIsThriftServerRunning(PrintStream outs)
    {
        outs.println(probe.isThriftServerRunning() ? "running" : "not running");
    }
*/
}
