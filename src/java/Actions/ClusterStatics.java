package Actions;

import Node.RingNode;
import Tools.ClusterConnection;
import Tools.ClusterManager;
import com.opensymphony.xwork2.ActionContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.transport.TTransportException;


/**
 *
 * @author yeyh10
 */
public class ClusterStatics {
    private String type;
    private String result;
    
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
        if (type == "Read Count" || type == "Write Count") {
            int r = 0;
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
                        r += Integer.parseInt(statics.get(getType()));
                }
                cm.close();
                cm = null;
                conn.disconnect();
                conn = null;
            }
            setResult(String.valueOf(r));
        }
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
}
