package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Position;
import cn.wizzer.app.hospital.modules.services.PositionService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/hospital/position")
public class PositionController{
    private static final Log log = Logs.get();
    @Inject
    private PositionService positionService;

    @At("")
    @Ok("beetl:/platform/hospital/position/index.html")
    @RequiresPermissions("/platform/hospital/position")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return Result.success("获取成功",positionService.query());
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPage(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("name") String name) {
        return positionService.listPage2(pageNumber,pageSize,Sqls.create("select sys_position.*,sys_role.name as roleName from sys_position left join  sys_role on sys_position.roleId=sys_role.id where sys_position.name like '%"+(name==null?"":name)+"%' order by opAt desc").setCallback(Sqls.callback.maps()));
    }

    @At("/add")
    @Ok("beetl:/platform/hospital/position/add.html")
    @RequiresPermissions("/platform/hospital/position")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "岗位", msg = "${args[0].id}")
    public Object addDo(@Param("..")Position position, HttpServletRequest req) {
		try {
		    if(positionService.fetch(Cnd.where("name","=",position.getName()))!=null)
                return Result.error("岗位不能重名");
			positionService.insert(position);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/position/edit.html")
    @RequiresPermissions("/platform/hospital/position")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", positionService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "岗位", msg = "${args[0].id}")
    public Object editDo(@Param("..")Position position, HttpServletRequest req) {
		try {
			positionService.updateIgnoreNull(position);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @DELETE
    @RequiresAuthentication
    @SLog(tag = "岗位", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				positionService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				positionService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/position/detail.html")
    @RequiresPermissions("/platform/hospital/position")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", positionService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}
}
