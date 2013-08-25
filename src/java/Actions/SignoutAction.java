package Actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Map;

/**
 * 该类用作处理注销的请求。
 * @author yeyh10
 */
public class SignoutAction extends ActionSupport{
    @Override
    public String execute() {
        // 清空session
        ActionContext actionContext = ActionContext.getContext();
        Map session = actionContext.getSession();
        session.clear();
        return SUCCESS;
    }
}
