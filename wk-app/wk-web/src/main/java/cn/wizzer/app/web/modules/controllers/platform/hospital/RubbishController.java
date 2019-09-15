package cn.wizzer.app.web.modules.controllers.platform.hospital;

import cn.wizzer.app.hospital.modules.models.Rubbish;
import cn.wizzer.app.hospital.modules.models.Type;
import cn.wizzer.app.hospital.modules.services.RubbishService;
import cn.wizzer.app.hospital.modules.services.TypeService;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.Pagination;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

@IocBean
@At("/platform/hospital/rubbish")
public class RubbishController {
    private static final Log log = Logs.get();
    @Inject
    private RubbishService rubbishService;
    @Inject
    private TypeService typeService;
    @At("")
    @Ok("beetl:/platform/hospital/rubbish/index.html")
    @RequiresPermissions("/platform/hospital/rubbish")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        return rubbishService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPage(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,@Param("start")String start,@Param("end")String end,@Param("status")String status) {
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);
        Pager pager = rubbishService.dao().createPager(pageNumber, pageSize);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select * from rubbish_with_name ");
        Pattern p=Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");
        if(status!=null&&status.length()!=0)
        stringBuilder.append("where status=").append(status);
        if(start!=null&&end!=null)
        if( p.matcher(start).matches()&&p.matcher(end).matches())
        {  if(status!=null&&status.length()!=0)
            stringBuilder.append(" and");
           else stringBuilder.append(" where");
            stringBuilder.append(" opAt between UNIX_TIMESTAMP('").append(start).append("') and UNIX_TIMESTAMP(DATE_ADD('").append(end).append("',interval 1 day))");
        }
        Sql sql=Sqls.create(stringBuilder.toString()).setCallback(Sqls.callback.maps());
        pager.setRecordCount((int) Daos.queryCount(rubbishService.dao(), sql));// 记录数需手动设置
        sql.setPager(pager);
        rubbishService.dao().execute(sql);
        return new Pagination(pageNumber, pageSize, pager.getRecordCount(), sql.getList(NutMap.class));
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPageToday(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("isBottle") boolean isBottle) {
        return rubbishService.listPage2(pageNumber,pageSize,Sqls.create("select * from rubbish_with_name_today where isBottle="+(isBottle?1:0)));
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object weightPerDayInLastMonth(@Param("isBottle") boolean isBottle) {
        Sql sql=Sqls.create("SELECT\n" +
                "\tcast(\n" +
                "\t\tsum(`rubbish`.`weight`) AS DECIMAL (10, 2)\n" +
                "\t) AS `weight`,\n" +
                "\tdate_format(\n" +
                "\t\tfrom_unixtime(`rubbish`.`opAt`),\n" +
                "\t\t'%Y-%m-%d'\n" +
                "\t) AS `date`\n" +
                "FROM\n" +
                "\t`rubbish`\n" +
                "WHERE\n" +
                "\t(\n" +
                "\t\t(\n" +
                "\t\t\tto_days(now()) - to_days(\n" +
                "\t\t\t\tfrom_unixtime(`rubbish`.`opAt`)\n" +
                "\t\t\t)\n" +
                "\t\t) <= 30\n" +
                "\tand  isBottle="+(isBottle?1:0)+
                ")\tGROUP BY\n" +
                "\tdate_format(\n" +
                "\t\tfrom_unixtime(`rubbish`.`opAt`),\n" +
                "\t\t'%Y-%m-%d'\n" +
                "\t)\n" +
                "ORDER BY\n" +
                "\t`date` DESC").setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object percentInLastMonth(@Param("isBottle") boolean isBottle) {
        Sql sql=Sqls.create("select * from last_month_percent where isBottle="+(isBottle?1:0)).setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object weightPerMonthInLastYear(@Param("isBottle")boolean isBottle){
        Sql sql=Sqls.create("select * from last_year_weight_per_month where isBottle="+(isBottle?1:0)).setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object percentInLastYear(@Param("isBottle")boolean isBottle){
        Sql sql=Sqls.create("select * from last_year_percent where isBottle="+(isBottle?1:0)).setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object weightPerDayInLastMonthByDepartment(@Param("isBottle")boolean isBottle){
        Sql sql=Sqls.create("select * from last_month_total_weight_department where isBottle="+(isBottle?1:0)).setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object weightPerDayByDepartment(@Param("start")String start,@Param("end")String end,@Param("name")String name,@Param("isBottle")boolean isBottle,@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,@Param("status")String status){
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT\n" +
                "\tcast(\n" +
                "\t\tsum(\n" +
                "\t\t\t`rubbish_by_department_type`.`total`\n" +
                "\t\t) AS DECIMAL (10, 2)\n" +
                "\t) AS `total`,\n" +
                "\tgroup_concat(\n" +
                "\t\t`rubbish_by_department_type`.`typeName` SEPARATOR ','\n" +
                "\t) AS `typeNames`,\n" +
                "\tgroup_concat(\n" +
                "\t\t`rubbish_by_department_type`.`total` SEPARATOR ','\n" +
                "\t) AS `totals`,\n" +
                "\t`rubbish_by_department_type`.`departmentName` AS `departmentName`\n" +
                "FROM\n" +
                "\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tcast(\n" +
                "\t\t\t\tsum(\n" +
                "\t\t\t\t\t`nutzwk`.`rubbish`.`weight`\n" +
                "\t\t\t\t) AS DECIMAL (10, 2)\n" +
                "\t\t\t) AS `total`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`isBottle` AS `isBottle`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`departmentId` AS `departmentId`,\n" +
                "\t\t\t`nutzwk`.`department`.`name` AS `departmentName`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`typeId` AS `typeId`,\n" +
                "\t\t\t`nutzwk`.`type`.`name` AS `typeName`\n" +
                "\t\tFROM\n" +
                "\t\t\t(\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\t\t`nutzwk`.`rubbish`\n" +
                "\t\t\t\t\tLEFT JOIN `nutzwk`.`department` ON (\n" +
                "\t\t\t\t\t\t(\n" +
                "\t\t\t\t\t\t\t`nutzwk`.`department`.`id` = `nutzwk`.`rubbish`.`departmentId`\n" +
                "\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t)\n" +
                "\t\t\t\tLEFT JOIN `nutzwk`.`type` ON (\n" +
                "\t\t\t\t\t(\n" +
                "\t\t\t\t\t\t`nutzwk`.`type`.`id` = `nutzwk`.`rubbish`.`typeId`\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t)\n" +
                "\t\t\t)\n"
                );
        Pattern p = Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");
        if( p.matcher(start).matches()&&p.matcher(end).matches())
            stringBuilder.append(" where rubbish.opAt between UNIX_TIMESTAMP('").append("') and UNIX_TIMESTAMP(DATE_ADD('").append(end).append("',interval 1 day))");
        else return Result.error("日期不正确");
        stringBuilder.append(" and department.name like '%").append(name == null ? "" : name).append("%'");
        stringBuilder.append(" and `nutzwk`.`rubbish`.`isBottle`=").append(isBottle ? 1 : 0);
        if(status!=null)
        stringBuilder.append(" and status=").append(status);
        stringBuilder.append("\t\tGROUP BY\n" +
                "\t\t\t`nutzwk`.`rubbish`.`departmentId`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`typeId`\n" +
                "\t) `rubbish_by_department_type`\n" +
                "GROUP BY\n" +
                "\t`rubbish_by_department_type`.`departmentId`\n" +
                "ORDER BY `rubbish_by_department_type`.`departmentId`");
        Sql sql=Sqls.create(stringBuilder.toString());
        return rubbishService.listPage2(pageNumber,pageSize,sql);
    }
    @At
    @Ok("raw")
    @GET
    @RequiresAuthentication
    public File exportWeightByDepartment(@Param("isBottle")boolean isBottle,@Param("status")String status) throws IOException {
        Sql sql=Sqls.create("SELECT\n" +
                "\tcast(\n" +
                "\t\tsum(\n" +
                "\t\t\t`rubbish_by_department_type`.`total`\n" +
                "\t\t) AS DECIMAL (10, 2)\n" +
                "\t) AS `total`,\n" +
                "\tgroup_concat(\n" +
                "\t\t`rubbish_by_department_type`.`typeName` SEPARATOR ','\n" +
                "\t) AS `typeNames`,\n" +
                "\tgroup_concat(\n" +
                "\t\t`rubbish_by_department_type`.`total` SEPARATOR ','\n" +
                "\t) AS `totals`,\n" +
                "\t`rubbish_by_department_type`.`departmentName` AS `departmentName`\n" +
                "FROM\n" +
                "\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tcast(\n" +
                "\t\t\t\tsum(\n" +
                "\t\t\t\t\t`nutzwk`.`rubbish`.`weight`\n" +
                "\t\t\t\t) AS DECIMAL (10, 2)\n" +
                "\t\t\t) AS `total`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`isBottle` AS `isBottle`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`departmentId` AS `departmentId`,\n" +
                "\t\t\t`nutzwk`.`department`.`name` AS `departmentName`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`typeId` AS `typeId`,\n" +
                "\t\t\t`nutzwk`.`type`.`name` AS `typeName`\n" +
                "\t\tFROM\n" +
                "\t\t\t(\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\t\t`nutzwk`.`rubbish`\n" +
                "\t\t\t\t\tLEFT JOIN `nutzwk`.`department` ON (\n" +
                "\t\t\t\t\t\t(\n" +
                "\t\t\t\t\t\t\t`nutzwk`.`department`.`id` = `nutzwk`.`rubbish`.`departmentId`\n" +
                "\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t)\n" +
                "\t\t\t\tLEFT JOIN `nutzwk`.`type` ON (\n" +
                "\t\t\t\t\t(\n" +
                "\t\t\t\t\t\t`nutzwk`.`type`.`id` = `nutzwk`.`rubbish`.`typeId`\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t)\n" +
                "\t\t\t)\n" +
                "\t\tWHERE\n" +
                "\t\t\t(\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\t\t(\n" +
                "\t\t\t\t\t\tto_days(now()) - to_days(\n" +
                "\t\t\t\t\t\t\tfrom_unixtime(`nutzwk`.`rubbish`.`opAt`)\n" +
                "\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t) <= 30\n" +
                "\t\t\t\t)\n" +
                "\t\t\t\tAND (\n" +
                "\t\t\t\t\t`nutzwk`.`rubbish`.`isBottle` = "+(isBottle?1:0)+"\n" +
                "\t\t\t\t)\n" +
                (status!=null?"AND `nutzwk`.`rubbish`.`status` = "+status:"") +
                "\t\t\t)\n" +
                "\t\tGROUP BY\n" +
                "\t\t\t`nutzwk`.`rubbish`.`departmentId`,\n" +
                "\t\t\t`nutzwk`.`rubbish`.`typeId`\n" +
                "\t) `rubbish_by_department_type`\n" +
                "GROUP BY\n" +
                "\t`rubbish_by_department_type`.`departmentId`\n" +
                "ORDER BY\n" +
                "\t`rubbish_by_department_type`.`departmentId`");
        sql.setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        List<NutMap> list=sql.getList(NutMap.class);
        List<Type> types=typeService.query(Cnd.where("isBottle","=",isBottle?1:0));
        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("病区");
        row.createCell(2).setCellValue("总重(kg)");
        float total=0f;
        NutMap totalWeightMap=new NutMap();
        int j=3;
        for(Type type:types){
            row.createCell(j++).setCellValue(type.getName()+"总重(kg)");
            totalWeightMap.put(type.getName(),0f);
        }
        int i=1;
        for(NutMap nutMap:list){
            Row row0 = sheet.createRow(i);
            row0.createCell(0).setCellValue(i);
            row0.createCell(1).setCellValue(nutMap.getString("departmentName"));
            row0.createCell(2).setCellValue(nutMap.getString("total"));
            total+=Float.valueOf(nutMap.getString("total"));
            NutMap weightMap=new NutMap();
            String[] totals=nutMap.getString("totals").split(",");
            String[] names=nutMap.getString("typeNames").split(",");
            for(int k=0;k<totals.length;k++){
               weightMap.put(names[k],totals[k]);
               totalWeightMap.put(names[k],Float.valueOf(totals[k])+(float)totalWeightMap.get(names[k]));
            }
            int l=3;
            for (Type type:types){
                 String value=weightMap.getString(type.getName());
                 row0.createCell(l++).setCellValue(Strings.isEmpty(value)?"0":value);
            }
            i++;
        }
        Row row0 = sheet.createRow(i);
        row0.createCell(2).setCellValue(String.valueOf(total));
        int m=3;
        for (Type type:types){
            row0.createCell(m++).setCellValue(String.valueOf((float)totalWeightMap.get(type.getName())));
        }
        File file=new File("/excel");
        if(!file.exists())file.mkdir();
        String name="科室报表";
        if(status!=null)
        switch (status){
            case "0":name="待入库列表";
            break;
            case "1":name="入库列表";
            break;
            case "2":name="出库列表";
            break;
        }
        try (OutputStream fileOut = new FileOutputStream("/excel/"+name+".xlsx")) {
            wb.write(fileOut);
        }
        return new File("/excel/"+name+".xlsx");
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object weightPerType(@Param("formatType")String type,@Param("start")String start,@Param("end")String end,@Param("isBottle")boolean isBottle,@Param("status")int status,@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize){
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        String format;
        Pattern p ;
        switch(type){
            case "day":
                format="%Y-%m-%d";
                p=Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");
                break;
            case "month":
                format="%Y-%m";
                p=Pattern.compile("^\\d{4}\\-\\d{1,2}$");
                break;
            case "year":
                format="%Y";
                p=Pattern.compile("^\\d{4}$");
                break;
            default:return Result.error("筛选类型错误");
        }
        stringBuilder.append("SELECT\n" + "\t`rubbish_by_type`.`storeTime` AS `storeTime`,\n" + "\t`rubbish_by_type`.`isBottle` AS `isBottle`,\n" + "\tcast(\n" + "\t\tsum(`rubbish_by_type`.`total`) AS DECIMAL (10, 2)\n" + "\t) AS `total`,\n" + "\tgroup_concat(\n" + "\t\t`rubbish_by_type`.`typeName` SEPARATOR ','\n" + "\t) AS `typeNames`,\n" + "\tgroup_concat(\n" + "\t\t`rubbish_by_type`.`total` SEPARATOR ','\n" + "\t) AS `totals`\n" + "FROM\n" + "\t(\n" + "\t\tSELECT\n" + "\t\t\tcast(\n" + "\t\t\t\tsum(\n" + "\t\t\t\t\t`nutzwk`.`rubbish`.`weight`\n" + "\t\t\t\t) AS DECIMAL (10, 2)\n" + "\t\t\t) AS `total`,\n" + "\t\t\t`nutzwk`.`rubbish`.`isBottle` AS `isBottle`,\n" + "\t\t\t`nutzwk`.`rubbish`.`typeId` AS `typeId`,\n" + "\t\t\tdate_format(\n" + "\t\t\t\tfrom_unixtime(\n" + "\t\t\t\t\t`nutzwk`.`rubbish`.`storeAt`\n" + "\t\t\t\t),\n" + "\t\t\t\t'").append(format).append("'\n").append("\t\t\t) AS `storeTime`,\n").append("\t\t\t`nutzwk`.`type`.`name` AS `typeName`\n").append("\t\tFROM\n").append("\t\t\t(\n").append("\t\t\t\t`nutzwk`.`rubbish`\n").append("\t\t\t\tLEFT JOIN `nutzwk`.`type` ON (\n").append("\t\t\t\t\t(\n").append("\t\t\t\t\t\t`nutzwk`.`type`.`id` = `nutzwk`.`rubbish`.`typeId`\n").append("\t\t\t\t\t)\n").append("\t\t\t\t)\n").append("\t\t\t)\n").append("\t\tWHERE\n").append("\t\t\t(\n").append("\t\t\t\t`nutzwk`.`rubbish`.`status` = \n").append(status).append("\t\t\t)\n");
        if( p.matcher(start).matches()&&p.matcher(end).matches())
            stringBuilder.append(" and nutzwk.rubbish.storeAt between UNIX_TIMESTAMP('").append(start).append("') and UNIX_TIMESTAMP(DATE_ADD('").append(end).append("',interval 1 day))");
        else return Result.error("日期不正确");
        stringBuilder.append(" and nutzwk.rubbish.isBottle=").append(isBottle ? 1 : 0);
        stringBuilder.append("\t\tGROUP BY from_unixtime(`nutzwk`.`rubbish`.`storeAt`,'").append(format).append("'\n").append("\t\t\t),\n").append("\t\t\t`nutzwk`.`rubbish`.`typeId`\n").append("\t) `rubbish_by_type`\n").append("GROUP BY\n").append("\t`rubbish_by_type`.`storeTime`");
        Sql sql=Sqls.create(stringBuilder.toString()).setCallback(Sqls.callback.maps());
        rubbishService.dao().execute(sql);
        return rubbishService.listPage2(pageNumber,pageSize,sql);
    }

    @At
    @Ok("json:full")
    @GET
    @RequiresAuthentication
    public Object listPageByDate(@Param("formatType")String type,@Param("date")String date,@Param("status")int status,@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("isBottle") boolean isBottle){
        String format;
        Pattern p ;
        String value;
        switch(type){
            case "day":
                format="%Y-%m-%d";
                p=Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");
                break;
            case "month":
                format="%Y-%m";
                p=Pattern.compile("^\\d{4}\\-\\d{1,2}$");
                break;
            case "year":
                format="%Y";
                p=Pattern.compile("^\\d{4}$");
                break;
            default:return Result.error("筛选类型错误");
        }
        switch(status){
            case 1:
                value="rubbish_with_name.storeAt";
                break;
                case 2:
                    value="rubbish_with_name.recycleAt";
                    break;
                    default:return Result.error("状态错误");
        }
        if(! p.matcher(date).matches())
            return Result.error("日期不正确");
        Sql sql=Sqls.create("select * from rubbish_with_name where isBottle="+(isBottle?1:0)+" and from_unixtime("+value+",'"+format+"')='"+date+"'");
        return rubbishService.listPage2(pageNumber,pageSize,sql);
    }

    @At
    @Ok("json:full")
    @PUT
    @RequiresAuthentication
    @AdaptBy(type = WhaleAdaptor.class)
    public Object editType(@Param("id")String id,@Param("typeId")String typeId){
        if(typeId==null||typeId.length()==0)
            return Result.error("类型id不能为空");
         Rubbish rubbish=rubbishService.fetch(id);
         rubbish.setTypeId(typeId);
         rubbishService.update(rubbish);
        return Result.success("system.success");
    }

    @At("/add")
    @Ok("beetl:/platform/hospital/rubbish/add.html")
    @RequiresPermissions("/platform/hospital/rubbish")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresAuthentication
    @AdaptBy(type=JsonAdaptor.class)
    @SLog(tag = "垃圾", msg = "${args[0].id}")
    public Object addDo(@Param("..") Rubbish rubbish, HttpServletRequest req) {
        try {
            rubbish.setId(R.UU32().toLowerCase());
            rubbishService.insert(rubbish);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/store")
    @Ok("json")
    @PUT
    @RequiresAuthentication
    @SLog(tag = "垃圾",msg = "${req.getAttribute('id')}")
    public Object store(@Param("ids") String[] ids) {
        try {
            for (String id :ids){
                Rubbish rubbish=rubbishService.fetch(id);
                rubbish.setStatus(1);
                rubbish.setStoreAt((int)(System.currentTimeMillis()/1000));
                rubbishService.updateIgnoreNull(rubbish);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/hospital/rubbish/edit.html")
    @RequiresPermissions("/platform/hospital/rubbish")
    public void edit(String id, HttpServletRequest req) {
        req.setAttribute("obj", rubbishService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("/platform/hospital/rubbish.edit")
    @SLog(tag = "垃圾", msg = "${args[0].id}")
    public Object editDo(@Param("..") Rubbish rubbish, HttpServletRequest req) {
        try {
            rubbish.setOpBy(StringUtil.getUid());
            rubbish.setOpAt((int) (System.currentTimeMillis() / 1000));
            rubbishService.updateIgnoreNull(rubbish);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("/platform/hospital/rubbish.delete")
    @SLog(tag = "垃圾", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                rubbishService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                rubbishService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/hospital/rubbish/detail.html")
    @RequiresPermissions("/platform/hospital/rubbish")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("obj", rubbishService.fetch(id));
        } else {
            req.setAttribute("obj", null);
        }
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
