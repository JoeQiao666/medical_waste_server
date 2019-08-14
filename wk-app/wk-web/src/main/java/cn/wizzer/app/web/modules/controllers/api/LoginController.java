package cn.wizzer.app.web.modules.controllers.api;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import cn.wizzer.app.hospital.modules.models.Alert;
import cn.wizzer.app.hospital.modules.models.Company;
import cn.wizzer.app.hospital.modules.models.Rubbish;
import cn.wizzer.app.hospital.modules.services.*;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysApiService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.Pagination;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@IocBean
@At("/api")
public class LoginController {
    @Inject
    private SysUserService userService;
    @Inject
    private SysApiService sysApiService;
    @Inject
    private TypeService typeService;
    @Inject
    private RubbishService rubbishService;
    @Inject
    private CompanyService companyService;
    @Inject
    private DepartmentService departmentService;
    @Inject
    private AlertService alertService;

    @At
    @Ok("json:full")
    public Object login(@Param("cardId") String id) {
        Sys_user sys_user = userService.fetch(Cnd.where("cardId", "=", id));
        sys_user = userService.fetchLinks(sys_user, "roles");
        if (sys_user != null) {
            if (sys_user.getRoles().size() > 0 && sys_user.getRoles().get(0).getCode().equals("recycler")) {
                String token = sysApiService.createJWT(Integer.MAX_VALUE, sys_user);
                Sql sql = Sqls.create("select * from user_with_other where id='" + sys_user.getId() + "'");
                sql.setCallback(Sqls.callback.maps());
                userService.dao().execute(sql);
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                map.put("user", sql.getList(NutMap.class).get(0));
                return Result.success("登录成功").addData(map);
            }
            return Result.error("无权限");
        }
        return Result.error("用户不存在");
    }

    @At
    @Ok("json:full")
    @Filters(@By(type = TokenFilter.class))
    public Object getTypes(@Param("isBottle") boolean isBottle) {
        return Result.success("获取成功", typeService.query(Cnd.where("isBottle", "=", isBottle ? 1 : 0)));
    }

    @At
    @Ok("json")
    @Filters(@By(type = TokenFilter.class))
    @AdaptBy(type = JsonAdaptor.class)
    public Object addRubbish(@Param("..") List<Rubbish> rubbishes, HttpServletRequest req) {

            for (Rubbish rubbish : rubbishes) {
                String staffId = rubbish.getStaffId();
                Sys_user user;
                if (staffId.length() == 8) {
                    user = userService.fetch(Cnd.where("cardId", "=", staffId));
                    rubbish.setStaffId(user.getId());
                } else user = userService.fetch(staffId);
                rubbish.setDepartmentId(user.getDepartmentId());
                rubbish.setStatus(0);
                rubbish.setOperatorId(null);
                rubbish.setRecyclerId(Strings.sNull(req.getAttribute("userId")));
                rubbish.setAdministratorId(null);
                rubbish.setStoreAt(null);
                rubbish.setRecycleAt(null);
                rubbish.setDelFlag(false);
                rubbishService.insert(rubbish);
            }
            return Result.success("system.success");

    }

    @At
    @Ok("json:full")
    @GET
    @Filters(@By(type = TokenFilter.class))
    public Object getRubbish(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("start") String start, @Param("end") String end, @Param("status") String status, @Param("departmentId") String departmentId, HttpServletRequest req) {
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);
        Pager pager = rubbishService.dao().createPager(pageNumber, pageSize);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from rubbish_with_name ");
        Pattern p = Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}$");
        if (status != null && status.length() != 0)
            stringBuilder.append("where status=").append(status);
        if (start != null && end != null)
            if (p.matcher(start).matches() && p.matcher(end).matches()) {
                stringBuilder.append(stringBuilder.toString().contains("where") ? " and" : " where");
                stringBuilder.append(" opAt between UNIX_TIMESTAMP('").append(start).append("') and UNIX_TIMESTAMP(DATE_ADD('").append(end).append("',interval 1 day))");
            }
        if (departmentId != null) {
            stringBuilder.append(stringBuilder.toString().contains("where") ? " and" : " where");
            stringBuilder.append(" departmentId='").append(departmentId).append("'");
        }
        stringBuilder.append(stringBuilder.toString().contains("where") ? " and" : " where");
        stringBuilder.append(" recyclerId='").append(Strings.sNull(req.getAttribute("userId"))).append("'");
        Sql sql = Sqls.create(stringBuilder.toString()).setCallback(Sqls.callback.maps());
        pager.setRecordCount((int) Daos.queryCount(rubbishService.dao(), sql));// 记录数需手动设置
        sql.setPager(pager);
        rubbishService.dao().execute(sql);
        return Result.success("获取成功", new Pagination(pageNumber, pageSize, pager.getRecordCount(), sql.getList(NutMap.class)));
    }

    @At
    @Ok("json:full")
    @GET
    @Filters(@By(type = TokenFilter.class))
    public Object getOneRubbish(@Param("id") String id, HttpServletRequest req) {
        Sql sql = Sqls.create("select * from rubbish_with_name where id='" + id + "'and recyclerId='" + Strings.sNull(req.getAttribute("userId") + "'"));
        sql.setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return Result.success("获取成功", sql.getList(NutMap.class).get(0));
    }

    @At
    @Ok("json")
    @PUT
    @Filters(@By(type = TokenFilter.class))
    public Object store(@Param("ids") String[] ids, @Param("administratorId") String administratorId, HttpServletRequest req) {
        int i = 0;
        for (String id : ids) {
            Rubbish rubbish = rubbishService.fetch(id);
            if (rubbish != null) {
                if (!rubbish.getRecyclerId().equals(Strings.sNull(req.getAttribute("userId")))) continue;
                if (administratorId == null) continue;
                rubbish.setStatus(1);
                rubbish.setStoreAt((int) (System.currentTimeMillis() / 1000));
                if(administratorId.length()==8)
                {Sys_user user=userService.fetch(Cnd.where("cardId", "=",administratorId));
                    rubbish.setAdministratorId(user.getId());
                }
               else
                rubbish.setAdministratorId(administratorId);
                rubbishService.update(rubbish);
                i++;
            }
        }
        Sql sql = Sqls.create("select (select sum(weight) from rubbish where status!=2)/sum(weight)*100 percent from rubbish").setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        double percent = (double) sql.getList(NutMap.class).get(0).get("percent");
        Alert alert = alertService.query().get(0);
        try {
        JPushClient jpushClient = new JPushClient("f5f636633ed555ae30ebf910", "13fc8b32c7627efe5a351bfb", null, ClientConfig.getInstance());
        if (percent > alert.getPercent()) {
            PushResult result = jpushClient.sendPush(PushPayload.newBuilder()
                    .setPlatform(Platform.android())
                    .setAudience(Audience.all())
                    .setNotification(Notification.android("您的医疗废品已超过" + alert.getPercent() + "%未处理，请及时处理", "异常信息", null))
                    .build());
        }
        return Result.success(i + "件入库");} catch (Exception e) {
            if (e instanceof APIRequestException)
                return Result.success("system.success");
            return Result.error("system.error");
        }
    }

    @At
    @Ok("json")
    @PUT
    @Filters(@By(type = TokenFilter.class))
    public Object recycle(@Param("ids") String[] ids, @Param("companyId") String companyId, HttpServletRequest req) {
        int i = 0;
        for (String id : ids) {
            Rubbish rubbish = rubbishService.fetch(id);
            if (rubbish != null) {
                if (!rubbish.getRecyclerId().equals(Strings.sNull(req.getAttribute("userId")))) continue;
                if (companyId == null) continue;
                if(companyId.length()==8){
                    Company company=companyService.fetch(Cnd.where("cardId", "=",companyId));
                    rubbish.setCompanyId(company.getId());
                }
                else rubbish.setCompanyId(companyId);
                rubbish.setStatus(2);
                rubbish.setRecycleAt((int) (System.currentTimeMillis() / 1000));
                rubbishService.update(rubbish);
                i++;
            }
        }
        return Result.success(i + "件出库");
    }

    @At
    @Ok("json:full")
    @GET
    @Filters(@By(type = TokenFilter.class))
    public Object getCompanies(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("name") String name) {
        return Result.success("获取成功", companyService.listPage(pageNumber, pageSize, Cnd.where("name", "like", "%" + (name != null ? name : "") + "%")));
    }

    @At
    @Ok("json:full")
    @GET
    @Filters(@By(type = TokenFilter.class))
    public Object getDepartmentById(@Param("id") String id) {
        return Result.success("获取成功", departmentService.fetch(id));
    }

    /**
     * 默认页码
     *
     * @param pageNumber
     * @return
     */
    protected int getPageNumber(Integer pageNumber) {
        return Lang.isEmpty(pageNumber) ? 1 : pageNumber;
    }

    /**
     * 默认页大小
     *
     * @param pageSize
     * @return
     */
    protected int getPageSize(int pageSize) {
        return pageSize == 0 ? 10 : pageSize;
    }
}