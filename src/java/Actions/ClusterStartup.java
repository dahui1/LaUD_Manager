/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ethz.ssh2.Connection; 
import ch.ethz.ssh2.Session; 
import ch.ethz.ssh2.StreamGobbler;
/**
 *
 * @author yeyh10
 */
public class ClusterStartup extends ActionSupport{
    private String startiplist ; 

    public String getStartiplist() {
        return startiplist;
    }

    public void setStartiplist(String startiplist) {
        this.startiplist = startiplist;
    }

    @Override
    public String execute() throws Exception {       
        String[] ips = getStartiplist().split(";");
        for(int i=0;i<ips.length;i++){
            final String ipstart = ips[i].trim();
            Thread t= new Thread(new Runnable(){
                
                @Override
                public void run() {
                    System.out.println(ipstart);
                    String hostname = ipstart;
                    String username = "usdms";
                    String password = "hgjusdms";
	    	
                    try
                    {    	
                        ch.ethz.ssh2.Connection conn = new Connection(hostname);
                        conn.connect();
                        boolean isAuthenticated = conn.authenticateWithPassword(username, password);
                        if (isAuthenticated == false)
                            throw new IOException("Authentication failed.");
                        Session sess = conn.openSession();
                        sess.execCommand("/home/usdms/cassandra1.2/bin/cassandra");
                        System.out.println("Here is some information about the remote host:");
                        InputStream stdout = new StreamGobbler(sess.getStdout());
                        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
                        while (true)
                        {
                            String line = br.readLine();
                            if (line == null)
                            break;
                            System.out.println(line);
                        }
	    	
                        /* Show exit status, if available (otherwise "null") */
                        System.out.println("ExitCode: " + sess.getExitStatus());
                        /* Close this session */

                        sess.close();
                        /* Close the connection */

                        conn.close();
                    }
                    catch (IOException e)
                    {
                       e.printStackTrace(System.err); 
                       //System.exit(2);
                    }
                }
            });
            t.start();
        }
        return SUCCESS;
    }
}
