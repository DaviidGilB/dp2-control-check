<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import="domain.Position" %>

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

<p><spring:message code="brotherhood.position.application.list" /></p>

	<display:table pagesize="5" name="allApplications" id="row" requestURI="${requestURI}" >



	<display:column titleKey="application.creationMoment" >
		<jstl:out value="${row.creationMoment}" />
	</display:column>
	
	<display:column titleKey="application.link" >	
		<jstl:out value="${row.link}" />
	</display:column>
	
	<display:column titleKey="application.explication" >
		<jstl:out value="${row.explication}" />
	</display:column>
	
	<display:column titleKey="application.submitMoment" >
		<jstl:out value="${row.submitMoment}" />
	</display:column>
	
	<display:column titleKey="application.status" >
		<jstl:out value="${row.status}" />
	</display:column>
	
	<display:column titleKey="application.problem"> 
		<jstl:out value="${row.problem.title}"/>
	</display:column>	
	
	<display:column titleKey="application.curriculum">
		<spring:message var="seeCurriculum" code="application.viewCurriculum" />
						
			<spring:url var="curriculumUrl" value="/anonymous/curriculum/list.do?applicationId={applicationId}">
            	<spring:param name="applicationId" value="${row.id}"/>
            	<spring:param name="assignable" value="${assignable}" />
        	</spring:url>
        	
        	<a href="${curriculumUrl}">
              <jstl:out value="${seeCurriculum}" />  
        	</a>
   	
	</display:column>
	
	<display:column titleKey="application.rookie">
		<jstl:out value="${row.rookie.userAccount.username}" />
	</display:column>
	
	
	
	<security:authorize access="hasRole('COMPANY')">
	
	
	<display:column>
	
		<jstl:if test="${row.status == 'SUBMITTED' && sameActorLogged}">
			<a href="position/company/application/accept.do?applicationId=${row.id}" onclick="return confirm('<spring:message code="company.confirmChangeStatus" />')">
				<spring:message code="application.accept" />
			</a>
			<br />
			<a href="position/company/application/reject.do?applicationId=${row.id}"  onclick="return confirm('<spring:message code="company.confirmChangeStatus" />')">
				<spring:message code="application.reject" />
			</a>
		</jstl:if>
	
	</display:column>
	
	</security:authorize>
	
	<!-- CONTROL_CHECK -->
	<display:column titleKey="audit.controlEntity">
		<jstl:if test="${(row.status == 'ACCEPTED' || row.status == 'REJECTED') && row.hasAnyFinalControlEntity()}">
			<spring:message code="audit.list.controlEntity" var="listControlEntity"/>
			<spring:url value="/controlEntity/anonymous/list.do" var="urlControlEntity">
				<spring:param name="applicationId" value="${row.id}"/>
			</spring:url>
			<a href="${urlControlEntity}"><jstl:out value="${listControlEntity}"/></a>
		</jstl:if>
	</display:column>
	
	</display:table>
	
	
<br />

<security:authorize access="hasRole('COMPANY')">
<jstl:if test="${sameActorLogged}">
<a href="position/company/list.do"><spring:message code="position.back" /></a>
</jstl:if>
</security:authorize>

<br />

	<jstl:if test="${!assignable}">
		<a href="anonymous/position/list.do"><spring:message code="position.backToPublicData" /></a>
	</jstl:if> 
  <security:authorize access="hasAnyRole('AUDITOR')">
    <jstl:if test="${assignable}">
  <a href="position/auditor/listAssignablePositions.do"><spring:message code="position.backToAssignablePositions" /></a>
  </jstl:if> 
  
  </security:authorize>



	
