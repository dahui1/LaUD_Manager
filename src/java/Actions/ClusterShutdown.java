/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.opensymphony.xwork2.ActionSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Wx
 */
public class ClusterShutdown extends ActionSupport{
    private String shutiplist ; 

    public String getShutiplist() {
        return shutiplist;
    }

    public void setShutiplist(String shutiplist) {
        this.shutiplist = shutiplist;
    }
   
  
    @Override
    public String execute() throws Exception {
       
        String[] ips = getShutiplist().split(";");
        for(int i=0;i<ips.length;i++){
            final String ipshut = ips[i].trim();
            Thread t= new Thread(new Runnable(){

                @Override
                public void run() {
                    System.out.println(ipshut);
                    String hostname = ipshut;
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
                        sess.execCommand("kill -9 `jps | grep 'LaUDDaemon' | awk '{print $1}'`");
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
       return "success";
    }
}
