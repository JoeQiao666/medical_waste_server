package cn.wizzer.framework.base.model;

import java.io.Serializable;

@Comment("国家")
@Table("dic_country")
public class DicCountry implements Serializable {
    private static final long serialVersionUID = 1L;
    @Name
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("编码")
    @ColDefine(type = ColType.VARCHAR)
    private String code;
    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR)
    private String name;

}
