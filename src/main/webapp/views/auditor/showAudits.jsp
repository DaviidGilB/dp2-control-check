<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	String scheme = request.getScheme();             
	String serverName = request.getServerName(); 
	int serverPort = request.getServerPort();    
	String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
	String prmstr = (String) request.getAttribute("javax.servlet.forward.query_string");
	String url = scheme + "://" +serverName + ":" + serverPort + uri;
	if(prmstr != null) {
		url = url + "?" + prmstr;
	}
	request.getSession().setAttribute("listBackUrl", url);
%>

<%@ page import="domain.Position" %>

<p><spring:message code="position.audits.list" /></p>

	<display:table pagesize="5" name="audits" id="row" requestURI="${requestURI}" >
	
	<display:column titleKey="position.title" >
		<jstl:out value="${row.position.title}"></jstl:out>
	</display:column>

	<display:column titleKey="audit.momentCreation" >
		<jstl:out value="${row.momentCreation}" />
	</display:column>
	
	<display:column titleKey="audit.freeText" >	
		<jstl:out value="${row.freeText}" />
	</display:column>
	
	<display:column titleKey="audit.score" >
		<jstl:out value="${row.score}" />
	</display:column>
	
	<display:column titleKey="position.status">
        <jstl:choose>
        	<jstl:when test="${row.isDraftMode}">
        		<spring:message code="position.draft" />
        	</jstl:when>
        	
        	<jstl:otherwise>
        		<spring:message code="position.final" />
        	</jstl:otherwise>
        	
        </jstl:choose>
    </display:column>
    
    <!-- CONTROL_CHECK -->
    
    <display:column titleKey="audit.reckon">
    	<jstl:if test="${!row.isDraftMode && row.hasAnyFinalReckon()}">
	    	<spring:message code="audit.list.reckon" var="listReckon"/>
	    	<spring:url value="/reckon/auditor/list.do" var="urlReckon">
				<spring:param name="auditId" value="${row.id}"/>
			</spring:url>
			<a href="${urlReckon}"><jstl:out value="${listReckon}"/></a>
		</jstl:if>
    </display:column>
    
    <display:column>
		<jstl:if test="${row.isDraftMode}">
			<a href="audit/auditor/edit.do?auditId=${row.id}">
				<spring:message code="position.edit" />
			</a>
		</jstl:if>

	</display:column>
	
	</display:table>
<br />



	
