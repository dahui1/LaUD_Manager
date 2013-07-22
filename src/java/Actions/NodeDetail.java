
package Actions;

import static Actions.NodeStatus.getClient;
import static Actions.NodeStatus.getClusterManager;

import Tools.Client;
import Tools.ClusterManager;
import Node.NodeInfo;
import Node.RingNode;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.apache.cassandra.dht.Token;
import org.apache.thrift.transport.TTransportException;
import static org.junit.Assert.assertEquals;

import com.opensymphony.xwork2.ActionSupport;

/**
 *
 * @author yeyh10
 */
public class NodeDetail  extends ActionSupport {
    private static ClusterManager clusterManager;
    private static Client client;
    private NodeInfo info;
    private String endPoint;

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
     * @return the info
     */
    public NodeInfo getInfo() {
        return info;
    }

    /**
     * @param aInfo the info to set
     */
    public void setInfo(NodeInfo aInfo) {
        info = aInfo;
    }

    /**
     * @return the endPoint
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * @param endPoint the endPoint to set
     */
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
    
    @Override
    public String execute() throws InterruptedException, TTransportException, IOException {
        String host = "192.168.3.1";
        Integer thrift = 9170;
        Integer JMX = 7199;

        setClient(new Client(host, thrift, JMX));
        try {
            getClient().connect();
        } catch (IOException e) {
        } catch (TTransportException e) {
        }
        
        setClusterManager(new ClusterManager(getClient(), getClient().getJmxPort()));
        RingNode ring = getClusterManager().listRing();
        Map<Token, String> rangeMap = ring.getRangeMap();
        List<Token> t = ring.getRanges();
        for (Token token : t) {
            String primaryEndpoint = rangeMap.get(token);
            if (!primaryEndpoint.equals(endPoint))
                continue;
            info = null;
            try {
                info = getClusterManager().getNodeInfo(primaryEndpoint, token);
            } catch (InterruptedException e) {
            }
            assertEquals(primaryEndpoint, info.getEndpoint());
            break;
        }
        return "ainfo";
    }

}
