package Actions;

import Tools.ClusterConnection;
import Tools.DataManager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.ColumnDef;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KsDef;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author yeyh10
 */
public class GetCFs extends ActionSupport{
    private String keyspace;
    private String strategy_class;
    private Integer replication_factor;
    private List<CfDef> cfs;
    private List<List<ColumnDef>> cd;
    
    @Override
    public String execute() throws IOException, InterruptedException, TException, InvalidRequestException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        DataManager dataManager;
        ClusterConnection conn;
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
        cd = new ArrayList<List<ColumnDef>>();

        for (KsDef k : kss) {
            if (k.getName().equals(getKeyspace())) {
                setStrategy_class(k.getStrategy_class());
                setReplication_factor(k.getReplication_factor());
                cfs = k.getCf_defs();
                for (CfDef c : cfs) {
                    cd.add(c.getColumn_metadata());
                }
            }
        }
        return "cfs";
    }

    public String getStrategy_class() {
        return strategy_class;
    }

    public void setStrategy_class(String strategy_class) {
        this.strategy_class = strategy_class;
    }

    public Integer getReplication_factor() {
        return replication_factor;
    }

    public void setReplication_factor(Integer replication_factor) {
        this.replication_factor = replication_factor;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public List<CfDef> getCfs() {
        return cfs;
    }

    public void setCfs(List<CfDef> cfs) {
        this.cfs = cfs;
    }

    public List<List<ColumnDef>> getCd() {
        return cd;
    }

    public void setCd(List<List<ColumnDef>> cd) {
        this.cd = cd;
    }
}
