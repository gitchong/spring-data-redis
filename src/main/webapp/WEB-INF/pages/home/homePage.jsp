<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.bocom.domain.SysUserInfo" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>spring-data-redis</title>
    <link rel="SHORTCUT ICON" href="${pageContext.request.contextPath}/media/image/ico.ico">
    <link rel="BOOKMARK" href="${pageContext.request.contextPath}/media/image/ico.ico">
    <style>
        body{
            margin: 0;
            padding: 0;
            background-color: #EAEAEA;
        }
        .center-in-center{
            position: absolute;
            top: 30%;
            left: 45%;
        }
    </style>

</head>
<body>
<div class="center-in-center">
    <c:choose>
        <c:when test='${sysUser.userName != null}'>
            <h1>欢迎 ： ${sysUser.userName}   </h1>
        </c:when>
        <c:otherwise>
            <h1>欢迎 ： 陌生人 </h1>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
