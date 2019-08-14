package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Alert;
import cn.wizzer.app.hospital.modules.services.AlertService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/hospital/alert")
public class AlertController{
    private static final Log log = Logs.get();
    @Inject
    private AlertService alertService;

    @At("")
    @Ok("beetl:/platform/hospital/alert/index.html")
    @RequiresPermissions("/platform/hospital/alert")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return alertService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/hospital/alert/add.html")
    @RequiresPermissions("/platform/hospital/alert")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("/platform/hospital/alert.add")
    @SLog(tag = "警戒值", msg = "${args[0].id}")
    public Object addDo(@Param("..")Alert alert, HttpServletRequest req) {
		try {
			alertService.insert(alert);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/alert/edit.html")
    @RequiresPermissions("/platform/hospital/alert")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", alertService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "警戒值", msg = "${args[0].id}")
    public Object editDo(@Param("..")Alert alert, HttpServletRequest req) {
		try {
			alertService.updateIgnoreNull(alert);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

}
