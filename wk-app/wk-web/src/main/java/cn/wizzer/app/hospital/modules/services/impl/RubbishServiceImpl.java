package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Rubbish;
import cn.wizzer.app.hospital.modules.services.RubbishService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class RubbishServiceImpl extends BaseServiceImpl<Rubbish> implements RubbishService {
    public RubbishServiceImpl(Dao dao) {
        super(dao);
    }
}
