package com.bocom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bocom.dao.SysUserDao;
import com.bocom.domain.SysUserInfo;
import com.bocom.service.UserService;
import com.bocom.utils.SerializeUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    protected RedisTemplate<Serializable, Serializable> redisTemplate;

    @Resource
    private SysUserDao sysUserDao;

    @Override
    public void saveUser(final SysUserInfo user, final String sessionId) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(redisTemplate.getStringSerializer().serialize(sessionId),
                        redisTemplate.getStringSerializer().serialize(JSONObject.toJSONString(user)));
                //设置失效时间  半小时
                connection.expire(SerializeUtil.serialize(sessionId), Long.valueOf("3600"));
                return null;
            }
        });
    }

    @Override
    public SysUserInfo getUser(final String sessionId) {
        return redisTemplate.execute(new RedisCallback<SysUserInfo>() {
            @Override
            public SysUserInfo doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = redisTemplate.getStringSerializer().serialize(sessionId);
                if (connection.exists(key)) {
                    byte[] value = connection.get(key);
                    ObjectMapper objectMapper = new ObjectMapper();
                    SysUserInfo userInfo = objectMapper.convertValue(
                            JSONObject.parse(redisTemplate.getStringSerializer().deserialize(value)),
                            SysUserInfo.class);
                    return userInfo;
                }
                return null;
            }
        });
    }

    @Override
    public SysUserInfo getUserByLogin(SysUserInfo sysUserInfo) {

        return sysUserDao.getUserByLogin(sysUserInfo);
    }

}
