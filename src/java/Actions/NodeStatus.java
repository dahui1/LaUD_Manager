/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.apache.cassandra.dht.Token;
import org.apache.thrift.transport.TTransportException;

import Node.RingNode;
import Node.NodeInfo;
import Tools.Client;
import Tools.ClusterManager;

import static org.junit.Assert.assertEquals;

public class NodeStatus extends ActionSupport {

    private static ClusterManager clusterManager;
    private static Client client;
    private List<NodeInfo> infos = new ArrayList<NodeInfo>();

    /**
     * @return the clusterManager
     */
    public static ClusterManager getClusterManager() {
        return clusterManager;
    }

    /**
     * @param aClusterManager the clusterManager to set
     */
    public static void setClusterManager(ClusterManager aClusterManager) {
        clusterManager = aClusterManager;
    }

    /**
     * @return the client
     */
    public static Client getClient() {
        return client;
    }

    /**
     * @param aClient the client to set
     */
    public static void setClient(Client aClient) {
        client = aClient;
    }

    /**
     * @return the infos
     */
    public List<NodeInfo> getInfos() {
        return infos;
    }

    /**
     * @param infos the infos to set
     */
    public void setInfos(List<NodeInfo> infos) {
        this.infos = infos;
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    public String execute() throws Exception {
        String host = "192.168.3.1";
        Integer thrift = 9170;
        Integer JMX = 7199;

        setClient(new Client(host, thrift, JMX));
        try {
            getClient().connect();
        } catch (TTransportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();

        setClusterManager(new ClusterManager(getClient(), getClient().getJmxPort()));
        RingNode ring = getClusterManager().listRing();
        Map<Token, String> rangeMap = ring.getRangeMap();
        List<Token> t = ring.getRanges();
        for (Token token : t) {
            String primaryEndpoint = rangeMap.get(token);
            NodeInfo nodeInfo = null;
            try {
                nodeInfo = getClusterManager().getNodeInfo(primaryEndpoint, token);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assertEquals(primaryEndpoint, nodeInfo.getEndpoint());
            infos.add(nodeInfo);
            session.put(nodeInfo.getEndpoint(), nodeInfo.getRange());
        }
        return "nodesinfo";
    }
}
