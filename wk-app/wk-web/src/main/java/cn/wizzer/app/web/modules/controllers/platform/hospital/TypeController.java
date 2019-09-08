package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Type;
import cn.wizzer.app.hospital.modules.services.TypeService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/hospital/type")
public class TypeController{
    private static final Log log = Logs.get();
    @Inject
    private TypeService typeService;

    @At("")
    @Ok("beetl:/platform/hospital/type/index.html")
    @RequiresPermissions("/platform/hospital/type")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns,@Param("isBottle")boolean isBottle) {
    	return  Result.success("获取成功",typeService.query(Cnd.where("isBottle","=",isBottle?1:0)));
    }

    @At("/add")
    @Ok("beetl:/platform/hospital/type/add.html")
    @RequiresPermissions("/platform/hospital/type")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("/platform/hospital/type.add")
    @SLog(tag = "垃圾分类", msg = "${args[0].id}")
    public Object addDo(@Param("..")Type type, HttpServletRequest req) {
		try {
			typeService.insert(type);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/type/edit.html")
    @RequiresPermissions("/platform/hospital/type")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", typeService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("/platform/hospital/type.edit")
    @SLog(tag = "垃圾分类", msg = "${args[0].id}")
    public Object editDo(@Param("..")Type type, HttpServletRequest req) {
		try {
			typeService.updateIgnoreNull(type);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("/platform/hospital/type.delete")
    @SLog(tag = "垃圾分类", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				typeService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				typeService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/type/detail.html")
    @RequiresPermissions("/platform/hospital/type")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", typeService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
