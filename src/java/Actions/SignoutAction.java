package Actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Map;

/**
 *
 * @author yeyh10
 */
public class SignoutAction extends ActionSupport{
    @Override
    public String execute() {
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        session.clear();
        return SUCCESS;
    }
}
