package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Alert;
import cn.wizzer.app.hospital.modules.services.AlertService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class AlertServiceImpl extends BaseServiceImpl<Alert> implements AlertService {
    public AlertServiceImpl(Dao dao) {
        super(dao);
    }
}
