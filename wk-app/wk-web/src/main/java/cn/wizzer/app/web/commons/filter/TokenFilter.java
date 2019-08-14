package cn.wizzer.app.web.commons.filter;

import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysApiService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.sys.modules.services.impl.SysApiServiceImpl;
import cn.wizzer.app.sys.modules.services.impl.SysUserServiceImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;


/**
 * Created by wizzer on 2016/8/11.
 */
public class TokenFilter implements ActionFilter {
    private static final Log log = Logs.get();
    private SysApiService apiService= Mvcs.ctx().getDefaultIoc().get(SysApiServiceImpl.class);
    private SysUserService userService=Mvcs.ctx().getDefaultIoc().get(SysUserServiceImpl.class);
    public View match(ActionContext context) {
        String token = context.getRequest().getHeader("Authorization");
        String userId;
        try {
            userId = JWT.decode(token).getClaim("id").asString();
        } catch (JWTDecodeException j) {
            throw new RuntimeException("访问异常！");
        }
        Sys_user user = userService.fetch(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在，请重新登录");
        }
        Boolean verify = apiService.isVerify(token, user);
        if (!verify) {
            throw new RuntimeException("非法访问！");
        }
        context.getRequest().setAttribute("userId",userId);
        return null;
    }


}
