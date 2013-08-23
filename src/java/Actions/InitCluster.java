package Actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;

/**
 *
 * @author yeyh10
 */
public class InitCluster extends ActionSupport {
    private static boolean inited  = true;
    private String rootdir;
    private String clustername;
    private String datafiledir;
    private String commitlogdir;
    private String cachesdir;
    private String rpcport;
    private String seeds;
    private String tokens;
    private static String base;// = getServlet().getServletContext().getRealPath("/");
    private static String yamldir;
    private static String tokendir;

    @Override
    public String execute() throws FileNotFoundException, IOException {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        ServletContext sc = ServletActionContext.getServletContext();  
        String path = sc.getRealPath("/");
        base = path + "/config/";
        yamldir = base + "cassandra.yaml";
        tokendir = base +"token";
        System.out.println(base);
        File filename = new File(yamldir);
        FileReader fileread;
        fileread = new FileReader(filename);
        String read;
        List<String> allread = new ArrayList<String>();
        String[] datadirs = datafiledir.split(",");
        String[] allseeds = seeds.split(",");
        String[] alltokens = tokens.split(",");
        BufferedReader bufread = new BufferedReader(fileread);
        try {
            while ((read = bufread.readLine()) != null) {
                allread.add(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < allread.size(); i++) {
            String one = allread.get(i);
            if (one.contains("cluster_name"))
                allread.set(i, "cluster_name: '" + clustername + "'");
            else if (one.contains("data_file_directories")) {
                for (String dir : datadirs) {
                    System.out.println(allread.get(i+1));
                    if (allread.get(i+1).contains(" - "))
                        allread.set((++i),"    - " + dir);
                    else
                        allread.add((++i),"    - " + dir);
                }
                i++;
                while (allread.get(i).contains(" - "))
                    allread.remove(i);
            }
            else if (one.contains("commitlog_directory"))
                allread.set(i, "commitlog_directory: " + commitlogdir);
            else if (one.contains("saved_caches_directory"))
                allread.set(i, "saved_caches_directory: " + cachesdir);
            else if (one.contains("seeds:"))
                allread.set(i, "          - seeds: \"" + seeds + "\"");
            else if (one.contains("rpc_port"))
                allread.set(i, "rpc_port: " + rpcport);
        }
        if (tokens.equals("")) {
            // Random token, so just send the .yaml file to all seeds.
            RandomAccessFile mm;
            try {
                mm = new RandomAccessFile(base+"cassandra.yaml", "rw");
                for (String one : allread) {
                    mm.writeBytes(one + "\r\n");
                }
                mm.close();
                for (String seed : allseeds) {
                    String cmd = "scp " + yamldir +" usdms@" + seed + ":" 
                            + (String)session.get("root") + "/cassandra/conf/";;
                    Runtime run = Runtime.getRuntime();
                    run.exec(cmd);
                }
                //mm.writeBytes(allread);
            } catch (IOException e1) {
                e1.printStackTrace();
            } 
        }
        else {
            int i = 0;
            RandomAccessFile tt;
            tt = new RandomAccessFile(tokendir, "rw");
            for (String seed : allseeds) {
                RandomAccessFile mm;
                try {
                    mm = new RandomAccessFile(yamldir, "rw");
                    for (String one : allread) {
                        if (one.contains("initial_token:")) {
                            one = "initial_token: " + alltokens[i];
                            tt.writeBytes(alltokens[i++] + "\r\n");
                        }
                        mm.writeBytes(one + "\r\n");
                    }
                    mm.close();
                    String cmd = "scp " + yamldir +" usdms@" + seed + ":" 
                            + (String)session.get("root") + "/cassandra/conf/";;
                    Runtime run = Runtime.getRuntime();
                    run.exec(cmd);
                    
                } catch (IOException e1) {
                    e1.printStackTrace();
                } 
            }
            tt.close();
        }
        
        RandomAccessFile back;
        try {
            back = new RandomAccessFile(yamldir, "rw");
            for (String one : allread) {
                back.writeBytes(one + "\r\n");
            }
            back.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "init";
    }
    
    public static boolean isInited() {
        return inited;
    }

    public static void setInited(boolean aInited) {
        inited = aInited;
    }

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

    public String getSeeds() {
        return seeds;
    }

    public void setSeeds(String seeds) {
        this.seeds = seeds;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }
}
