package Actions;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 该类用作处理获取日志列表的请求。
 * @author Wx
 */
public class GetLog extends ActionSupport{
    private String ip;
    static String path;
    private List<String> loglist;
    
    String username = "usdms";
    String password = "hgjusdms";
    ch.ethz.ssh2.Connection conn;

    public List<String> getLoglist() {
        return loglist;
    }

    public void setLoglist(List<String> loglist) {
        this.loglist = loglist;
    }
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
     
    @Override
    public String execute(){
        String hostname = getIp();

        try
        {    	
            // 通过session连接，获取Log列表
            conn = new Connection(hostname);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            ActionContext actionContext = ActionContext.getContext();
            Map session = actionContext.getSession();
            session.put("logconnection",conn);
            Session sess = conn.openSession();
            sess.execCommand("ls "+getPath());

            // 获取系统输出的列表信息
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            loglist=new ArrayList<String>();
            String string;
            while (true)
            {
                string="<tr>";
                String line = null;
                for(int i=0;i<3;i++){
                    line = br.readLine();
                    if (line == null) 
                        break;
                    else 
                        string=string+"<td><a onclick=\"openlog('"+line+"')\">"+line+"</a></td>";
                }
                string = string+"</tr>";
                loglist.add(string);
                if (line == null) break;
            }
            sess.close();

        }catch(Exception e){
            e.printStackTrace(System.err);
        }
        return "getloglist";
    }

}
