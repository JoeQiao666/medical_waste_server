package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.hospital.modules.models.PlatformName;
import cn.wizzer.app.hospital.modules.services.PlatformnameService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/hospital/name")
public class PlatformnameController{
    private static final Log log = Logs.get();
    @Inject
    private PlatformnameService platformNameService;

    @At("/data")
    @Ok("json:full")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return platformNameService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/editDo")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "名称", msg = "${args[0].id}")
    public Object editDo(@Param("..")PlatformName platformName, HttpServletRequest req) {
		try {
			platformNameService.updateIgnoreNull(platformName);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
}
