package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Record;
import cn.wizzer.app.hospital.modules.services.RecordService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/hospital/record")
public class RecordController{
    private static final Log log = Logs.get();
    @Inject
    private RecordService recordService;

    @At("")
    @Ok("beetl:/platform/hospital/record/index.html")
    @RequiresPermissions("/platform/hospital/record")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("/platform/hospital/record")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return recordService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPage(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize){
        return recordService.listPage2(pageNumber,pageSize, Sqls.create("select record.*,sys_user.username from record left join sys_user on sys_user.id=record.operatorId order by opAt desc").setCallback(Sqls.callback.maps()));
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object getByRubbishId(@Param("rubbishId") String rubbishId){
        Sql sql=Sqls.create("select record.*,sys_user.username from record left join sys_user on sys_user.id=record.operatorId where rubbishId='"+rubbishId+"'");
        sql.setCallback(Sqls.callback.maps());
        recordService.dao().execute(sql);
        return Result.success("获取成功",sql.getList(NutMap.class).get(0));
    }

    @At("/add")
    @Ok("beetl:/platform/hospital/record/add.html")
    @RequiresPermissions("/platform/hospital/record")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("/platform/hospital/record.add")
    @SLog(tag = "异常记录", msg = "${args[0].id}")
    public Object addDo(@Param("..")Record record, HttpServletRequest req) {
		try {
			recordService.insert(record);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/record/edit.html")
    @RequiresPermissions("/platform/hospital/record")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", recordService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("/platform/hospital/record.edit")
    @SLog(tag = "异常记录", msg = "${args[0].id}")
    public Object editDo(@Param("..")Record record, HttpServletRequest req) {
		try {
            record.setOpBy(StringUtil.getUid());
			record.setOpAt((int) (System.currentTimeMillis() / 1000));
			recordService.updateIgnoreNull(record);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("/platform/hospital/record.delete")
    @SLog(tag = "异常记录", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				recordService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				recordService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/record/detail.html")
    @RequiresPermissions("/platform/hospital/record")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", recordService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
