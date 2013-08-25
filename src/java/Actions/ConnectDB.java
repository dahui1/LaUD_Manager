/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import Node.RingNode;
import Tools.ClusterConnection;
import Tools.ClusterManager;
import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.apache.cassandra.dht.Token;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author Wx
 */
public class ConnectDB extends ActionSupport{
    private String connectip;
    private String connectport;

    public String getConnectip() {
        return connectip;
    }

    public void setConnectip(String connectip) {
        this.connectip = connectip;
    }

    public String getConnectport() {
        return connectport;
    }

    public void setConnectport(String connectport) {
        this.connectport = connectport;
    }
     
    
    @Override
    public String execute() throws IOException, InterruptedException, TTransportException{
        java.sql.Connection con=null;
        String ip=getConnectip();
        String port=getConnectport();
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        ClusterConnection cn = (ClusterConnection)session.get("conn");
        cn.connect();
        ClusterManager cm = new ClusterManager(cn, cn.getHost(), cn.getJmxPort());
        RingNode ring = cm.listRing();
        Map<Token, String> rangeMap = ring.getRangeMap();
        List<Token> t = ring.getRanges();
        List<String> endpoints = new ArrayList<String>();
        for (Token token : t) {
            endpoints.add(rangeMap.get(token));
        }
        if (!endpoints.contains(ip))
            return "noip";
        
        String host=System.getProperty("url","jdbc:laud://"+ip+":"+port+
                "?data_port=9091&support_function=true&hive=false");
        System.out.println(host);
        try {
            Class.forName("cn.edu.thu.laud.jdbc.LaUDDriver");
            con = DriverManager.getConnection(host, "", "");
            session.put("connected", ip+":"+port);
            session.put("connection", con);
            System.out.println("success");
            return SUCCESS;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            return ERROR;
        }
        catch (SQLException ex) {
            Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            return ERROR;
        }

    }
    
    public String  disconnect(){
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        java.sql.Connection con=(java.sql.Connection) session.get("connection");
        if(con!=null){
            try {
                con.close();
                System.out.println(((java.sql.Connection) session.get("connection")).isClosed());
                System.out.println("con is closed!");
                session.remove("connection");
                session.remove("connected");
            } catch (SQLException ex) {
                Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "disconnect";
    }      
}
