package Actions;

import Node.Tpstats;
import Tools.ClusterConnection;
import Tools.DataManager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.IOException;
import java.util.Map;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author yeyh10
 */
public class TPStatics extends ActionSupport{
    private DataManager dataManager;
    private ClusterConnection conn;
    private Tpstats tpstatics;
    private String result;
    private String type;
    private String ip;
    private String tpname;

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
    
    public DataManager getDataManager() {
        return this.dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public ClusterConnection getConn() {
        return conn;
    }

    public void setConn(ClusterConnection conn) {
        this.conn = conn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public Tpstats getTpstatics() {
        return tpstatics;
    }

    public void setTpstatics(Tpstats tpstatics) {
        this.tpstatics = tpstatics;
    }

    public String getTpname() {
        return tpname;
    }

    public void setTpname(String tpname) {
        this.tpname = tpname;
    }
    
    @Override
    public String execute() throws InterruptedException, IOException, TTransportException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        if ((dataManager = (DataManager)session.get("dataManager")) == null) {
            String host = (String)session.get("ip");
            Integer thrift = (Integer)session.get("thrift");
            Integer JMX = (Integer)session.get("jmx");

            setConn(new ClusterConnection(host, thrift, JMX));
            try {
                getConn().connect();
            } catch (TTransportException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setDataManager(new DataManager(getConn()));
            session.put("dataManager", dataManager);
        }
        setTpstatics(getDataManager().getTpstats(ip, tpname));
        if (type.equals("Active Count")) {
            setResult(String.valueOf(getTpstatics().getActiveCount()));
        }
        else if (type.equals("Pending Tasks")) {
            setResult(String.valueOf(getTpstatics().getPendingTasks()));
        }
        else if (type.equals("Completed Tasks")) {
            setResult(String.valueOf(getTpstatics().getCompletedTasks()));
        }
        return "tpstatics";
    }
}


