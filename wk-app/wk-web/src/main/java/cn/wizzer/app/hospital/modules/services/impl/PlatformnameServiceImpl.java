package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.hospital.modules.models.PlatformName;
import cn.wizzer.app.hospital.modules.services.PlatformnameService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class PlatformnameServiceImpl extends BaseServiceImpl<PlatformName> implements PlatformnameService {
    public PlatformnameServiceImpl(Dao dao) {
        super(dao);
    }
}
