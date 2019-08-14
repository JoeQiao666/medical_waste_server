package cn.wizzer.app.hospital.modules.services.impl;

import cn.wizzer.app.hospital.modules.models.Car;
import cn.wizzer.app.hospital.modules.services.CarService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class CarServiceImpl extends BaseServiceImpl<Car> implements CarService {
    public CarServiceImpl(Dao dao) {
        super(dao);
    }
}
