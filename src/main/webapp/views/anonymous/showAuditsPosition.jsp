<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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



	
