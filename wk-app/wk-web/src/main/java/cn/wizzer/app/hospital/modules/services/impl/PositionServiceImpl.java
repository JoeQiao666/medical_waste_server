package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Position;
import cn.wizzer.app.hospital.modules.services.PositionService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class PositionServiceImpl extends BaseServiceImpl<Position> implements PositionService {
    public PositionServiceImpl(Dao dao) {
        super(dao);
    }
}
