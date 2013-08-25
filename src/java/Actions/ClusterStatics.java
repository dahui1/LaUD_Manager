package Actions;

import Tools.ClusterConnection;
import Tools.ClusterManager;

import com.opensymphony.xwork2.ActionContext;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.transport.TTransportException;


/**
 * 该类用作处理获取集群整体监控数据的请求。
 * @author yeyh10
 */
public class ClusterStatics {
    private String type;
    private String result;
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    public String execute() throws IOException, InterruptedException, TTransportException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        ClusterConnection conn;
        ClusterManager cm;
        Map<String,String> statics;
        List<String> keyspaces;
        
        ClusterConnection cn = (ClusterConnection)session.get("conn");
        cn.connect();
        cm = new ClusterManager(cn, cn.getHost(), cn.getJmxPort());
        keyspaces = cm.getProbe().getKeyspaces();
        NodeProbe probe;
        probe = new NodeProbe((String)session.get("ip"), (Integer)session.get("jmx"));
        Collection<String> liveNodes = probe.getLiveNodes();

        Integer thrift = (Integer)session.get("thrift");
        Integer JMX = (Integer)session.get("jmx");
        // 处理获取读/写数目的请求
        if (type.equals("Read Count") || type.equals("Write Count")) {
            int r = 0;
            // 对每一个正常的节点进行统计
            for (String ep : liveNodes) {
                conn = new ClusterConnection(ep, thrift, JMX);
                try {
                    conn.connect();
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
                cm = new ClusterManager(conn, conn.getHost(), conn.getJmxPort());
                // 对该节点中每一个Keyspace的数据进行统计
                for (String ks : keyspaces) {
                    statics = cm.getKeyspaceStatics(ks);
                    if (!statics.get(getType()).equals("NaN"))
                        r += Integer.parseInt(statics.get(getType()));
                }
                cm.close();
                cm = null;
                conn.disconnect();
                conn = null;
            }
            setResult(String.valueOf(r));
        }
        // 处理读/写延迟的统计
        else {
            double r = 0;
            int count = 0;
            for (String ep : liveNodes) {
                conn = new ClusterConnection(ep, thrift, JMX);
                try {
                        conn.connect();
                    } catch (TTransportException e) {
                        e.printStackTrace();
                }
                cm = new ClusterManager(conn, conn.getHost(), conn.getJmxPort());
                for (String ks : keyspaces) {
                    statics = cm.getKeyspaceStatics(ks);
                    if (!statics.get(getType()).equals("NaN"))
                        r += Double.parseDouble(statics.get(getType()));
                    count++;
                }
                cm.close();
                cm = null;
                conn.disconnect();
                conn = null;
            }
            setResult(String.valueOf(r / count));
        }

        return "cluster";
    }
}
