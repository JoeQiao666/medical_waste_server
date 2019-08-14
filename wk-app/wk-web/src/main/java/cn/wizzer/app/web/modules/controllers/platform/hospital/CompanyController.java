package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Company;
import cn.wizzer.app.hospital.modules.services.CompanyService;
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
@At("/platform/hospital/company")
public class CompanyController{
    private static final Log log = Logs.get();
    @Inject
    private CompanyService companyService;

    @At("")
    @Ok("beetl:/platform/hospital/company/index.html")
    @RequiresPermissions("/platform/hospital/company")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("/platform/hospital/company")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return companyService.data(length, start, draw, order, columns, cnd, null);
    }
    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPage(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,@Param("name")String name){
        return companyService.listPage(pageNumber,pageSize, Cnd.where("name","like","%"+(name!=null?name:"")+"%"));
    }
    @At("/add")
    @Ok("beetl:/platform/hospital/company/add.html")
    @RequiresPermissions("/platform/hospital/company")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresAuthentication
    @AdaptBy(type=JsonAdaptor.class)
    @SLog(tag = "公司", msg = "${args[0].id}")
    public Object addDo(@Param("..")Company company, HttpServletRequest req) {
		try {
			companyService.insert(company);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/company/edit.html")
    @RequiresPermissions("/platform/hospital/company")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", companyService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type=JsonAdaptor.class)
    @SLog(tag = "公司", msg = "${args[0].id}")
    public Object editDo(@Param("..")Company company, HttpServletRequest req) {
		try {
            company.setOpBy(StringUtil.getUid());
			company.setOpAt((int) (System.currentTimeMillis() / 1000));
			companyService.updateIgnoreNull(company);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @DELETE
    @RequiresAuthentication
    @SLog(tag = "公司", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				companyService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				companyService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/company/detail.html")
    @RequiresPermissions("/platform/hospital/company")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", companyService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
