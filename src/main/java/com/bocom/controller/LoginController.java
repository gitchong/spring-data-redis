package com.bocom.controller;

import com.bocom.dao.SysUserDao;
import com.bocom.service.UserService;
import com.bocom.domain.SysUserInfo;
import com.bocom.utils.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class LoginController {

    // log
    private static Logger logger = LoggerFactory
            .getLogger(LoginController.class);

    @Autowired
    @Qualifier("redisTemplate")
    private StringRedisTemplate writeTemplate;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Resource
    private UserService userService;

    private final String redis_key = "training_redis_";

    @RequestMapping(method = RequestMethod.GET)
    public String homePage() {
        SysUserInfo userInfo = (SysUserInfo) request.getSession().getAttribute("sysUser");
        String sessionID;
        if (null == userInfo) {
            //根据session id 去redis 取值
            HttpSession httpSession = request.getSession();
            sessionID = httpSession.getId();
            System.out.println("sessionID  = " + sessionID);
            SysUserInfo sysUserInfo = userService.getUser(redis_key + sessionID);
            if (null == sysUserInfo) {
                //根据cookie中的JSESSIONID 去取值
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("JSESSIONID".equals(cookie.getName())) {
                            sessionID = cookie.getValue();
                            System.out.println("cookie JSESSIONID  = " + sessionID);
                            break;
                        }
                    }
                    sysUserInfo = userService.getUser(redis_key + sessionID);
                    if (sysUserInfo != null) {
                        request.getSession().setAttribute("sysUser", sysUserInfo);
                        return "home/homePage";
                    }
                }
                //根据 url 中的JSESSIONID 去取值
                String requestURI = request.getRequestURI();
                String suffixURI = requestURI.substring(requestURI.lastIndexOf(";") + 1, requestURI.length());
                if (suffixURI.startsWith("jsessionid=")) {
                    String sessionId = suffixURI.split("jsessionid=")[1];
                    System.out.println("jsessionid  = " + sessionId);
                    sysUserInfo = userService.getUser(redis_key + sessionId);
                    if (null != sysUserInfo) {
                        request.getSession().setAttribute("sysUser", sysUserInfo);
                        return "home/homePage";
                    }
                }
                return "home/login";
            }
            request.getSession().setAttribute("sysUser", sysUserInfo);
            return "home/homePage";
        } else {
            return "home/homePage";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    public String login() {
        try {
            String userName = request.getParameter("userName");
            String pwd = request.getParameter("password");
            SysUserInfo sysUserInfo = new SysUserInfo();
            sysUserInfo.setUserName(userName);
            sysUserInfo.setPassword(pwd);
            SysUserInfo userInfo = userService.getUserByLogin(sysUserInfo);
            if (null == userInfo) {
                return "home/login";
            } else {
                request.getSession().setAttribute("sysUser", userInfo);
                userService.saveUser(userInfo, redis_key + request.getSession().getId());
                return "home/homePage";
            }
        } catch (Exception e) {
            logger.error("login error ", e);
            return "home/login";
        }
    }

    /**
     * 登出
     */
    @RequestMapping(value = "/loginOut", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo loginCasOut(HttpSession session) {
        session.invalidate();
        return new ResponseVo();
    }
}
