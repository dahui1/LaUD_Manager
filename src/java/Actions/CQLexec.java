/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wx
 */
public class CQLexec extends ActionSupport{
    private String cqlstring;
    private List<String> jslist;
    private List<String> rslist;
    private String thead;
    private String errorinfo;
    private String typestring;
    private int currentpage=0;
    private int totalpage=0;
    private String current;
    private String total;
    private String gotopage;

    public String getGotopage() {
        return gotopage;
    }

    public void setGotopage(String gotopage) {
        this.gotopage = gotopage;
    }
    
    
    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    
    
    public int getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(int currentpage) {
        this.currentpage = currentpage;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }
    
    
    public String getTypestring() {
        return typestring;
    }

    public void setTypestring(String typestring) {
        this.typestring = typestring;
    }
    
    
    public String getErrorinfo() {
        return errorinfo;
    }

    public void setErrorinfo(String errorinfo) {
        this.errorinfo = errorinfo;
    }
    
    
    public List<String> getJslist() {
        return jslist;
    }

    public void setJslist(List<String> jslist) {
        this.jslist = jslist;
    }
    
    public String getThead() {
        return thead;
    }

    public void setThead(String thead) {
        this.thead = thead;
    }
    
    public List<String> getRslist() {
        return rslist;
    }

    public void setRslist(List<String> rslist) {
        this.rslist = rslist;
    }   
  
    public String getCqlstring() {
        return cqlstring;
    }

    public void setCqlstring(String cqlstring) {
        this.cqlstring = cqlstring;
    }
    
    @Override
    public String execute() {
        
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        java.sql.Connection con=(java.sql.Connection) session.get("connection");
       // System.out.println(getCqlstring());
        String[] cql=(getCqlstring().trim()).split(";\n");
        Statement statement;
        ResultSet rs = null;
        String selectstring = null;
        if(con!=null){
          try {
              
               if(getTypestring().equals("firstpage")){
                statement = con.createStatement();
                 for(int i=0;i<cql.length;i++){               
                        statement.execute(cql[i].trim());
                        System.out.println( cql[i].trim()+" success");
                        if(cql[i].trim().substring(0, 6).equalsIgnoreCase("select")){
                            
                            rs=statement.getResultSet();
                            selectstring=cql[i].trim();
                        }
                       
                    }
                  if(rs!=null){
                    ResultSetMetaData md =rs.getMetaData();
                    this.thead="<table class=\"table table-hover\">"+
                            "<thead><tr align=\"left\"><th></th><th>name</th><th>value</th><th></th>"+
                            "</tr></thead><tbody id =\"rstable\"></tbody>";
                    rslist=new ArrayList<String>();
                    jslist=new ArrayList<String>();
                  
                    int row=1;
                    int pagerows=10;
                    while(rs.next()){
                      if(pagerows>0){
                        String list = null;
                        String jsstring="<div class=\"cqlrs\"><table class=\"table table-bordered\">";
                        list="<tr><td>"+row+"</td><td>"+md.getColumnLabel(1)+"</td><td>"+
                        rs.getString(1)+"</td>"+
                            "<td><a id=\"rs"+row+"\"  rel=\"popover\">显示详细结果</a></td></tr>";
                        
                        int n=md.getColumnCount();
                        for(int i=1;i<=n;i++){
                            jsstring=jsstring+"<tr><td>"+md.getColumnLabel(i)+"</td><td>"+rs.getString(i)+"</td></tr>";
                        }
                        jsstring=jsstring+"</table></div>";
                        jsstring="$(\"#rs"+row+"\").popover({content:'"+jsstring+"',placement:'left',html: true});";
                        rslist.add(list);
                        jslist.add(jsstring);
                        pagerows--;
                     }
                        row++;
                    }
                    totalpage=(row-1)/10;
                    if((row-1)%10!=0){totalpage++;}
                    session.put("selectstring", selectstring);
                    currentpage=1;
                    rs.close(); 
                }
               statement.close();
            
              }
               
             if(getTypestring().equals("next")){
                      statement = con.createStatement();
                      String str=(String) session.get("selectstring");
                      statement.execute(str);
                      rs=statement.getResultSet();
                      ResultSetMetaData md =rs.getMetaData();
                    this.thead="<table class=\"table table-hover\">"+
                            "<thead><tr align=\"left\"><th></th><th>name</th><th>value</th><th></th>"+
                            "</tr></thead><tbody id =\"rstable\"></tbody>";
                    rslist=new ArrayList<String>();
                    jslist=new ArrayList<String>();
                    int row=1;
                    int index=Integer.valueOf(getCurrent())*10+1;
                    int pagerows=10;
                     while(rs.next()){
                      if(pagerows>0 && row>=index){
                        String list = null;
                        String jsstring="<div class=\"cqlrs\"><table class=\"table table-bordered\">";
                        list="<tr><td>"+row+"</td><td>"+md.getColumnLabel(1)+"</td><td>"+
                        rs.getString(1)+"</td>"+
                            "<td><a id=\"rs"+row+"\"  rel=\"popover\">显示详细结果</a></td></tr>";
                        
                        int n=md.getColumnCount();
                        for(int i=1;i<=n;i++){
                            jsstring=jsstring+"<tr><td>"+md.getColumnLabel(i)+"</td><td>"+rs.getString(i)+"</td></tr>";
                        }
                        jsstring=jsstring+"</table></div>";
                        jsstring="$(\"#rs"+row+"\").popover({content:'"+jsstring+"',placement:'left',html: true});";
                        rslist.add(list);
                        jslist.add(jsstring);
                        pagerows--;
                     }
                     if(pagerows==0){break;}
                        row++;
                    }
                     currentpage=Integer.valueOf(getCurrent())+1;
                      rs.close();
                      statement.close();
                      
               }
             
             if(getTypestring().equals("previous")){
                      statement = con.createStatement();
                      String str=(String) session.get("selectstring");
                      statement.execute(str);
                      rs=statement.getResultSet();
                      ResultSetMetaData md =rs.getMetaData();
                    this.thead="<table class=\"table table-hover\">"+
                            "<thead><tr align=\"left\"><th></th><th>name</th><th>value</th><th></th>"+
                            "</tr></thead><tbody id =\"rstable\"></tbody>";
                    rslist=new ArrayList<String>();
                    jslist=new ArrayList<String>();
                    int row=1;
                    int index=Integer.valueOf(getCurrent())*10-19;
                    int pagerows=10;
                     while(rs.next()){
                      if(pagerows>0 && row>=index){
                        String list = null;
                        String jsstring="<div class=\"cqlrs\"><table class=\"table table-bordered\">";
                        list="<tr><td>"+row+"</td><td>"+md.getColumnLabel(1)+"</td><td>"+
                        rs.getString(1)+"</td>"+
                            "<td><a id=\"rs"+row+"\"  rel=\"popover\">显示详细结果</a></td></tr>";
                        
                        int n=md.getColumnCount();
                        for(int i=1;i<=n;i++){
                            jsstring=jsstring+"<tr><td>"+md.getColumnLabel(i)+"</td><td>"+rs.getString(i)+"</td></tr>";
                        }
                        jsstring=jsstring+"</table></div>";
                        jsstring="$(\"#rs"+row+"\").popover({content:'"+jsstring+"',placement:'left',html: true});";
                        rslist.add(list);
                        jslist.add(jsstring);
                        pagerows--;
                     }
                     if(pagerows==0){break;}
                        row++;
                    }
                     currentpage=Integer.valueOf(getCurrent())-1;
                      rs.close();
                      statement.close();
                      
                  }
             
             if(getTypestring().equals("goto")){
                      statement = con.createStatement();
                      String str=(String) session.get("selectstring");
                      statement.execute(str);
                      rs=statement.getResultSet();
                      ResultSetMetaData md =rs.getMetaData();
                    this.thead="<table class=\"table table-hover\">"+
                            "<thead><tr align=\"left\"><th></th><th>name</th><th>value</th><th></th>"+
                            "</tr></thead><tbody id =\"rstable\"></tbody>";
                    rslist=new ArrayList<String>();
                    jslist=new ArrayList<String>();
                    int row=1;
                    int index=Integer.valueOf(getGotopage())*10-9;
                    int pagerows=10;
                     while(rs.next()){
                      if(pagerows>0 && row>=index){
                        String list = null;
                        String jsstring="<div class=\"cqlrs\"><table class=\"table table-bordered\">";
                        list="<tr><td>"+row+"</td><td>"+md.getColumnLabel(1)+"</td><td>"+
                        rs.getString(1)+"</td>"+
                            "<td><a id=\"rs"+row+"\"  rel=\"popover\">显示详细结果</a></td></tr>";
                        
                        int n=md.getColumnCount();
                        for(int i=1;i<=n;i++){
                            jsstring=jsstring+"<tr><td>"+md.getColumnLabel(i)+"</td><td>"+rs.getString(i)+"</td></tr>";
                        }
                        jsstring=jsstring+"</table></div>";
                        jsstring="$(\"#rs"+row+"\").popover({content:'"+jsstring+"',placement:'left',html: true});";
                        rslist.add(list);
                        jslist.add(jsstring);
                        pagerows--;
                     }
                     if(pagerows==0){break;}
                        row++;
                    }
                     currentpage=Integer.valueOf(getGotopage());
                      rs.close();
                      statement.close();
                      
                  }
             
          } catch (SQLException ex) {
                Logger.getLogger(CQLexec.class.getName()).log(Level.SEVERE, null, ex);
                errorinfo=ex.getMessage();
                System.out.println( "error:"+ex.getMessage()); 
            }
        }
         return "cqlrs";
    }       
       
}

