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
package Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * file:ColumnFamily.java
 * columnFamily model
 * created at:11-03-2012
 * @author 林丹
 * */
public class ColumnFamily implements Serializable{
    private int id;
    private String keyspace;
    private String columnFamilyName;
    private String columnType;
    private String comparator;
    private String subcomparator;
    private String comment;
    private String rowsCached;
    private String rowCacheSavePeriod;
    private String keysCached;
    private String keyCacheSavePeriod;
    private String readRepairChance;
    private String gcGrace;
   /* private String memtableOperations;
    private String memtableThroughput;
    private String memtableFlushAfter;*/
    private String defaultValidationClass;
    private String minCompactionThreshold;
    private String maxCompactionThreshold;
    private List<ColumnFamilyMetaData> metaDatas = new ArrayList<ColumnFamilyMetaData>();

    public ColumnFamily() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the columnFamilyName
     */
    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    /**
     * @param columnFamilyName the columnFamilyName to set
     */
    public void setColumnFamilyName(String columnFamilyName) {
        this.columnFamilyName = columnFamilyName;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * @param columnType the columnType to set
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * @return the comparator
     */
    public String getComparator() {
        return comparator;
    }

    /**
     * @param comparator the comparator to set
     */
    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    /**
     * @return the subcomparator
     */
    public String getSubcomparator() {
        return subcomparator;
    }

    /**
     * @param subcomparator the subcomparator to set
     */
    public void setSubcomparator(String subcomparator) {
        this.subcomparator = subcomparator;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the rowsCached
     */
    public String getRowsCached() {
        return rowsCached;
    }

    /**
     * @param rowsCached the rowsCached to set
     */
    public void setRowsCached(String rowsCached) {
        this.rowsCached = rowsCached;
    }

    /**
     * @return the rowCacheSavePeriod
     */
    public String getRowCacheSavePeriod() {
        return rowCacheSavePeriod;
    }

    /**
     * @param rowCacheSavePeriod the rowCacheSavePeriod to set
     */
    public void setRowCacheSavePeriod(String rowCacheSavePeriod) {
        this.rowCacheSavePeriod = rowCacheSavePeriod;
    }

    /**
     * @return the keysCached
     */
    public String getKeysCached() {
        return keysCached;
    }

    /**
     * @param keysCached the keysCached to set
     */
    public void setKeysCached(String keysCached) {
        this.keysCached = keysCached;
    }

    /**
     * @return the keyCacheSavePeriod
     */
    public String getKeyCacheSavePeriod() {
        return keyCacheSavePeriod;
    }

    /**
     * @param keyCacheSavePeriod the keyCacheSavePeriod to set
     */
    public void setKeyCacheSavePeriod(String keyCacheSavePeriod) {
        this.keyCacheSavePeriod = keyCacheSavePeriod;
    }

    /**
     * @return the readRepairChance
     */
    public String getReadRepairChance() {
        return readRepairChance;
    }

    /**
     * @param readRepairChance the readRepairChance to set
     */
    public void setReadRepairChance(String readRepairChance) {
        this.readRepairChance = readRepairChance;
    }

    /**
     * @return the gcGrace
     */
    public String getGcGrace() {
        return gcGrace;
    }

    /**
     * @param gcGrace the gcGrace to set
     */
    public void setGcGrace(String gcGrace) {
        this.gcGrace = gcGrace;
    }

    /**
     * @return the defaultValidationClass
     */
    public String getDefaultValidationClass() {
        return defaultValidationClass;
    }

    /**
     * @param defaultValidationClass the defaultValidationClass to set
     */
    public void setDefaultValidationClass(String defaultValidationClass) {
        this.defaultValidationClass = defaultValidationClass;
    }

    /**
     * @return the minCompactionThreshold
     */
    public String getMinCompactionThreshold() {
        return minCompactionThreshold;
    }

    /**
     * @param minCompactionThreshold the minCompactionThreshold to set
     */
    public void setMinCompactionThreshold(String minCompactionThreshold) {
        this.minCompactionThreshold = minCompactionThreshold;
    }

    /**
     * @return the maxCompactionThreshold
     */
    public String getMaxCompactionThreshold() {
        return maxCompactionThreshold;
    }

    /**
     * @param maxCompactionThreshold the maxCompactionThreshold to set
     */
    public void setMaxCompactionThreshold(String maxCompactionThreshold) {
        this.maxCompactionThreshold = maxCompactionThreshold;
    }

    /**
     * @return the metaDatas
     */
    public List<ColumnFamilyMetaData> getMetaDatas() {
        return metaDatas;
    }

    /**
     * @param metaDatas the metaDatas to set
     */
    public void setMetaDatas(List<ColumnFamilyMetaData> metaDatas) {
        this.metaDatas = metaDatas;
    }

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}
}
