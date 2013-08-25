package Actions;

import Tools.ClusterConnection;
import Tools.ClusterManager;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import java.io.IOException;
import java.util.Map;

import org.apache.thrift.transport.TTransportException;


/**
 * 该类用作处理获取指定Keyspace实时监控数据的请求。
 * @author yeyh10
 */
public class KSStatics extends ActionSupport {
    private ClusterManager clusterManager;
    private ClusterConnection conn;
    private String keyspace;
    private Map<String,String> statics;
    private String result;
    private String type;
    private String ip;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
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

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public Map<String,String> getStatics() {
        return statics;
    }

    public void setStatics(Map<String,String> statics) {
        this.statics = statics;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    @Override
    public String execute() throws InterruptedException, IOException, TTransportException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        String host = getIp();
        Integer thrift = (Integer)session.get("thrift");
        Integer JMX = (Integer)session.get("jmx");

        setConn(new ClusterConnection(host, thrift, JMX));
        try {
            getConn().connect();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        
        // 通过JMX获取对应Keyspace的数据
        setClusterManager(new ClusterManager( getConn(), getConn().getHost(), getConn().getJmxPort()));
        setStatics(getClusterManager().getKeyspaceStatics(getKeyspace()));
        setResult(getStatics().get(type).toString());
        return "ksstatics";
    }
}
