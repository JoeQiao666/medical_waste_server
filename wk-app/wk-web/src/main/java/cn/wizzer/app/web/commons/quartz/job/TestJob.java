package cn.wizzer.app.web.commons.quartz.job;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import cn.wizzer.app.hospital.modules.models.Alert;
import cn.wizzer.app.hospital.modules.models.Record;
import cn.wizzer.app.hospital.modules.models.Rubbish;
import cn.wizzer.app.hospital.modules.services.AlertService;
import cn.wizzer.app.hospital.modules.services.RecordService;
import cn.wizzer.app.hospital.modules.services.RubbishService;
import cn.wizzer.app.sys.modules.models.Sys_task;
import cn.wizzer.app.web.modules.controllers.platform.hospital.RubbishController;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.jpush.api.push.model.notification.PlatformNotification.ALERT;

/**
 * Created by Wizzer.cn on 2015/6/27.
 */
@IocBean
public class TestJob implements Job {

    private static final Log log = Logs.get();
    @Inject
    protected Dao dao;

    @Inject
    protected RecordService recordService;

    @Inject
    protected RubbishService rubbishService;

    @Inject
    protected AlertService alertService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
       /* JobDataMap data = context.getJobDetail().getJobDataMap();
        String taskId = context.getJobDetail().getKey().getName();
        String hi = data.getString("hi");
        log.info("Test Job hi::" + hi);
        dao.update(Sys_task.class, Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行成功"), Cnd.where("id", "=", taskId));*/
//        Alert alert=alertService.query().get(0);
//        List<Rubbish> rubbishes=rubbishService.query(Cnd.where("status","=",1).and("(UNIX_TIMESTAMP()-storeAt)",">",alert.getTime()*3600));
//        for (Rubbish rubbish:rubbishes){
//             if(recordService.query(Cnd.where("rubbishId","=",rubbish.getId())).size()==0){
//                 Record record=new Record();
//                 record.setOperatorId(rubbish.getRecyclerId());
//                 record.setStatus(rubbish.getStatus());
//                 record.setStoreAt(rubbish.getStoreAt());
//                 record.setDetail("超时出库");
//                 record.setReviewWeight(rubbish.getWeight());
//                 record.setReviewWeight(rubbish.getWeight());
//                 recordService.insert(record);
//             }
//        }

        JPushClient jpushClient = new JPushClient("f5f636633ed555ae30ebf910", "13fc8b32c7627efe5a351bfb", null, ClientConfig.getInstance());
        List<Record> records=recordService.query(Cnd.where("pushFlag","=",0));
        Alert alert=alertService.query().get(0);
        for(Record record:records){

            // For push, all you need do is to build PushPayload object.

        try {
            Map<String,String> map=new HashMap<>();
            map.put("id",record.getRubbishId());
            PushResult result = jpushClient.sendPush(PushPayload.newBuilder()
                    .setPlatform(Platform.android())
                    .setAudience(Audience.alias(record.getOperatorId()))
                    .setNotification(Notification.android("您的医疗废品已超过"+alert.getTime()+"小时未处理，请及时处理", "异常信息", map))
                    .build());
            record.setPushFlag(true);
            recordService.update(record);
        } catch (APIConnectionException e) {
            // Connection error, should retry later
            e.printStackTrace();
        } catch (APIRequestException e) {
            // Should review the error, and fix the request
            e.printStackTrace();
        }}
    }



     private PushPayload buildPushObject_all_all_alert() {
        return PushPayload.alertAll(ALERT);
    }
}
