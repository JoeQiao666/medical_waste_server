package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Record;
import cn.wizzer.app.hospital.modules.services.RecordService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class RecordServiceImpl extends BaseServiceImpl<Record> implements RecordService {
    public RecordServiceImpl(Dao dao) {
        super(dao);
    }
}
