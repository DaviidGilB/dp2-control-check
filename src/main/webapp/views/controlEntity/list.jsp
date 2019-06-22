<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import="java.util.Date" %>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.util.Calendar" %>

<%
	Calendar c = Calendar.getInstance();
	
	Date now = new Date();
	
	Date ref1 = new Date();
	c.setTime(ref1);
	c.add(Calendar.MONTH, -1);
	Date oneMonth = c.getTime();
	
	Date ref2 = new Date();
	c.setTime(ref2);
	c.add(Calendar.MONTH, -2);
	Date twoMonth = c.getTime();
%>
<jstl:set var="now" value="<%=now%>"/>
<jstl:set var="oneMonth" value="<%=oneMonth%>"/>
<jstl:set var="twoMonth" value="<%=twoMonth%>"/>

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
	
	String listBackUrl = "";
	if(request.getSession().getAttribute("listBackUrl") != null) {
		listBackUrl = request.getSession().getAttribute("listBackUrl").toString();
	}
	
	request.getSession().setAttribute("backUrl", url);
%>
<jstl:set var="listBackUrl" value="<%=listBackUrl%>"/>

	<display:table pagesize="5" name="controlEntity" id="row" requestURI="${requestURI}" >
	
		<jstl:choose>
			<jstl:when test="${row.publicationMoment < now && row.publicationMoment >= oneMonth && !row.isDraftMode}">
				<jstl:set var="color" value="Indigo" />
			</jstl:when>
			
			<jstl:when test="${row.publicationMoment < oneMonth && row.publicationMoment >= twoMonth && !row.isDraftMode}">
				<jstl:set var="color" value="DarkSlateGrey" />
			</jstl:when>
			
			<jstl:when test="${row.publicationMoment < twoMonth && !row.isDraftMode}">
				<jstl:set var="color" value="PapayaWhip" />
			</jstl:when>
			
			<jstl:otherwise>
				<jstl:set var="color" value="black" />
			</jstl:otherwise>
		</jstl:choose>

		<display:column titleKey="controlEntity.ticker" style="color:${color}">
			<b><jstl:out value="${row.ticker}" /></b>
		</display:column>
		
		<jstl:if test="${locale=='EN'}">
			<jstl:set var="format" value="{0,date,yy/MM/dd HH:mm}"/>
		</jstl:if>
		<jstl:if test="${locale=='ES'}">
			<jstl:set var="format" value="{0,date,dd-MM-yy HH:mm}"/>
		</jstl:if>
	
		<display:column titleKey="controlEntity.publicationMoment" property="publicationMoment" format="${format}" style="color:${color}"/>
	
		<display:column titleKey="controlEntity.body" style="color:${color}">
			<jstl:out value="${row.body}" />
		</display:column>
	
		<display:column titleKey="controlEntity.picture" style="color:${color}">
			<jstl:out value="${row.picture}" />
		</display:column>
		
		<jstl:if test="${editOption || createOption}">
			<security:authorize access="hasRole('COMPANY')">
				<display:column>
					<jstl:if test="${row.isDraftMode}">
						<a href="controlEntity/company/edit.do?controlEntityId=${row.id}"><spring:message code="controlEntity.edit"/></a>
					</jstl:if>
				</display:column>
			</security:authorize>
		</jstl:if>

	</display:table>
	
	<security:authorize access="hasRole('COMPANY')">
		<jstl:if test="${createOption}">
			<a href="controlEntity/company/create.do?auditId=${auditId}"><button type="button"><spring:message code="controlEntity.create"/></button></a>
		</jstl:if>
	</security:authorize>
	
	<spring:message code="controlEntity.back" var="messageBack"/>
	<p><a href="${listBackUrl}"><jstl:out value="${messageBack}"/></a></p>
