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

/**
 *
 * @author yeyh10
 */
public class GetInitParam extends ActionSupport {
    private String rootdir;
    private String clustername;
    private String datafiledir;
    private String commitlogdir;
    private String cachesdir;
    private String rpcport;
    private List<String> seeds;
    private List<String> tokens = new ArrayList<String>();
    private static String base = "/Users/yeyh10/Downloads/LaUD_Manager/src/java/Actions/";
    //private static String base = "/home/usdms/apache-tomcat-7.0.42/webapps/ROOT/WEB-INF/classes/Actions/";
    private static String yamldir = base + "cassandra_server.yaml";
    private static String tokendir = base +"token";

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

    public List<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public String execute() throws FileNotFoundException, IOException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        rootdir = (String)session.get("root");
        File filename = new File(base+"config");
        FileReader fileread;
        fileread = new FileReader(filename);
        String read;
        BufferedReader bufread = new BufferedReader(fileread);

        try {
            seeds = new ArrayList<String>();
            rootdir = bufread.readLine();
            clustername = bufread.readLine();
            datafiledir = bufread.readLine();
            commitlogdir = bufread.readLine();
            cachesdir = bufread.readLine();
            rpcport = bufread.readLine();
            while ((read = bufread.readLine()) != null) {
                seeds.add(read);
            }
            /*
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
                else if (read.contains("commitlog_directory"))
                    commitlogdir = read.substring("commitlog_directory:".length()).trim();
                else if (read.contains("saved_caches_directory"))
                    cachesdir = read.substring("saved_caches_directory: ".length()).trim();
                else if (read.contains("seeds:")){
                    int index = read.lastIndexOf("seeds:") + "seeds:".length() + 2;
                    String temp = read.substring(index).trim();
                    seeds = temp.split(",");
                    seeds[seeds.length-1] = seeds[seeds.length-1].substring(0, seeds[seeds.length-1].length()-1);
                }
                else if (read.contains("rpc_port"))
                    rpcport = read.substring("rpc_port:".length()).trim();
                    * 
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        bufread.close();
        fileread.close();
        
        filename = new File(tokendir);
        fileread = new FileReader(filename);
        bufread = new BufferedReader(fileread);
        try {
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
