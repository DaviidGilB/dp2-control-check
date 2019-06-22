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

	<display:table pagesize="5" name="finalAudits" id="row" requestURI="${requestURI}" >

		<display:column titleKey="audit.momentCreation" >
			<jstl:out value="${row.momentCreation}" />
		</display:column>
	
		<display:column titleKey="audit.freeText" >
			<jstl:out value="${row.freeText}" />
		</display:column>	
	
		<display:column titleKey="audit.score" >
			<jstl:out value="${row.score}" />
		</display:column>
	
		<display:column titleKey="audit.auditor">
			<jstl:out value="${row.auditor.userAccount.username}" />
		</display:column>
		
		<!-- CONTROL_CHECK -->
		
		<display:column titleKey="audit.controlEntity">
			<spring:message code="audit.list.controlEntity" var="listControlEntity"/>
			
			<jstl:choose>
				<jstl:when test="${assignable}">
					<jstl:if test="${!row.isDraftMode && row.hasAnyFinalControlEntity()}">
						<spring:url value="/controlEntity/anonymous/list.do" var="urlControlEntity">
							<spring:param name="auditId" value="${row.id}"/>
						</spring:url>
						<a href="${urlControlEntity}"><jstl:out value="${listControlEntity}"/></a>
					</jstl:if>
				</jstl:when>
			
		    	<jstl:otherwise>
		    		<security:authorize access="hasAnyRole('AUDITOR')">
		    			<jstl:if test="${!row.isDraftMode && row.hasAnyFinalControlEntity()}">
			  				<spring:url value="/controlEntity/auditor/list.do" var="urlControlEntity">
								<spring:param name="auditId" value="${row.id}"/>
							</spring:url>
							<a href="${urlControlEntity}"><jstl:out value="${listControlEntity}"/></a>
						</jstl:if>
		  			</security:authorize>
		  			<security:authorize access="hasAnyRole('ROOKIE')">
		  				<jstl:if test="${!row.isDraftMode && row.hasAnyFinalControlEntity()}">
			  				<spring:url value="/controlEntity/rookie/list.do" var="urlControlEntity">
								<spring:param name="auditId" value="${row.id}"/>
							</spring:url>
							<a href="${urlControlEntity}"><jstl:out value="${listControlEntity}"/></a>
						</jstl:if>
		  			</security:authorize>
		  			<security:authorize access="hasAnyRole('COMPANY')">
		  				<jstl:if test="${!row.isDraftMode}">
			  				<spring:url value="/controlEntity/company/list.do" var="urlControlEntity">
								<spring:param name="auditId" value="${row.id}"/>
							</spring:url>
							<a href="${urlControlEntity}"><jstl:out value="${listControlEntity}"/></a>
						</jstl:if>
		  			</security:authorize>
		  		</jstl:otherwise>
		  	</jstl:choose>
			
		</display:column>
	</display:table>
	
	<jstl:choose>
		<jstl:when test="${assignable}">
			<a href="anonymous/position/list.do"><spring:message code="position.backToPublicData" /></a>
		</jstl:when>
		
    	<jstl:otherwise>
    		<security:authorize access="hasAnyRole('AUDITOR')">
  				<a href="position/auditor/listAssignablePositions.do"><spring:message code="position.backToPositions" /></a>
  			</security:authorize>
  			<security:authorize access="hasAnyRole('ROOKIE')">
  				<a href="finder/rookie/list.do"><spring:message code="position.backToPositions" /></a>
  			</security:authorize>
  			<security:authorize access="hasAnyRole('COMPANY')">
  				<a href="position/company/list.do"><spring:message code="position.backToPositions" /></a>
  			</security:authorize>
  		</jstl:otherwise>
	</jstl:choose>



	
