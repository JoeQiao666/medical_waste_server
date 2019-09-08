package cn.wizzer.app.hospital.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("科室")
@Table("department")
public class Department extends BaseModel implements Serializable {
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
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
