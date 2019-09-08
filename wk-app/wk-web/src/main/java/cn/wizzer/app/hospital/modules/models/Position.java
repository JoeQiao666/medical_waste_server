package cn.wizzer.app.hospital.modules.models;

import org.nutz.dao.entity.annotation.*;

@Comment("岗位")
@Table("sys_position")
public class Position {
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
    @Comment("角色ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String roleId;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getRoleId() {
        return roleId;
    }
}
