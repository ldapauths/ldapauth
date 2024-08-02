package com.ldapauth.enums;


public enum OrgsBusinessExceptionEnum {
    SUB_USERS_EXISTS(500001, "请先移除/移动当前组织机构下的用户"),
    SYNC_USERS_EXISTS(500001,"请先移除/移动当前组织机构下的同步用户" ),
    SUB_ORGS_EXISTS(500002,"请先移除/移动当前组织机构下的部门" ),
    ILLEGAL_MOVE_ORG(500003,"非法的移动操作"),
    SUB_USERS_ACTIVE(500004, "请先禁用当前组织机构下的活跃用户"),
    SYNC_USERS_ACTIVE(500004, "请先禁用当前组织机构下的同步用户"),
    SUB_ORGS_ACTIVE(500005, "请先禁用当前组织机构下的活跃部门"),
    CURRENT_ORGS_ACTIVE(500006, "请先禁用活跃组织后再进行删除"),
    CURRENT_USERS_ACTIVE(500006, "请先禁用活跃用户后再进行删除"),
    PARENT_ORGS_FORBIDDEN(500007, "请先启用当前组织机构的父级组织机构"),

    DUPLICATE_ORGS_EXIST(500008, "当前企业层级已存在相同机构名称，请重新输入"),
    DUPLICATE_ORGSCODE_EXIST(500009, "当前企业已存在相同的编码，请重新输入"),
    GROUP_ALREADY_USED(500010, "当前分组正在被使用，无法禁用"),
    GROUPS_ACTIVE(500011, "请先禁用当前分组后再进行删除");


    String msg;
    Integer code;

    OrgsBusinessExceptionEnum(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

}
