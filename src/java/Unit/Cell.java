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
import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;

public class Cell implements Unit, Serializable {
    private static final long serialVersionUID = 4517336493185234248L;

    private Unit parent;
    private String name;
    private String value;
    private Date date;
    private DefaultMutableTreeNode treeNode;

    public Cell() {
    }

    public Cell(Unit parent, String name, String value, Date date) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.date = date;
    }

    /**
     * @return the parent
     */
    public Unit getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Unit parent) {
        this.parent = parent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the treeNode
     */
    public DefaultMutableTreeNode getTreeNode() {
        return treeNode;
    }

    /**
     * @param treeNode the treeNode to set
     */
    public void setTreeNode(DefaultMutableTreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
