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
        ServletContext sc = ServletActionContext.getServletContext();  
        String path = sc.getRealPath("/");
        base = path + "/config/";
        yamldir = base + "cassandra_server.yaml";
        tokendir = base +"token";
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

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        bufread.close();
        fileread.close();
        
        filename = new File(tokendir);
        System.out.println(tokendir);
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
