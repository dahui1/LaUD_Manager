package Tools;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.management.MemoryUsage;
import java.math.BigInteger;
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
import Node.*;
import Unit.Cell;
import Unit.ColumnFamily;
import Unit.ColumnFamilyMetaData;
import Unit.Key;
import Unit.SColumn;

import org.apache.cassandra.thrift.*;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class DataManager{
    Client client;
	ClusterConnection connection;
	private String keyspace;
	private String columnFamily;
	private boolean superColumn;
	private static final String UTF8 = "UTF8";


	/**
	 * constructor
	 * */
	public DataManager(ClusterConnection clusterConnection) {
		this.connection = clusterConnection;
		this.client = clusterConnection.getClient();
		
	}
	public void diconnect()
	{
		this.connection.disconnect();
	}

	public enum ColumnType {
		SUPER("Super"), STANDARD("Standard");

		private String type;

		private ColumnType(String type) {
			this.type = type;
		}

		public String toString() {
			return type;
		}
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

	public List<TokenRange> describeRing(String keyspace) throws TException,
			InvalidRequestException {
		this.keyspace = keyspace;
		return client.describe_ring(keyspace);
	}

	// JMX
	public Tpstats getTpstats(String endpoint, String tpname) throws IOException,
			InterruptedException {
                if (tpname == null)
                    return null;
		NodeProbe p = new NodeProbe(endpoint,connection.getJmxPort());
		Iterator<Entry<String, JMXEnabledThreadPoolExecutorMBean>> threads = p
				.getThreadPoolMBeanProxies();
		for (; threads.hasNext();) {
			Entry<String, JMXEnabledThreadPoolExecutorMBean> thread = threads
					.next();
                        String name = thread.getKey();
                        if (tpname.equals(name)) {
                            Tpstats tp = new Tpstats();
                            tp.setPoolName(thread.getKey());

                            IExecutorMBean threadPoolProxy = thread.getValue();
                            tp.setActiveCount(threadPoolProxy.getActiveCount());
                            tp.setPendingTasks(threadPoolProxy.getPendingTasks());
                            tp.setCompletedTasks(threadPoolProxy.getCompletedTasks());
                            return tp;
                        }
		}
		return null;
	}
        
	public List<String> getTpnames(String endpoint) throws IOException,
			InterruptedException {
                List<String> names = new ArrayList<String>();

		NodeProbe p = new NodeProbe(endpoint,connection.getJmxPort());
		Iterator<Entry<String, JMXEnabledThreadPoolExecutorMBean>> threads = p
				.getThreadPoolMBeanProxies();
		for (; threads.hasNext();) {
			Entry<String, JMXEnabledThreadPoolExecutorMBean> thread = threads
					.next();
                        names.add(thread.getKey());
		}

		return names;
	}

	// get Keyspaces of the cluser
	public List<KsDef> getKeyspaces() throws TException,
			InvalidRequestException {
		return client.describe_keyspaces();
	}
	
	public ArrayList<String> getKeyspacesName() 
	{
		ArrayList<String> ksName = new ArrayList<String>();
		List<KsDef> list = null;
		try {
			list = getKeyspaces();
		} catch (Exception e) {
			e.printStackTrace();
		};
		for(KsDef ks :list)
		{
			ksName.add(ks.name);
		}
			
		return ksName;
	}
	

	//add column
	public void addCfDef(CfDef cfDef) throws InvalidRequestException, SchemaDisagreementException, TException
	{
	
		client.system_update_column_family(cfDef);
	}
	// get keyspace with a given name
	public KsDef describeKeyspace(String keyspaceName)
			throws NotFoundException, InvalidRequestException, TException {
		return client.describe_keyspace(keyspaceName);
	}

	// add keyspace
	public void addKeyspace(String keyspaceName, String strategy,
			Map<String, String> strategyOptions, int replicationFactor)
			throws InvalidRequestException, TException,
			SchemaDisagreementException {
		KsDef ksDef = new KsDef();
		ksDef.setName(keyspaceName);
		ksDef.setStrategy_class(strategy);
		ksDef.setCf_defs(new LinkedList<CfDef>());

		if (strategyOptions.isEmpty()) {
			strategyOptions = new HashMap<String, String>();
			strategyOptions.put("replication_factor",
					String.valueOf(replicationFactor));
		}
		
		ksDef.setStrategy_options(strategyOptions);

		client.system_add_keyspace(ksDef);
	}

	// update keyspace with strategy, replication
	public void updateKeyspace(String keyspaceName, String strategy,
			Map<String, String> strategyOptions, int replicationFactor)
			throws InvalidRequestException, TException,
			SchemaDisagreementException {
		KsDef ksDef = new KsDef();
		ksDef.setName(keyspaceName);
		ksDef.setStrategy_class(strategy);
		ksDef.setCf_defs(new LinkedList<CfDef>());
		if (strategyOptions.isEmpty()) {
			strategyOptions = new HashMap<String, String>();
			strategyOptions.put("replication_factor",
					String.valueOf(replicationFactor));
		}
		ksDef.setStrategy_options(strategyOptions);

		client.system_update_keyspace(ksDef);
	}

	// drop keyspace
	public void dropKeyspace(String keyspaceName)
			throws InvalidRequestException, SchemaDisagreementException,
			TException {
		client.system_drop_keyspace(keyspaceName);
	}

	//

	// add column family
	public void addColumnFamily(String keyspaceName, ColumnFamily cf)
			throws InvalidRequestException, TException,
			SchemaDisagreementException {
		this.keyspace = keyspaceName;
		// create columnfamily cfDef with default settings
		CfDef cfDef = new CfDef(keyspaceName, cf.getColumnFamilyName());

		cfDef.setColumn_type(cf.getColumnType());
		// set cfDef's settings with custom input
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
			cfDef.setRow_cache_save_period_in_seconds(Integer.valueOf(cf
					.getRowCacheSavePeriod()));
		}

		if (!isEmpty(cf.getKeysCached())) {
			cfDef.setKey_cache_size(Double.valueOf(cf.getKeysCached()));
		}

		if (!isEmpty(cf.getKeyCacheSavePeriod())) {
			cfDef.setKey_cache_save_period_in_seconds(Integer.valueOf(cf
					.getKeyCacheSavePeriod()));
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

	/*	if (!isEmpty(cf.getMemtableOperations())) {
			cfDef.setm
			cfDef.setMemtable_operations_in_millions(Double.valueOf(cf
					.getMemtableOperations()));
		}

		if (!isEmpty(cf.getMemtableThroughput())) {
			cfDef.setMemtable_throughput_in_mb(Integer.valueOf(cf
					.getMemtableThroughput()));
		}

		if (!isEmpty(cf.getMemtableFlushAfter())) {
			cfDef.setMemtable_flush_after_mins(Integer.valueOf(cf
					.getMemtableFlushAfter()));
		}*/

		if (!isEmpty(cf.getDefaultValidationClass())) {
			cfDef.setDefault_validation_class(cf.getDefaultValidationClass());
		}

		if (!isEmpty(cf.getMinCompactionThreshold())) {
			cfDef.setMin_compaction_threshold(Integer.valueOf(cf
					.getMinCompactionThreshold()));
		}

		if (!isEmpty(cf.getMaxCompactionThreshold())) {
			cfDef.setMax_compaction_threshold(Integer.valueOf(cf
					.getMaxCompactionThreshold()));
		}

		client.set_keyspace(keyspaceName);
		client.system_add_column_family(cfDef);
	}

	// update column family
	public void updateColumnFamily(String keyspaceName, ColumnFamily cf)
			throws InvalidRequestException, TException,
			SchemaDisagreementException {
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
			cfDef.setRow_cache_save_period_in_seconds(Integer.valueOf(cf
					.getRowCacheSavePeriod()));
		}

		if (!isEmpty(cf.getKeysCached())) {
			cfDef.setKey_cache_size(Double.valueOf(cf.getKeysCached()));
		}

		if (!isEmpty(cf.getKeyCacheSavePeriod())) {
			cfDef.setKey_cache_save_period_in_seconds(Integer.valueOf(cf
					.getKeyCacheSavePeriod()));
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

		if (!isEmpty(cf.getDefaultValidationClass())) {
			cfDef.setDefault_validation_class(cf.getDefaultValidationClass());
		}

		if (!isEmpty(cf.getMinCompactionThreshold())) {
			cfDef.setMin_compaction_threshold(Integer.valueOf(cf
					.getMinCompactionThreshold()));
		}

		if (!isEmpty(cf.getMaxCompactionThreshold())) {
			cfDef.setMax_compaction_threshold(Integer.valueOf(cf
					.getMaxCompactionThreshold()));
		}

		client.set_keyspace(keyspaceName);
		client.system_update_column_family(cfDef);
	}

	// drop column family
	public void dropColumnFamily(String keyspaceName, String columnFamilyName)
			throws InvalidRequestException, TException,
			SchemaDisagreementException {
		this.keyspace = keyspaceName;
		client.set_keyspace(keyspaceName);
		client.system_drop_column_family(columnFamilyName);
	}

	// truncate column family
	public void truncateColumnFamily(String keyspaceName,
			String columnFamilyName) throws InvalidRequestException,
			TException, UnavailableException {
		this.keyspace = keyspaceName;
		this.columnFamily = columnFamilyName;
		client.set_keyspace(keyspaceName);
		
		try {
			client.truncate(columnFamilyName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public Map<String, String> getColumnFamily(String keyspace,
			String columnFamily) throws NotFoundException, TException,
			InvalidRequestException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		for (Iterator<CfDef> cfIterator = client.describe_keyspace(keyspace)
				.getCf_defsIterator(); cfIterator.hasNext();) {
			CfDef next = cfIterator.next();
			if (columnFamily.equalsIgnoreCase(next.getName())) {
				Map<String, String> columnMetadata = new HashMap<String, String>();

				CfDef._Fields[] fields = CfDef._Fields.values();

				for (int i = 0; i < fields.length; i++) {
					CfDef._Fields field = fields[i];
					// using string concat to avoin NPE, if the value is not
					// null
					// need to find an elegant solution
					columnMetadata.put(field.name(), next.getFieldValue(field)
							+ "");
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
			throws NotFoundException, TException, InvalidRequestException,
			UnsupportedEncodingException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		for (Iterator<CfDef> cfIterator = client.describe_keyspace(keyspace)
				.getCf_defsIterator(); cfIterator.hasNext();) {
			CfDef cd = cfIterator.next();
			if (columnFamily.equalsIgnoreCase(cd.getName())) {
				ColumnFamily cf = new ColumnFamily();
				cf.setId(cd.getId());
				cf.setKeyspace(keyspace);
				cf.setColumnFamilyName(cd.getName());
				cf.setColumnType(cd.getColumn_type());
				cf.setComparator(cd.getComparator_type());
				cf.setSubcomparator(cd.getSubcomparator_type());
				cf.setComment(cd.getComment());
				cf.setRowsCached(String.valueOf(cd.getRow_cache_size()));
				cf.setRowCacheSavePeriod(String.valueOf(cd
						.getRow_cache_save_period_in_seconds()));
				cf.setKeysCached(String.valueOf(cd.getKey_cache_size()));
				cf.setKeyCacheSavePeriod(String.valueOf(cd
						.getKey_cache_save_period_in_seconds()));
				cf.setReadRepairChance(String.valueOf(cd
						.getRead_repair_chance()));
				cf.setGcGrace(String.valueOf(cd.getGc_grace_seconds()));
				
				cf.setDefaultValidationClass(cd.getDefault_validation_class());
				cf.setMinCompactionThreshold(String.valueOf(cd
						.getMin_compaction_threshold()));
				cf.setMaxCompactionThreshold(String.valueOf(cd
						.getMax_compaction_threshold()));
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
 * get columns of columnsFamily
 * @throws InvalidRequestException 
 * @throws TException 
 * @throws NotFoundException 
 * @throws UnsupportedEncodingException 
 * */	
	public ArrayList<String> getColumns(String keyspace, String columnFamily) throws UnsupportedEncodingException, NotFoundException, TException, InvalidRequestException
	{
		ArrayList<String> columns = new ArrayList<String>();
		ColumnFamily  colummFamily = getColumnFamilyBean(keyspace, columnFamily);
		for(ColumnFamilyMetaData l: colummFamily.getMetaDatas())
		{
			columns.add(l.getColumnName());
		}
		return columns;
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
	public Set<String> getColumnFamilys(String keyspace) {
		this.keyspace = keyspace;

		Set<String> s = new TreeSet<String>();
		KsDef ks = null;
		try {
			ks = client.describe_keyspace(keyspace);
		} catch (Exception e) {
			e.printStackTrace();
		} ;

		for (Iterator<CfDef> cfIterator = ks.getCf_defsIterator(); cfIterator.hasNext();) {
			CfDef next = cfIterator.next();
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
	 * @return client.get_count(ByteBuffer.wrap(key.getBytes()), colParent,
	 *         null, ConsistencyLevel.ONE)
	 * @throws TimedOutException
	 * @throws TException
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 */
	public int countColumnsRecord(String keyspace, String columnFamily,
			String key) throws InvalidRequestException, UnavailableException,
			TimedOutException, TException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnParent colParent = new ColumnParent(columnFamily);
		// TODO - Verify if its working fine
		return client.get_count(ByteBuffer.wrap(key.getBytes()), colParent,
				null, ConsistencyLevel.ONE);
	}

	public int countSuperColumnsRecord(String keyspace, String columnFamily,
			String superColumn, String key) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnParent colParent = new ColumnParent(columnFamily);
		colParent.setSuper_column(superColumn.getBytes());
		// TODO - verify if its working fine
		return client.get_count(ByteBuffer.wrap(key.getBytes()), colParent,
				null, ConsistencyLevel.ONE);
	}

	/**
	 * insert a key, column ,value
	 * */
	public Date insertColumn(String keyspace, String columnFamily, String key,
			String superColumn, String column, String value)
			throws InvalidRequestException, UnavailableException,
			TimedOutException, TException, UnsupportedEncodingException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnParent parent;

		if (superColumn == null) {
			parent = new ColumnParent(columnFamily);
		} else {
			parent = new ColumnParent(columnFamily);
			parent.setSuper_column(ByteBuffer.wrap(superColumn.getBytes()));
		}

		long timestamp = System.currentTimeMillis();

		Column col = new Column(toByteBuffer(column));
		col.setValue(value.getBytes(UTF8));
		col.setTimestamp(timestamp);

		client.set_keyspace(keyspace);
		client.insert(ByteBuffer.wrap(key.getBytes()), parent, col,
				ConsistencyLevel.ONE);

		return new Date(timestamp / 1000);
	}
	public static ByteBuffer toByteBuffer(String value) 
	       throws UnsupportedEncodingException
	       {
	           return ByteBuffer.wrap(value.getBytes("UTF-8"));
	       };

	public Date insertByteColumn(String keyspace, String columnFamily, String key,
			String superColumn, String column, byte[] bytesValue)
			throws InvalidRequestException, UnavailableException,
			TimedOutException, TException, UnsupportedEncodingException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnParent parent;

		if (superColumn == null) {
			parent = new ColumnParent(columnFamily);
		} else {
			parent = new ColumnParent(columnFamily);
			parent.setSuper_column(ByteBuffer.wrap(superColumn.getBytes()));
		}

		long timestamp = System.currentTimeMillis() * 1000;
		Column col = new Column();
		col.setName(column.getBytes(UTF8));
		col.setValue(bytesValue);
		col.setTimestamp(timestamp);
	

		client.set_keyspace(keyspace);
		client.insert(ByteBuffer.wrap(key.getBytes()), parent, col,
				ConsistencyLevel.ONE);

		return new Date(timestamp / 1000);
	}
	
	public void removeKey(String keyspace, String columnFamily, String key)
			throws InvalidRequestException, UnavailableException,
			TimedOutException, TException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnPath colPath = new ColumnPath(columnFamily);
		long timestamp = System.currentTimeMillis() * 1000;

		client.set_keyspace(keyspace);
		client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp,
				ConsistencyLevel.ONE);
	}

	public void removeSuperColumn(String keyspace, String columnFamily,
			String key, String superColumn) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException {
		ColumnPath colPath = new ColumnPath(columnFamily);
		colPath.setSuper_column(superColumn.getBytes());
		long timestamp = System.currentTimeMillis() * 1000;

		client.set_keyspace(keyspace);
		client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp,
				ConsistencyLevel.ONE);
	}

	public void removeColumn(String keyspace, String columnFamily, String key,
			String column) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnPath colPath = new ColumnPath(columnFamily);
		colPath.setColumn(column.getBytes());
		long timestamp = System.currentTimeMillis() * 1000;

		client.set_keyspace(keyspace);
		client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp,
				ConsistencyLevel.ONE);
	}

	public void removeColumn(String keyspace, String columnFamily, String key,
			String superColumn, String column) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		ColumnPath colPath = new ColumnPath(columnFamily);
		colPath.setSuper_column(superColumn.getBytes());
		colPath.setColumn(column.getBytes());
		long timestamp = System.currentTimeMillis() * 1000;

		client.set_keyspace(keyspace);
		client.remove(ByteBuffer.wrap(key.getBytes()), colPath, timestamp,
				ConsistencyLevel.ONE);
	}

	/**
	 * get one row
	 * */
	public Map<String, Key> getKey(String keyspace, String columnFamily,
			String superColumn, String key) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException,
			UnsupportedEncodingException {
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;

		Map<String, Key> m = new TreeMap<String, Key>();

		ColumnParent columnParent = new ColumnParent(columnFamily);
		if (superColumn != "") {
			columnParent.setSuper_column(superColumn.getBytes());
		}

		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(new byte[0]);
		sliceRange.setFinish(new byte[0]);

		SlicePredicate slicePredicate = new SlicePredicate();
		slicePredicate.setSlice_range(sliceRange);
		client.set_keyspace(keyspace);

		List<ColumnOrSuperColumn> l = null;
		try {
			l = client.get_slice(ByteBuffer.wrap(key.getBytes("UTF-8")), columnParent,
					slicePredicate, ConsistencyLevel.ONE);
		} catch (Exception e) {
			return m;
		}

		Key k = new Key(key, new TreeMap<String, SColumn>(),
				new TreeMap<String, Cell>());
		for (ColumnOrSuperColumn column : l) {
			k.setSuperColumn(column.isSetSuper_column());
			if (column.isSetSuper_column()) {
				SuperColumn scol = column.getSuper_column();
				SColumn s = new SColumn(k, new String(scol.getName(), UTF8),
						new TreeMap<String, Cell>());
				for (Column col : scol.getColumns()) {
					Cell c = new Cell(s, new String(col.getName(), UTF8),
							new String(col.getValue(), UTF8), new Date(
									col.getTimestamp() / 1000),col.getValue());
					s.getCells().put(c.getName(), c);
				}

				k.getSColumns().put(s.getName(), s);
			} else {
				Column col = column.getColumn();
				Cell c = new Cell(k, new String(col.getName(), UTF8),
						new String(col.getValue(), UTF8), new Date(
								col.getTimestamp() / 1000),col.getValue());
				k.getCells().put(c.getName(), c);
			}

			m.put(k.getName(), k);
		}

		return m;
	}
	
/**
 * get first n row
 * here will not use startKey and endKey, cause the row is stored in md5, there is no meaning to sort them by md5
 * */
	public Map<String, Key> listKeyAndValues(String keyspace,String columnFamily, String startKey, String endKey, int rows)
			throws InvalidRequestException, UnavailableException,
			TimedOutException, TException, UnsupportedEncodingException {
		
		this.keyspace = keyspace;
		this.columnFamily = columnFamily;
		
		//get columnmeta data to check column type
		ColumnFamily columnFamilyBean = new ColumnFamily();       
		try {
			columnFamilyBean =  getColumnFamilyBean(keyspace, columnFamily);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<ColumnFamilyMetaData> metaDatas = columnFamilyBean.getMetaDatas();
		//make a hash of columnName and indexType
		Map<String,String> columnTypeMap = new HashMap<String,String>();
		for(ColumnFamilyMetaData meta : metaDatas)
		{
			columnTypeMap.put(meta.getColumnName(), meta.getValiDationClass());
		}
		
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
			keySlices = client.get_range_slices(columnParent, slicePredicate,
					keyRange, ConsistencyLevel.ONE);
	
		} catch (UnavailableException e) {
			return m;
		}

		for (KeySlice keySlice : keySlices) {
			Key key = new Key(new String(keySlice.getKey()),
					new TreeMap<String, SColumn>(), new TreeMap<String, Cell>());

			for (ColumnOrSuperColumn column : keySlice.getColumns()) {
				key.setSuperColumn(column.isSetSuper_column());
				
				if (column.isSetSuper_column()) {
					SuperColumn scol = column.getSuper_column();
					SColumn s = new SColumn(key, new String(scol.getName(),
							UTF8), new TreeMap<String, Cell>());
					for (Column col : scol.getColumns()) {
						Cell c = getValueByType(columnTypeMap, col,key);
						s.getCells().put(c.getName(), c);
						
					}
					key.getSColumns().put(s.getName(), s);
				} else {
					Column col = column.getColumn();
					Cell c = getValueByType(columnTypeMap, col,key);
					key.getCells().put(c.getName(), c);
				}
			}
			m.put(key.getName(), key);
		}
		return m;
	}
	
	/*get cell value by type*/
	public Cell getValueByType(Map<String,String> columnTypeMap, Column col, Key key)
	{
		Cell c = new Cell();
		try {
			String n = new String(col.getName(), UTF8);
			if(columnTypeMap.containsKey(new String(col.getName(), UTF8)))
			{
				String type= columnTypeMap.get(new String(col.getName(), UTF8));
				//get the value based on column type
				if(type.equals("org.apache.cassandra.db.marshal.UTF8Type"))
				{
					c = new Cell(key, new String(col.getName(), UTF8),new String(col.getValue(), UTF8), new Date(
							col.getTimestamp() / 1000),col.getValue());
			
				}
				else if(type.equals("org.apache.cassandra.db.marshal.LongType"))
				{
					BigInteger   valueInteger   =   new   BigInteger(col.getValue()); 
					long   valueLong   =   valueInteger.longValue();
					c = new Cell(key, new String(col.getName(), UTF8), valueLong+"", new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
				else if(type.equals("org.apache.cassandra.db.marshal.FloatType"))
				{
					BigInteger   valueInteger   =   new   BigInteger(col.getValue()); 
					float   valueFloat   =   valueInteger.floatValue();
					c = new Cell(key, new String(col.getName(), UTF8), valueFloat+"", new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
				else if(type.equals("org.apache.cassandra.db.marshal.BytesType"))
				{
					c = new Cell(key, new String(col.getName(), UTF8), col.getValue()+"", new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
				else if(type.equals("org.apache.cassandra.db.marshal.AsciiType"))
				{
					c = new Cell(key, new String(col.getName(), UTF8),new String(col.getValue(), "US-ASCII"), new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
				else if(type.equals("org.apache.cassandra.db.marshal.Int32Type"))
				{
					BigInteger   valueInteger   =   new   BigInteger(col.getValue()); 
					c = new Cell(key, new String(col.getName(), UTF8), valueInteger+"" , new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
				else if(type.equals("org.apache.cassandra.db.marshal.TimeUUIDType"))
				{
					UUID valueUUID = toUUID(col.getValue());
					c = new Cell(key, new String(col.getName(), UTF8),valueUUID.toString(), new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
				else
				{
					c = new Cell(key, new String(col.getName(), UTF8),new String(col.getValue(), UTF8), new Date(
							col.getTimestamp() / 1000),col.getValue());
				}
			}
			else
			{
				    c = new Cell(key, new String(col.getName(), UTF8),new String(col.getValue(), UTF8), new Date(
								col.getTimestamp() / 1000),col.getValue());
				
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return c;
	}
	

	public static java.util.UUID toUUID( byte[] uuid )
	{
	    long msb = 0;
	    long lsb = 0;
	    assert uuid.length == 16;
	    for (int i=0; i<8; i++)
	        msb = (msb << 8) | (uuid[i] & 0xff);
	    for (int i=8; i<16; i++)
	        lsb = (lsb << 8) | (uuid[i] & 0xff);
	    long mostSigBits = msb;
	    long leastSigBits = lsb;
	
	    UUID u = new UUID(msb,lsb);
	    return java.util.UUID.fromString(u.toString());
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
	 * @param keyspace
	 *            the keyspace to set
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
	 * @param columnFamily
	 *            the columnFamily to set
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
	 * @param superColumn
	 *            the superColumn to set
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
}
