package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Car;
import cn.wizzer.app.hospital.modules.services.CarService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
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
@At("/platform/hospital/car")
public class CarController{
    private static final Log log = Logs.get();
    @Inject
    private CarService carService;

    @At("")
    @Ok("beetl:/platform/hospital/car/index.html")
    @RequiresPermissions("/platform/hospital/car")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("/platform/hospital/car")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return carService.data(length, start, draw, order, columns, cnd, null);
    }
    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPage(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("name") String name) {
        return carService.listPage2(pageNumber,pageSize, Sqls.create("select car.*,sys_user.username from car left join  sys_user on sys_user.id=car.recyclerId where car.name like '%"+(name==null?"":name)+"%' order by opAt desc"));
    }

    @At("/add")
    @Ok("beetl:/platform/hospital/car/add.html")
    @RequiresPermissions("/platform/hospital/car")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "回收车", msg = "${args[0].id}")
    public Object addDo(@Param("..")Car car, HttpServletRequest req) {
		try {
			carService.insert(car);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/car/edit.html")
    @RequiresPermissions("/platform/hospital/car")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", carService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type= JsonAdaptor.class)
    @SLog(tag = "回收车", msg = "${args[0].id}")
    public Object editDo(@Param("..")Car car, HttpServletRequest req) {
		try {
            car.setOpBy(StringUtil.getUid());
			car.setOpAt((int) (System.currentTimeMillis() / 1000));
			carService.updateIgnoreNull(car);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @DELETE
    @RequiresAuthentication
    @SLog(tag = "回收车", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				carService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				carService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/car/detail.html")
    @RequiresPermissions("/platform/hospital/car")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", carService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
