package Actions;

import Tools.ClusterManager;
import Node.NodeInfo;
import Node.RingNode;
import Tools.ClusterConnection;
import com.opensymphony.xwork2.ActionContext;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.dht.Token;
import org.apache.thrift.transport.TTransportException;
import static org.junit.Assert.assertEquals;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 该类用作处理获取某节点详细信息的请求。
 * @author yeyh10
 */
public class NodeDetail  extends ActionSupport {
    private ClusterManager clusterManager;
    private Client client;
    private ClusterConnection conn;
    private NodeInfo info;
    private String endPoint;

    public ClusterManager getClusterManager() {
        return this.clusterManager;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public NodeInfo getInfo() {
        return info;
    }

    public void setInfo(NodeInfo aInfo) {
        info = aInfo;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
    
    public ClusterConnection getConn() {
        return conn;
    }

    public void setConn(ClusterConnection conn) {
        this.conn = conn;
    }
    
    @Override
    public String execute() throws InterruptedException, TTransportException, IOException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        ClusterConnection cn = (ClusterConnection)session.get("conn");
        cn.connect();
        setClusterManager(new ClusterManager(cn, cn.getHost(), cn.getJmxPort()));
        RingNode ring = getClusterManager().listRing();
        Map<Token, String> rangeMap = ring.getRangeMap();
        List<Token> t = ring.getRanges();
        for (Token token : t) {
            String primaryEndpoint = rangeMap.get(token);
            if (!primaryEndpoint.equals(endPoint))
                continue;
            // 当token为指定endpoint的token时，返回其详细信息
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
