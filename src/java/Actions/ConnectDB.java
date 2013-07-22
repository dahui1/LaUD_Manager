/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

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
    public static java.sql.Connection con=null;
    
    @Override
    public String execute(){
        String ip=getConnectip();
        String port=getConnectport();
        System.out.println("connectip="+ip);
        System.out.println("connecport="+port);

        String host=System.getProperty("url","jdbc:laud://"+ip+":"+port+
                "?data_port=9091&support_function=true&hive=false");
        System.out.println(host);
        try {
            Class.forName("cn.edu.thu.laud.jdbc.LaUDDriver");
            con = DriverManager.getConnection(host, "", "");
//            Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
//            con = DriverManager.getConnection("jdbc:cassandra://192.168.3.3:9170?version=2.0");
            ActionContext actionContext = ActionContext.getContext();
            Map session = actionContext.getSession();
            session.put("connected", ip+":"+port);
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
}
