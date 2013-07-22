/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import java.util.Map;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
/**
 *
 * @author yeyh10
 */
public class SigninAction extends ActionSupport{
    private String username;
    private String password;

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public String execute() throws Exception {
        if (getUsername().equals("user") && getPassword().equals("123456")) {
            ActionContext actionContext = ActionContext.getContext();
            Map session = actionContext.getSession();
            session.put("user", "user");
            return SUCCESS;
        }
        else {
            return ERROR;
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
}
