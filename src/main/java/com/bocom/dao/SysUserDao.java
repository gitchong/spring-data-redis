package com.bocom.dao;

import com.bocom.domain.SysUserInfo;

public interface SysUserDao {

    SysUserInfo getUserByLogin(SysUserInfo sysUserInfo);
}
