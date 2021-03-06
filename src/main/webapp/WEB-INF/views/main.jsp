<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
  <head>
  
    <jsp:include page="/WEB-INF/views/metaAndIcon.jsp" />
  
    <title>RDCenter</title>
  
    <c:choose>
      <c:when test="${tema == null || tema eq '' || tema eq 'default' }">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.6/css/bootstrap-theme.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
      </c:when>
      <c:otherwise>
        <link href="${context}/css/bootstrap-themes/${tema}/bootstrap.min.css" rel="stylesheet">
      </c:otherwise>
    </c:choose>

    <jsp:include page="/WEB-INF/views/customStyles.jsp" />
    
    <jsp:include page="/WEB-INF/views/scripts.jsp" />
    
    <script type="text/javascript">
       var contextPath = "${pageContext.request.contextPath}";
    </script>
     
<!--     <script src='https://www.google.com/recaptcha/api.js'></script>     -->
  </head>

  <body role="document" class="${backgroundColor}">
     
     
