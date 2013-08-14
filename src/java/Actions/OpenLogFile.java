package Actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;
import java.util.Map;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Wx
 */
public class OpenLogFile extends ActionSupport{
     private List<String> logcontent; 
     private String logfilename;

    public String getLogfilename() {
        return logfilename;
    }

    public void setLogfilename(String logfilename) {
        this.logfilename = logfilename;
    }
    public List<String> getLogcontent() {
        return logcontent;
    }

    public void setLogcontent(List<String> logcontent) {
        this.logcontent = logcontent;
    }
     
    @Override
    public String execute(){
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        Connection conn=(Connection) session.get("logconnection");
        Session sess;
        try {
            sess = conn.openSession();
            sess.execCommand("cat /data1/logcassandra/logs/"+logfilename);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            logcontent=new ArrayList<String>();
            String string;
            while (true)
            {
                string="<p>";
                String line = br.readLine();
                if (line == null) break;
                string=string+line+"</p>";
                logcontent.add(string);
            }
            sess.close();
        } catch (IOException ex) {
            Logger.getLogger(OpenLogFile.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return "logcontent";
    }
}
