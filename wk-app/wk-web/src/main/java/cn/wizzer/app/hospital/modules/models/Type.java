package cn.wizzer.app.hospital.modules.models;

import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("垃圾分类")
@Table("type")
public class Type implements Serializable {
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
    @Comment("是否是盐水瓶")
    @ColDefine(type = ColType.BOOLEAN)
    private String isBottle;
    public String getName() {
        return name;
    }
}
