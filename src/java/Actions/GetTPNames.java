package Actions;

import Tools.ClusterConnection;
import Tools.DataManager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.thrift.transport.TTransportException;

/**
 * 该类用作处理获取线程池名字的请求。
 * @author yeyh10
 */
public class GetTPNames extends ActionSupport{
    private DataManager dataManager;
    private ClusterConnection conn;
    private List<String> tpnames;
    private String ip;

    public DataManager getDataManager() {
        return dataManager;
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

    public List<String> getTpnames() {
        return tpnames;
    }

    public void setTpnames(List<String> tpnames) {
        this.tpnames = tpnames;
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
        // 获取指定IP的机器中的线程池列表
        setDataManager(new DataManager(getConn()));
        setTpnames(getDataManager().getTpnames(ip));
        return "tps";
    }
}
