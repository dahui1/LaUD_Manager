package Actions;

import Tools.ClusterConnection;
import Tools.ClusterManager;
import java.util.Map;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;
import org.apache.thrift.transport.TTransportException;
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
    private ClusterManager clusterManager;
    private ClusterConnection conn;

    public boolean fetchYaml(String root) throws IOException {
        String path = root + "/cassandra/conf/cassandra.yaml";
        ServletContext sc = ServletActionContext.getServletContext();  
        String localpath = sc.getRealPath("/");
        String base = localpath + "config/";
        String cmd = "scp usdms@" + getIp() + ":" + path + " " + base;
        Runtime run = Runtime.getRuntime();
        Process p = run.exec(cmd);
        System.out.println(p.getOutputStream());
        return true;
    }
    
    public void getConfig(String root) throws FileNotFoundException, IOException {
        ServletContext sc = ServletActionContext.getServletContext();  
        String path = sc.getRealPath("/");
        String read;
        FileReader fileread = new FileReader(path + "config/cassandra.yaml");
        BufferedReader bufread = new BufferedReader(fileread);
        RandomAccessFile mm = null;
        try {
            mm = new RandomAccessFile(path + "config/config", "rw");
        } catch (IOException e1) {
            e1.printStackTrace();
        } 
        
        String clustername = null, datafiledir = null, commitlogdir = null, 
                cachesdir = null, rpcport = null;
        String[] seeds = null;
        while ((read = bufread.readLine()) != null) {
            if (read.contains("cluster_name")) {
                clustername = read.substring("cluster_name:".length()).trim();
                clustername = clustername.substring(1, clustername.length()-1);
            }
            else if (read.contains("data_file_directories")) {
                read = bufread.readLine();
                datafiledir = "";
                while (read.contains(" - ")) {
                    datafiledir += read.substring(read.lastIndexOf("-") + 1).trim();
                    datafiledir += ";";
                    read = bufread.readLine();
                }
                datafiledir = datafiledir.substring(0, datafiledir.length()-1).trim();
            }
            else if (read.contains("commitlog_directory")) {
                commitlogdir = read.substring("commitlog_directory:".length()).trim();
            }
            else if (read.contains("saved_caches_directory")) {
                cachesdir = read.substring("saved_caches_directory: ".length()).trim();
            }
            else if (read.contains("seeds:")){
                int index = read.lastIndexOf("seeds:") + "seeds:".length() + 2;
                String temp = read.substring(index).trim();
                seeds = temp.split(",");
                seeds[seeds.length-1] = seeds[seeds.length-1]
                        .substring(0, seeds[seeds.length-1].length()-1);
            }
            else if (read.contains("rpc_port")) {
                rpcport = read.substring("rpc_port:".length()).trim();
            }
        }
        mm.writeBytes(root + "\r\n");
        mm.writeBytes(clustername + "\r\n");
        mm.writeBytes(datafiledir + "\r\n");
        mm.writeBytes(commitlogdir + "\r\n");
        mm.writeBytes(cachesdir + "\r\n");
        mm.writeBytes(rpcport + "\r\n");
        for (String s : seeds)
            mm.writeBytes(s + "\r\n");
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
            getConfig(root);
            if (InitCluster.isInited() == true) {
                session.put("inited", "true");
            }
            else {
                session.put("inited", "false");
                return SUCCESS;
            }
            setConn(new ClusterConnection(ip, thrift, jmx));
            try {
                getConn().connect();
            } catch (TTransportException e) {
                // TODO Auto-generated catch block
                return ERROR;
            }
            setClusterManager(new ClusterManager(getConn(), getConn().getHost(), getConn().getJmxPort()));
            session.put("clusterManager", getClusterManager());
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
}
