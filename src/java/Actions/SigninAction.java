package Actions;

import Tools.ClusterConnection;
import java.util.Map;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;
/**
 *
 * @author yeyh10
 */
public class SigninAction extends ActionSupport{
    private String username;
    private String password;
    private String ip;
    private Integer thrift;
    private Integer jmx;
    private String root;
    private ClusterConnection conn;

    public boolean fetchYaml(String root) throws IOException {
        String path = root + "/cassandra/conf/cassandra.yaml";
        ServletContext sc = ServletActionContext.getServletContext();  
        String localpath = sc.getRealPath("/");
        String base = localpath + "config/";
        String cmd = "scp usdms@" + getIp() + ":" + path + " " + base;
        File file = new File(base + "cassandra.yaml");
        if (file.exists())
            file.delete();
        Runtime run = Runtime.getRuntime();
        run.exec(cmd);
        return true;
    }
    
    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public String execute() throws Exception {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        if (session.get("user") != null) {
            return SUCCESS;
        }
        if (getUsername().equals("user") && getPassword().equals("123456")) {
            fetchYaml(root);
            if (InitCluster.isInited() == true) {
                session.put("inited", "true");
            }
            else {
                session.put("inited", "false");
                return SUCCESS;
            }
            setConn(new ClusterConnection(ip, thrift, jmx));
            session.put("conn", getConn());
            session.put("user", "user");
            session.put("ip", ip);
            session.put("thrift", thrift);
            session.put("jmx", jmx);
            session.put("root", getRoot());
            return SUCCESS;
        }
        else {
            return ERROR;
        }
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getThrift() {
        return thrift;
    }

    public void setThrift(Integer thrift) {
        this.thrift = thrift;
    }

    public Integer getJmx() {
        return jmx;
    }

    public void setJmx(Integer jmx) {
        this.jmx = jmx;
    }

    public ClusterConnection getConn() {
        return conn;
    }

    public void setConn(ClusterConnection conn) {
        this.conn = conn;
    }
}
