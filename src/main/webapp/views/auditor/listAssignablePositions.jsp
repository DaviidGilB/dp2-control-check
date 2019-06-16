<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>



<display:table name="positions" id="row" pagesize="5" class="displaytag" requestURI="/position/auditor/listAssignablePositions.do">

	<display:column titleKey="possition.title" >
			<jstl:out value="${row.title}" />
	</display:column>
	<display:column titleKey="possition.description" >
			<jstl:out value="${row.description}" />
	</display:column>
	<display:column titleKey="possition.deadline" >
			<jstl:out value="${row.deadline}" />
	</display:column>
	<display:column titleKey="possition.requiredProfile" >
			<jstl:out value="${row.requiredProfile}" />
	</display:column>
	<display:column titleKey="possition.requiredSkills" >
			<jstl:out value="${row.requiredSkills}" />
	</display:column>
	<display:column titleKey="possition.requiredTecnologies" >
			<jstl:out value="${row.requiredTecnologies}" />
	</display:column>
	<display:column titleKey="possition.offeredSalary" >
			<jstl:out value="${row.offeredSalary}" />
	</display:column>
	<display:column titleKey="possition.ticker" >
			<jstl:out value="${row.ticker}" />
	</display:column>

	<display:column titleKey="position.sponsorship">
		<jstl:if test="${randomSpo.get(row.id).id>0}">
			<a href="${randomSpo.get(row.id).targetUrl}"><img
				src="${randomSpo.get(row.id).banner}"
				style="width: auto; height: 50px;"
				alt="<spring:message code='position.sponsorship'/>" /></a>
		</jstl:if>
	</display:column>

	<display:column titleKey="possition.problems">
		<spring:url var="createUrl0"
			value="/position/auditor/problem/list.do?positionId={positionId}">
			<spring:param name="positionId" value="${row.id}" />
			<spring:param name="assignable" value="${true}" />
		</spring:url>
		<a href="${createUrl0}"><spring:message code="annonymous.problems" /></a>
	</display:column>

	<display:column titleKey="position.audits">

		<spring:url var="auditsUrl"
			value="/position/auditor/audit/list.do?positionId={positionId}">
			<spring:param name="positionId" value="${row.id}" />
			<spring:param name="assignable" value="${true}" />
		</spring:url>

		<a href="${auditsUrl}"> <spring:message var="viewAudits"
				code="position.viewAudits" /> <jstl:out value="${viewAudits}" />
		</a>

	</display:column>


	<display:column titleKey="annonymous.companies">
		<spring:url var="createUrl1"
			value="/position/auditor/company/listOne.do?positionId={positionId}">
			<spring:param name="positionId" value="${row.id}" />
			<spring:param name="assignable" value="${true}" />
		</spring:url>
		<a href="${createUrl1}"><spring:message code="annonymous.company" /></a>
	</display:column>

	<security:authorize access="hasRole('AUDITOR')">
		<display:column titleKey="position.assign">

			<button type="button"
				onclick="javascript: relativeRedir('audit/auditor/create.do?positionId='+${row.id})">
				<spring:message code="position.assign" />
			</button>

		</display:column>
	</security:authorize>




</display:table>
