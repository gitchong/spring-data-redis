package com.bocom.service;

import com.bocom.domain.SysUserInfo;

public interface UserService {

    SysUserInfo getUser(final String sessionId);

    void saveUser(final SysUserInfo user, final String sessionId);

    SysUserInfo getUserByLogin(SysUserInfo sysUserInfo);
}
