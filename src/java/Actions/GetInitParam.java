package Actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;

/**
 * 该类用作处理获取初始化之前原来集群配置信息的请求。
 * @author yeyh10
 */
public class GetInitParam extends ActionSupport {
    private String rootdir;
    private String clustername;
    private String datafiledir;
    private String commitlogdir;
    private String cachesdir;
    private String rpcport;
    private String[] seeds;
    private List<String> tokens = new ArrayList<String>();
    private static String base;
    private static String yamldir;
    private static String tokendir;

    public String getRootdir() {
        return rootdir;
    }

    public void setRootdir(String rootdir) {
        this.rootdir = rootdir;
    }

    public String getClustername() {
        return clustername;
    }

    public void setClustername(String clustername) {
        this.clustername = clustername;
    }

    public String getDatafiledir() {
        return datafiledir;
    }

    public void setDatafiledir(String datafiledir) {
        this.datafiledir = datafiledir;
    }

    public String getCommitlogdir() {
        return commitlogdir;
    }

    public void setCommitlogdir(String commitlogdir) {
        this.commitlogdir = commitlogdir;
    }

    public String getCachesdir() {
        return cachesdir;
    }

    public void setCachesdir(String cachesdir) {
        this.cachesdir = cachesdir;
    }

    public String getRpcport() {
        return rpcport;
    }

    public void setRpcport(String rpcport) {
        this.rpcport = rpcport;
    }

    public String[] getSeeds() {
        return seeds;
    }

    public void setSeeds(String[] seeds) {
        this.seeds = seeds;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
    
    public void getConfig() throws FileNotFoundException, IOException {
        // 打开从输入IP端获取的yaml配置文件
        ServletContext sc = ServletActionContext.getServletContext();  
        String path = sc.getRealPath("/");
        String read;
        FileReader fileread = new FileReader(path + "config/cassandra.yaml");
        BufferedReader bufread = new BufferedReader(fileread);
        
        // 读取yaml文件，并从中获取需要的配置信息
        String cn = null, df = null, cl = null, 
                cd = null, rp = null;
        String[] seed = null;
        while ((read = bufread.readLine()) != null) {
            if (read.trim().startsWith("#"))
                continue;
            if (read.contains("cluster_name")) {
                cn = read.substring("cluster_name:".length()).trim();
                cn = cn.substring(1, cn.length()-1);
            }
            else if (read.contains("data_file_directories")) {
                read = bufread.readLine();
                df = "";
                while (read.contains(" - ")) {
                    df += read.substring(read.lastIndexOf("-") + 1).trim();
                    df += ";";
                    read = bufread.readLine();
                }
                df = df.substring(0, df.length()-1).trim();
            }
            else if (read.contains("commitlog_directory")) {
                cl = read.substring("commitlog_directory:".length()).trim();
            }
            else if (read.contains("saved_caches_directory")) {
                cd = read.substring("saved_caches_directory: ".length()).trim();
            }
            else if (read.contains("seeds:")){
                int index = read.lastIndexOf("seeds:") + "seeds:".length() + 2;
                String temp = read.substring(index).trim();
                seed = temp.split(",");
                seed[seed.length-1] = seed[seed.length-1]
                        .substring(0, seed[seed.length-1].length()-1);
            }
            else if (read.contains("rpc_port")) {
                rp = read.substring("rpc_port:".length()).trim();
            }
        }
        
        //将获取的配置信息放入session中，供后续使用
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        session.put("clustername", cn);
        session.put("datafiledir", df);
        session.put("commitlogdir", cl);
        session.put("cachesdir", cd);
        session.put("rpcport", rp);
        session.put("seeds", seed);
    }

    @Override
    public String execute() throws FileNotFoundException, IOException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        rootdir = (String)session.get("root");
        ServletContext sc = ServletActionContext.getServletContext();  
        String path = sc.getRealPath("/");
        base = path + "/config/";
        yamldir = base + "cassandra.yaml";
        tokendir = base +"token";
        
        getConfig();

        // 获取之前的配置信息
        seeds = (String[])session.get("seeds");
        rootdir = (String)session.get("root");
        clustername = (String)session.get("clustername");
        datafiledir = (String)session.get("datafiledir");
        commitlogdir = (String)session.get("commitlogdir");
        cachesdir = (String)session.get("cachesdir");
        rpcport = (String)session.get("rpcport");
       
        // 读取token
        File filename = new File(tokendir);
        FileReader fileread = new FileReader(filename);
        BufferedReader bufread = new BufferedReader(fileread);
        try {
            String read;
            while ((read = bufread.readLine()) != null) {
                if (read == "")
                    tokens = null;
                else
                    tokens.add(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "params";
    }
}
