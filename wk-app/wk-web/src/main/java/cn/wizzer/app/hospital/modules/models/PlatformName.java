package cn.wizzer.app.hospital.modules.models;

import org.nutz.dao.entity.annotation.*;

@Comment("名称")
@Table("platform_name")
public class PlatformName {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("医院名称")
    @ColDefine(type = ColType.VARCHAR)
    private String hospital;
    @Column
    @Comment("医院名称")
    @ColDefine(type = ColType.VARCHAR)
    private String client;
    @Column
    @Comment("医院名称")
    @ColDefine(type = ColType.VARCHAR)
    private String web;
}
