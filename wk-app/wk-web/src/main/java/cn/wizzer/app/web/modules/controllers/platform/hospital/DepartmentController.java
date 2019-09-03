package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Department;
import cn.wizzer.app.hospital.modules.services.DepartmentService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
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
@At("/platform/hospital/department")
public class DepartmentController{
    private static final Log log = Logs.get();
    @Inject
    private DepartmentService departmentService;

    @At("")
    @Ok("beetl:/platform/hospital/department/index.html")
    @RequiresAuthentication
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
    	return departmentService.data(length, start, draw, order, columns, Cnd.where("delFlag","=",0), null);
    }
    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPage(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,@Param("name")String name){
        return departmentService.listPage(pageNumber,pageSize, Cnd.where("name","like","%"+(name!=null?name:"")+"%").and("delFlag","=",0));
    }
    @At("/add")
    @Ok("beetl:/platform/hospital/department/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "科室", msg = "${args[0].id}")
    public Object addDo(@Param("..")Department department, HttpServletRequest req) {
		try {
			departmentService.insert(department);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At
    @Ok("json")
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    public Object batch(@Param("..")List<Department> departments) {
        try {
            for (Department department:departments)
            departmentService.insert(department);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/department/edit.html")
    @RequiresPermissions("/platform/hospital/department")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", departmentService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "科室", msg = "${args[0].id}")
    public Object editDo(@Param("..")Department department, HttpServletRequest req) {
		try {
            department.setOpBy(StringUtil.getUid());
			department.setOpAt((int) (System.currentTimeMillis() / 1000));
			departmentService.updateIgnoreNull(department);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @DELETE
    @RequiresAuthentication
    @SLog(tag = "科室", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				departmentService.vDelete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				departmentService.vDelete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/department/detail.html")
    @RequiresPermissions("/platform/hospital/department")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", departmentService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
