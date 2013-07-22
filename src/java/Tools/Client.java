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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.management.MemoryUsage;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.cassandra.concurrent.IExecutorMBean;
import org.apache.cassandra.concurrent.JMXEnabledThreadPoolExecutorMBean;
import org.apache.cassandra.dht.Token;
import Node.*;
import Unit.Cell;
import Unit.ColumnFamily;
import Unit.ColumnFamilyMetaData;
import Unit.Key;
import Unit.SColumn;

import org.apache.cassandra.thrift.*;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.TException;
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
public class Client{
    public static final String DEFAULT_THRIFT_HOST = "localhost";
    public static final int DEFAULT_THRIFT_PORT = 9160;
    public static final int DEFAULT_JMX_PORT = 7199;
    private static final String UTF8 = "UTF8";

    private static final String CQL_URL = "jdbc:cassandra:/@%s:%d/%s";

    public enum ColumnType {
        SUPER("Super"),
        STANDARD("Standard");

        private String type;

        private ColumnType(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

    private TTransport transport;
    private TProtocol protocol;
    private Cassandra.Client client;
    private NodeProbe probe;

    private Connection db;
    private Statement st;

    private boolean connected = false;
    private boolean cqlConnected = false;
    private String host;
    private int thriftPort;
    private int jmxPort;

    private String keyspace;
    private String columnFamily;
    private boolean superColumn;

    public Client() {
        this(DEFAULT_THRIFT_HOST, DEFAULT_THRIFT_PORT, DEFAULT_JMX_PORT);
    }

    public Client(String host) {
        this(host, DEFAULT_THRIFT_PORT, DEFAULT_JMX_PORT);
    }

    public Client(String host, int thriftPort, int jmxPort) {
        this.host = host;
        this.thriftPort = thriftPort;
        this.jmxPort = jmxPort;
    }

    public void connect()
            throws TTransportException, IOException, InterruptedException {
        if (!connected) {
            // Updating the transport to Framed one as it has been depreciated with Cassandra 0.7.0
            transport = new TFramedTransport(new TSocket(host, thriftPort));
            protocol = new TBinaryProtocol(transport);
            client = new Cassandra.Client(protocol);
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

    public void cqlConnect(String keyspace) throws ClassNotFoundException, SQLException {
        if (!cqlConnected) {
            Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
            db = DriverManager.getConnection(String.format(CQL_URL, host, thriftPort, keyspace), new Properties());
            st = db.createStatement();
            cqlConnected = true;
        }
    }

    public void cqlDisconnect() throws SQLException {
        if (cqlConnected) {
            st.close();
            db.close();
            cqlConnected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public String describeClusterName() throws TException {
        return client.describe_cluster_name();
    }

    public String descriveVersion() throws TException {
        return client.describe_version();
    }

    public String describeSnitch() throws TException {
        return client.describe_snitch();
    }

    public Map<String, List<String>> describeSchemaVersions()
            throws InvalidRequestException, TException {
        return client.describe_schema_versions();
    }

    public String describePartitioner() throws TException {
        return client.describe_partitioner();
    }

    public List<TokenRange> describeRing(String keyspace)
            throws TException, InvalidRequestException {
        this.keyspace = keyspace;
        return client.describe_ring(keyspace);
    }
    /**
     * get node ring of cluster
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
  /*  public NodeInfo getNodeInfo(String primaryEndpoint,Token token) throws IOException, InterruptedException {
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
*/
    //JMX
    public List<Tpstats> getTpstats(String endpoint) throws IOException, InterruptedException {
        List<Tpstats> l = new ArrayList<Tpstats>();

        NodeProbe p = new NodeProbe(endpoint, jmxPort);
        Iterator<Entry<String, JMXEnabledThreadPoolExecutorMBean>> threads = p.getThreadPoolMBeanProxies();
        for (;threads.hasNext();) {
            Entry<String, JMXEnabledThreadPoolExecutorMBean> thread = threads.next();

            Tpstats tp = new Tpstats();
            tp.setPoolName(thread.getKey());

            IExecutorMBean threadPoolProxy = thread.getValue();
            tp.setActiveCount(threadPoolProxy.getActiveCount());
            tp.setPendingTasks(threadPoolProxy.getPendingTasks());
            tp.setCompletedTasks(threadPoolProxy.getCompletedTasks());
            l.add(tp);
        }

        return l;
    }

    //get Keyspaces of the cluser
    public List<KsDef> getKeyspaces()
            throws TException, InvalidRequestException {
        return client.describe_keyspaces();
    }

    //get keyspace with a given name
    public KsDef describeKeyspace(String keyspaceName)
            throws NotFoundException, InvalidRequestException, TException {
        return client.describe_keyspace(keyspaceName);
    }

    //add keyspace
	public void addKeyspace(String keyspaceName, String strategy,Map<String, String> strategyOptions, int replicationFactor)
			throws InvalidRequestException, TException,SchemaDisagreementException {
		KsDef ksDef = new KsDef();
		ksDef.setName(keyspaceName);
		ksDef.setStrategy_class(strategy);
		ksDef.setCf_defs(new LinkedList<CfDef>());

		if (strategyOptions.isEmpty()) {
			strategyOptions = new HashMap<String, String>();
			strategyOptions.put("replication_factor",String.valueOf(replicationFactor));
		}
		ksDef.setStrategy_options(strategyOptions);

		client.system_add_keyspace(ksDef);
	}


    //update keyspace with strategy, replication
    public void updateKeyspace(String keyspaceName,
                               String strategy,
                               Map<String, String> strategyOptions,
                               int replicationFactor)
            throws InvalidRequestException, TException, SchemaDisagreementException {
        KsDef ksDef = new KsDef();
        ksDef.setName(keyspaceName);
        ksDef.setStrategy_class(strategy);
        ksDef.setCf_defs(new LinkedList<CfDef>());
		if (strategyOptions.isEmpty()) {
			strategyOptions = new HashMap<String, String>();
			strategyOptions.put("replication_factor",String.valueOf(replicationFactor));
		}
		ksDef.setStrategy_options(strategyOptions);

        client.system_update_keyspace(ksDef);
    }

    //drop keyspace
    public void dropKeyspace(String keyspaceName)
            throws InvalidRequestException, SchemaDisagreementException, TException {
        client.system_drop_keyspace(keyspaceName);
    }
    //

    //add column family
    public void addColumnFamily(String keyspaceName,
                                ColumnFamily cf)
            throws InvalidRequestException, TException, SchemaDisagreementException {
        this.keyspace = keyspaceName;
        //create columnfamily cfDef with default settings
        CfDef cfDef = new CfDef(keyspaceName, cf.getColumnFamilyName());
        
        cfDef.setColumn_type(cf.getColumnType());
        //set cfDef's settings with custom input
        if (!isEmpty(cf.getComparator())) {
            cfDef.setComparator_type(cf.getComparator());
        }

        if (cf.getComparator().equals(ColumnType.SUPER)) {
            if (!isEmpty(cf.getSubcomparator())) {
                cfDef.setSubcomparator_type(cf.getSubcomparator());
            }
        }

        if (!isEmpty(cf.getComment())) {
            cfDef.setComment(cf.getComment());
        }

        if (!isEmpty(cf.getRowsCached())) {
            cfDef.setRow_cache_size(Double.valueOf(cf.getRowsCached()));
        }

        if (!isEmpty(cf.getRowCacheSavePeriod())) {
            cfDef.setRow_cache_save_period_in_seconds(Integer.valueOf(cf.getRowCacheSavePeriod()));
        }

        if (!isEmpty(cf.getKeysCached())) {
            cfDef.setKey_cache_size(Double.valueOf(cf.getKeysCached()));
        }

        if (!isEmpty(cf.getKeyCacheSavePeriod())) {
            cfDef.setKey_cache_save_period_in_seconds(Integer.valueOf(cf.getKeyCacheSavePeriod()));
        }

        if (!isEmpty(cf.getReadRepairChance())) {
            cfDef.setRead_repair_chance(Double.valueOf(cf.getReadRepairChance()));
        }

        if (!isEmpty(cf.getGcGrace())) {
            cfDef.setGc_grace_seconds(Integer.valueOf(cf.getGcGrace()));
        }

        if (!cf.getMetaDatas().isEmpty()) {
            List<ColumnDef> l = new ArrayList<ColumnDef>();
            for (ColumnFamilyMetaData metaData : cf.getMetaDatas()) {
                ColumnDef cd = new ColumnDef();
                cd.setName(metaData.getColumnName().getBytes());

                if (metaData.getValiDationClass() != null) {
                    cd.setValidation_class(metaData.getValiDationClass());
                }
                if (metaData.getIndexType() != null) {
                    cd.setIndex_type(metaData.getIndexType());
                }
                if (metaData.getIndexName() != null) {
                    cd.setIndex_name(metaData.getIndexName());
                }

                l.add(cd);
            }
            cfDef.setColumn_metadata(l);
        }
/*
        if (!isEmpty(cf.getMemtableOperations())) {
            cfDef.setMemtable_operations_in_millions(Double.valueOf(cf.getMemtableOperations()));
        }

        if (!isEmpty(cf.getMemtableThroughput())) {
            cfDef.setMemtable_throughput_in_mb(Integer.valueOf(cf.getMemtableThroughput()));
        }

        if (!isEmpty(cf.getMemtableFlushAfter())) {
            cfDef.setMemtable_flush_after_mins(Integer.valueOf(cf.getMemtableFlushAfter()));
        }
*/
        if (!isEmpty(cf.getDefaultValidationClass())) {
            cfDef.setDefault_validation_class(cf.getDefaultValidationClass());
        }

        if (!isEmpty(cf.getMinCompactionThreshold())) {
            cfDef.setMin_compaction_threshold(Integer.valueOf(cf.getMinCompactionThreshold()));
        }

        if (!isEmpty(cf.getMaxCompactionThreshold())) {
            cfDef.setMax_compaction_threshold(Integer.valueOf(cf.getMaxCompactionThreshold()));
        }

        client.set_keyspace(keyspaceName);
        client.system_add_column_family(cfDef);
    }

    //update column family
    public void updateColumnFamily(String keyspaceName,
                                   ColumnFamily cf)
            throws InvalidRequestException, TException, SchemaDisagreementException {
        this.keyspace = keyspaceName;
        CfDef cfDef = new CfDef(keyspaceName, cf.getColumnFamilyName());
        cfDef.setId(cf.getId());
        cfDef.setColumn_type(cf.getColumnType());

        if (!isEmpty(cf.getComparator())) {
            cfDef.setComparator_type(cf.getComparator());
        }

        if (cf.getComparator().equals(ColumnType.SUPER)) {
            if (!isEmpty(cf.getSubcomparator())) {
                cfDef.setSubcomparator_type(cf.getSubcomparator());
            }
        }

        if (!isEmpty(cf.getComment())) {
            cfDef.setComment(cf.getComment());
        }

        if (!isEmpty(cf.getRowsCached())) {
            cfDef.setRow_cache_size(Double.valueOf(cf.getRowsCached()));
        }

        if (!isEmpty(cf.getRowCacheSavePeriod())) {
            cfDef.setRow_cache_save_period_in_seconds(Integer.valueOf(cf.getRowCacheSavePeriod()));
        }

        if (!isEmpty(cf.getKeysCached())) {
            cfDef.setKey_cache_size(Double.valueOf(cf.getKeysCached()));
        }

        if (!isEmpty(cf.getKeyCacheSavePeriod())) {
            cfDef.setKey_cache_save_period_in_seconds(Integer.valueOf(cf.getKeyCacheSavePeriod()));
        }

        if (!isEmpty(cf.getReadRepairChance())) {
            cfDef.setRead_repair_chance(Double.valueOf(cf.getReadRepairChance()));
        }

        if (!isEmpty(cf.getGcGrace())) {
            cfDef.setGc_grace_seconds(Integer.valueOf(cf.getGcGrace()));
        }

        if (!cf.getMetaDatas().isEmpty()) {
            List<ColumnDef> l = new ArrayList<ColumnDef>();
            for (ColumnFamilyMetaData metaData : cf.getMetaDatas()) {
                ColumnDef cd = new ColumnDef();
                cd.setName(metaData.getColumnName().getBytes());

                if (metaData.getValiDationClass() != null) {
                    cd.setValidation_class(metaData.getValiDationClass());
                }
                if (metaData.getIndexType() != null) {
                    cd.setIndex_type(metaData.getIndexType());
                }
                if (metaData.getIndexName() != null) {
                    cd.setIndex_name(metaData.getIndexName());
                }

                l.add(cd);
            }
            cfDef.setColumn_metadata(l);
        }
/*
        if (!isEmpty(cf.getMemtableOperations())) {
            cfDef.setMemtable_operations_in_millions(Double.valueOf(cf.getMemtableOperations()));
        }

        if (!isEmpty(cf.getMemtableThroughput())) {
            cfDef.setMemtable_throughput_in_mb(Integer.valueOf(cf.getMemtableThroughput()));
        }

        if (!isEmpty(cf.getMemtableFlushAfter())) {
            cfDef.setMemtable_flush_after_mins(Integer.valueOf(cf.getMemtableFlushAfter()));
        }
*/
        if (!isEmpty(cf.getDefaultValidationClass())) {
            cfDef.setDefault_validation_class(cf.getDefaultValidationClass());
        }

        if (!isEmpty(cf.getMinCompactionThreshold())) {
            cfDef.setMin_compaction_threshold(Integer.valueOf(cf.getMinCompactionThreshold()));
        }

        if (!isEmpty(cf.getMaxCompactionThreshold())) {
            cfDef.setMax_compaction_threshold(Integer.valueOf(cf.getMaxCompactionThreshold()));
        }

        client.set_keyspace(keyspaceName);
        client.system_update_column_family(cfDef);
    }

    //drop column family
    public void dropColumnFamily(String keyspaceName, String columnFamilyName)
            throws InvalidRequestException, TException, SchemaDisagreementException {
        this.keyspace = keyspaceName;
        client.set_keyspace(keyspaceName);
        client.system_drop_column_family(columnFamilyName);
    }

    //truncate column family
    public void truncateColumnFamily(String keyspaceName, String columnFamilyName)
            throws InvalidRequestException, TException, UnavailableException {
        this.keyspace = keyspaceName;
        this.columnFamily = columnFamilyName;
        client.set_keyspace(keyspaceName);
        client.truncate(columnFamilyName);
    }

    /**
     *
     * Retrieve Column metadata from a given keyspace
     *
     * @param keyspace
     * @param columnFamily
     * @return
     * @throws NotFoundException
     * @throws TException
     * @throws InvalidRequestException
     */
    public Map<String, String> getColumnFamily(String keyspace, String columnFamily)
            throws NotFoundException, TException, InvalidRequestException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        for (Iterator<CfDef> cfIterator = client.describe_keyspace(keyspace).getCf_defsIterator(); cfIterator.hasNext();) {
            CfDef next = cfIterator.next();
            if (columnFamily.equalsIgnoreCase(next.getName())) {
                Map<String, String> columnMetadata = new HashMap<String, String>();

                CfDef._Fields[] fields = CfDef._Fields.values();

                for (int i = 0; i < fields.length; i++) {
                    CfDef._Fields field = fields[i];
                    // using string concat to avoin NPE, if the value is not null
                    // need to find an elegant solution
                    columnMetadata.put(field.name(), next.getFieldValue(field)+"");
                }

                return columnMetadata;
            }
        }
        System.out.println("returning null");
        return null;
    }

    /**
    *
    * get column family from a given keyspace and column family name
    *
    * @param keyspace
    * @param columnFamily
    * @return
    * @throws NotFoundException
    * @throws TException
    * @throws InvalidRequestException
    * @throws UnsupportedEncodingException
    */
    public ColumnFamily getColumnFamilyBean(String keyspace, String columnFamily)
            throws NotFoundException, TException, InvalidRequestException, UnsupportedEncodingException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        for (Iterator<CfDef> cfIterator = client.describe_keyspace(keyspace).getCf_defsIterator(); cfIterator.hasNext();) {
            CfDef cd = cfIterator.next();
            if (columnFamily.equalsIgnoreCase(cd.getName())) {
                ColumnFamily cf = new ColumnFamily();
                cf.setId(cd.getId());
                cf.setColumnFamilyName(cd.getName());
                cf.setColumnType(cd.getColumn_type());
                cf.setComparator(cd.getComparator_type());
                cf.setSubcomparator(cd.getSubcomparator_type());
                cf.setComment(cd.getComment());
                cf.setRowsCached(String.valueOf(cd.getRow_cache_size()));
                cf.setRowCacheSavePeriod(String.valueOf(cd.getRow_cache_save_period_in_seconds()));
                cf.setKeysCached(String.valueOf(cd.getKey_cache_size()));
                cf.setKeyCacheSavePeriod(String.valueOf(cd.getKey_cache_save_period_in_seconds()));
                cf.setReadRepairChance(String.valueOf(cd.getRead_repair_chance()));
                cf.setGcGrace(String.valueOf(cd.getGc_grace_seconds()));
//                cf.setMemtableOperations(String.valueOf(cd.getMemtable_operations_in_millions()));
//                cf.setMemtableThroughput(String.valueOf(cd.getMemtable_throughput_in_mb()));
//                cf.setMemtableFlushAfter(String.valueOf(cd.getMemtable_flush_after_mins()));
                cf.setDefaultValidationClass(cd.getDefault_validation_class());
                cf.setMinCompactionThreshold(String.valueOf(cd.getMin_compaction_threshold()));
                cf.setMaxCompactionThreshold(String.valueOf(cd.getMax_compaction_threshold()));
                for (ColumnDef cdef : cd.getColumn_metadata()) {
                    ColumnFamilyMetaData cfmd = new ColumnFamilyMetaData();
                    cfmd.setColumnName(new String(cdef.getName(), UTF8));
                    cfmd.setValiDationClass(cdef.getValidation_class());
                    cfmd.setIndexType(cdef.getIndex_type());
                    cfmd.setIndexName(cdef.getIndex_name());
                    cf.getMetaDatas().add(cfmd);
                }

                return cf;
            }
        }

        System.out.println("returning null");
        return null;
    }
    /**
    *
    * get column familys name from a given keyspace 
    *
    * @param keyspace
    * @return s
    * @throws NotFoundException
    * @throws TException
    * @throws InvalidRequestException
    */
    public Set<String> getColumnFamilys(String keyspace)
            throws NotFoundException, TException, InvalidRequestException {
        this.keyspace = keyspace;

        Set<String> s = new TreeSet<String>();

        for (Iterator<CfDef> cfIterator = client.describe_keyspace(keyspace).getCf_defsIterator(); cfIterator.hasNext();) {
           CfDef next =  cfIterator.next();
           s.add(next.getName());
        }
        return s;
    }

    /**
    *
    * count how many columns foe a row in the given column family
    *
    * @param keyspace
    * @param columnFamily
    * @param key
    * @return client.get_count(ByteBuffer.wrap(key.getBytes()), colParent, null, ConsistencyLevel.ONE)
    * @throws TimedOutException
    * @throws TException
    * @throws InvalidRequestException
    * @throws UnavailableException
    */
    public int countColumnsRecord(String keyspace, String columnFamily, String key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnParent colParent = new ColumnParent(columnFamily);
        //TODO - Verify if its working fine
        return client.get_count(ByteBuffer.wrap(key.getBytes()), colParent, null, ConsistencyLevel.ONE);
    }

    public int countSuperColumnsRecord(String keyspace, String columnFamily, String superColumn, String key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnParent colParent = new ColumnParent(columnFamily);
        colParent.setSuper_column(superColumn.getBytes());
        // TODO - verify if its working fine
        return client.get_count(ByteBuffer.wrap(key.getBytes()), colParent, null, ConsistencyLevel.ONE);
    }
    
    /**
     * insert a key, column ,value
     * */
    public Date insertColumn(String keyspace,
                             String columnFamily,
                             String key,
                             String superColumn,
                             String column,
                             String value)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnParent parent;

    /*    if(superColumn == null) {
           parent = new ColumnParent(columnFamily);
        } else {
            parent = new ColumnParent(superColumn);
        }*/
        if(superColumn == null) {
            parent = new ColumnParent(columnFamily);
         } else {
             parent = new ColumnParent(columnFamily);
             parent.setSuper_column(ByteBuffer.wrap(superColumn.getBytes()));
         }
        

        long timestamp = System.currentTimeMillis() * 1000;
        Column col = new Column();
        col.setName(column.getBytes(UTF8));
        col.setValue(value.getBytes(UTF8));
        col.setTimestamp(timestamp);

        client.set_keyspace(keyspace);
        client.insert(ByteBuffer.wrap(key.getBytes()), parent, col, ConsistencyLevel.ONE);

        return new Date(timestamp / 1000);
    }


    public void removeKey(String keyspace, String columnFamily, String key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnPath colPath = new ColumnPath(columnFamily);
        long timestamp = System.currentTimeMillis() * 1000;

        client.set_keyspace(keyspace);
        client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp, ConsistencyLevel.ONE);
    }
    

    public void removeSuperColumn(String keyspace, String columnFamily, String key, String superColumn)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        ColumnPath colPath = new ColumnPath(columnFamily);
        colPath.setSuper_column(superColumn.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;

        client.set_keyspace(keyspace);
        client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp, ConsistencyLevel.ONE);
    }

    public void removeColumn(String keyspace, String columnFamily, String key, String column)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnPath colPath = new ColumnPath(columnFamily);
        colPath.setColumn(column.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;

        client.set_keyspace(keyspace);
        client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp, ConsistencyLevel.ONE);
    }

    public void removeColumn(String keyspace, String columnFamily, String key, String superColumn, String column)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnPath colPath = new ColumnPath(columnFamily);
        colPath.setSuper_column(superColumn.getBytes());
        colPath.setColumn(column.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;

        client.set_keyspace(keyspace);
        client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp, ConsistencyLevel.ONE);
    }
    
    

    public Map<String, Key> getKey(String keyspace, String columnFamily, String superColumn, String key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        Map<String, Key> m = new TreeMap<String, Key>();

        ColumnParent columnParent = new ColumnParent(columnFamily);
        if (superColumn != null) {
            columnParent.setSuper_column(superColumn.getBytes());
        }

        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(new byte[0]);
        sliceRange.setFinish(new byte[0]);

        SlicePredicate slicePredicate = new SlicePredicate();
        slicePredicate.setSlice_range(sliceRange);

        List<ColumnOrSuperColumn> l = null;
        try {
            l = client.get_slice(ByteBuffer.wrap(key.getBytes()), columnParent, slicePredicate, ConsistencyLevel.ONE);
        } catch (Exception e) {
            return m;
        }

        Key k = new Key(key, new TreeMap<String, SColumn>(), new TreeMap<String, Cell>());
        for (ColumnOrSuperColumn column : l) {
            k.setSuperColumn(column.isSetSuper_column());
            if (column.isSetSuper_column()) {
                SuperColumn scol = column.getSuper_column();
                SColumn s = new SColumn(k, new String(scol.getName(), UTF8), new TreeMap<String, Cell>());
                for (Column col : scol.getColumns()) {
                    Cell c = new Cell(s,
                                      new String(col.getName(), UTF8),
                                      new String(col.getValue(), UTF8),
                                      new Date(col.getTimestamp() / 1000));
                    s.getCells().put(c.getName(), c);
                }

                k.getSColumns().put(s.getName(), s);
            } else {
                Column col = column.getColumn();
                Cell c = new Cell(k,
                                  new String(col.getName(), UTF8),
                                  new String(col.getValue(), UTF8),
                                  new Date(col.getTimestamp() / 1000));
                k.getCells().put(c.getName(), c);
            }

            m.put(k.getName(), k);
        }

        return m;
    }

    public Map<String, Key> listKeyAndValues(String keyspace, String columnFamily, String startKey, String endKey, int rows)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        Map<String, Key> m = new TreeMap<String, Key>();

        ColumnParent columnParent = new ColumnParent(columnFamily);

        KeyRange keyRange = new KeyRange(rows);
        keyRange.setStart_key(ByteBuffer.wrap(startKey.getBytes()));
        keyRange.setEnd_key(ByteBuffer.wrap(endKey.getBytes()));

        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(new byte[0]);
        sliceRange.setFinish(new byte[0]);

        SlicePredicate slicePredicate = new SlicePredicate();
        slicePredicate.setSlice_range(sliceRange);
        client.set_keyspace(keyspace);

        List<KeySlice> keySlices = null;
        try {
            keySlices = client.get_range_slices(columnParent, slicePredicate, keyRange, ConsistencyLevel.ONE);
        } catch (UnavailableException e) {
            return m;
        }

        for (KeySlice keySlice : keySlices) {
            Key key = new Key(new String(keySlice.getKey()), new TreeMap<String, SColumn>(), new TreeMap<String, Cell>());

            for (ColumnOrSuperColumn column : keySlice.getColumns()) {
                key.setSuperColumn(column.isSetSuper_column());
                if (column.isSetSuper_column()) {
                    SuperColumn scol = column.getSuper_column();
                    SColumn s = new SColumn(key, new String(scol.getName(), UTF8), new TreeMap<String, Cell>());
                    for (Column col : scol.getColumns()) {
                        Cell c = new Cell(s,
                                          new String(col.getName(), UTF8),
                                          new String(col.getValue(), UTF8),
                                          new Date(col.getTimestamp() / 1000));
                        s.getCells().put(c.getName(), c);
                    }

                    key.getSColumns().put(s.getName(), s);
                } else {
                    Column col = column.getColumn();
                    Cell c = new Cell(key,
                                      new String(col.getName(), UTF8),
                                      new String(col.getValue(), UTF8),
                                      new Date(col.getTimestamp() / 1000));
                    key.getCells().put(c.getName(), c);
                }
            }

            m.put(key.getName(), key);
        }

        return m;
    }

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * @return the keyspace
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * @param keyspace the keyspace to set
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    /**
     * @return the columnFamily
     */
    public String getColumnFamily() {
        return columnFamily;
    }

    /**
     * @param columnFamily the columnFamily to set
     */
    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    /**
     * @return the superColumn
     */
    public boolean isSuperColumn() {
        return superColumn;
    }

    /**
     * @param superColumn the superColumn to set
     */
    public void setSuperColumn(boolean superColumn) {
        this.superColumn = superColumn;
    }

    /**
     * @return the strategyMap
     */
    public static Map<String, String> getStrategyMap() {
        Map<String, String> strategyMap = new TreeMap<String, String>();
        strategyMap.put("SimpleStrategy", "org.apache.cassandra.locator.SimpleStrategy");
        strategyMap.put("LocalStrategy", "org.apache.cassandra.locator.LocalStrategy");
        strategyMap.put("NetworkTopologyStrategy", "org.apache.cassandra.locator.NetworkTopologyStrategy");
        strategyMap.put("OldNetworkTopologyStrategy", "org.apache.cassandra.locator.OldNetworkTopologyStrategy");
        return strategyMap;
    }

    public static Map<String, String> getComparatorTypeMap() {
        Map<String, String> comparatorMap = new TreeMap<String, String>();
        comparatorMap.put("org.apache.cassandra.db.marshal.AsciiType", "AsciiType");
        comparatorMap.put("org.apache.cassandra.db.marshal.BytesType", "BytesType");
        comparatorMap.put("org.apache.cassandra.db.marshal.LexicalUUIDType", "LexicalUUIDType");
        comparatorMap.put("org.apache.cassandra.db.marshal.LongType", "LongType");
        comparatorMap.put("org.apache.cassandra.db.marshal.TimeUUIDType", "TimeUUIDType");
        comparatorMap.put("org.apache.cassandra.db.marshal.UTF8Type", "UTF8Type");

        return comparatorMap;
    }

    public static Map<String, String> getValidationClassMap() {
        Map<String, String> validationClassMap = new TreeMap<String, String>();
        validationClassMap.put("org.apache.cassandra.db.marshal.AsciiType", "AsciiType");
        validationClassMap.put("org.apache.cassandra.db.marshal.BytesType", "BytesType");
        validationClassMap.put("org.apache.cassandra.db.marshal.IntegerType", "IntegerType");
        validationClassMap.put("org.apache.cassandra.db.marshal.LongType", "LongType");
        validationClassMap.put("org.apache.cassandra.db.marshal.TimeUUIDType", "TimeUUIDType");
        validationClassMap.put("org.apache.cassandra.db.marshal.UTF8Type", "UTF8Type");

        return validationClassMap;
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
