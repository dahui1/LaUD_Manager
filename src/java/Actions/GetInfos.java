package Actions;

import Node.RingNode;
import Tools.ClusterConnection;
import Tools.ClusterManager;
import Tools.DataManager;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author yeyh10
 */
public class GetInfos extends ActionSupport{
    private ClusterManager clusterManager;
    private ClusterConnection conn;
    private List<String> keyspaces;
    private List<List<String>> kscfs;
    private List<String> endpoints;

    public ClusterManager getClusterManager() {
        return clusterManager;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public ClusterConnection getConn() {
        return conn;
    }

    public void setConn(ClusterConnection conn) {
        this.conn = conn;
    }

    public List<String> getKeyspaces() {
        return keyspaces;
    }

    public void setKeyspaces(List<String> keyspaces) {
        this.keyspaces = keyspaces;
    }
    
    public List<String> getEndpoints() {
        return this.endpoints;
    }
    
    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
    
    public List<List<String>> getKscfs() {
        return kscfs;
    }

    public void setKscfs(List<List<String>> kscfs) {
        this.kscfs = kscfs;
    }
    
    public void checkCM ()  throws InterruptedException, IOException, TTransportException  {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        ClusterConnection cn = (ClusterConnection)session.get("conn");
        cn.connect();
        setClusterManager(new ClusterManager(cn, cn.getHost(), cn.getJmxPort()));
    }
    
    public String getKSs() throws InterruptedException, IOException, TTransportException{
        checkCM();
        setKeyspaces(getClusterManager().getProbe().getKeyspaces());
        return "keyspaces";
    }
    
    public String getEps() throws TTransportException, IOException, InterruptedException {
        NodeProbe probe;
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        probe = new NodeProbe((String)session.get("ip"), (Integer)session.get("jmx"));
        Collection<String> liveNodes = probe.getLiveNodes();

        endpoints = new ArrayList<String>();
        for (String ep : liveNodes) {
            endpoints.add(ep);
        }
        return "endpoints";
    }
    
    public String getAllEps() throws TTransportException, IOException, InterruptedException {
        checkCM();
        RingNode ring = getClusterManager().listRing();
        Map<Token, String> rangeMap = ring.getRangeMap();
        List<Token> t = ring.getRanges();
        endpoints = new ArrayList<String>();
        for (Token token : t) {
            endpoints.add(rangeMap.get(token));
        }
        return "endpoints";
    }
    
    public String getKSCFs() throws InterruptedException, IOException, TTransportException, TException, InvalidRequestException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        DataManager dataManager;
        if ((dataManager = (DataManager)session.get("dataManager")) == null) {
            String host = (String)session.get("ip");
            Integer thrift = (Integer)session.get("thrift");
            Integer JMX = (Integer)session.get("jmx");

            conn = new ClusterConnection(host, thrift, JMX);
            try {
                conn.connect();
            } catch (TTransportException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dataManager = new DataManager(conn);
            session.put("dataManager", dataManager);
        }
        List<KsDef> kss = dataManager.getKeyspaces();
        kscfs = new ArrayList<List<String>>();
        int i = 0;
        for (KsDef ks : kss) {
            List<String> temp = new ArrayList<String>();
            temp.add(ks.getName());
            List<CfDef> cfs = ks.getCf_defs();
            for (CfDef c : cfs) {
                temp.add(c.getName());
            }
            kscfs.add(i++, temp);
        }
        return "kscfs";
    }
}
