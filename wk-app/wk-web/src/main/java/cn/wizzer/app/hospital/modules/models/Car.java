package cn.wizzer.app.hospital.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("回收车")
@Table("car")
public class Car extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;
    @Column
    @Comment("重量")
    @ColDefine(type = ColType.FLOAT)
    private float weight;
    @Column
    @Comment("回收员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String recyclerId;
}
