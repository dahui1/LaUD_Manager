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
import java.util.List;
import java.util.Map;

import org.apache.cassandra.dht.Token;

@SuppressWarnings("rawtypes")
public class RingNode implements Serializable {
    private static final long serialVersionUID = 8351368757758010586L;

    private Map<Token, String> rangeMap;
    private List<Token> ranges;

    public Map<Token, String> getRangeMap() {
        return rangeMap;
    }

    public void setRangeMap(Map<Token, String> map) {
        this.rangeMap = map;
    }

    public List<Token> getRanges() {
        return ranges;
    }

    public void setRanges(List<Token> ranges) {
        this.ranges = ranges;
    }

}
