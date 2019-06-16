<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('ROOKIE')">

	<form:form action="finder/rookie/clean.do">
		<acme:submit name="save" code="finder.cleanFilter" />

		<spring:url var="finderUrl" value="/finder/rookie/edit.do" />
		<a href="${finderUrl}">
			<button type="button">
				<spring:message code="finder.edit" />
			</button>
		</a>
	</form:form>

	</br>

	<display:table pagesize="5" name="positions" id="row"
		class="displaytag" requestURI="/finder/rookie/list.do">

		<display:column titleKey="position.ticker" >
			<jstl:out value="${row.ticker}" />
		</display:column>

		<display:column titleKey="position.title" >
			<jstl:out value="${row.title}" />
		</display:column>

		<display:column titleKey="position.description" >
			<jstl:out value="${row.description}" />
		</display:column>

		<display:column titleKey="position.deadline" >
			<jstl:out value="${row.deadline}" />
		</display:column>

		<display:column titleKey="position.requiredProfile" >
			<jstl:out value="${row.requiredProfile}" />
		</display:column>

		<display:column titleKey="position.requiredSkills">
			<spring:url var="requiredSkills"
				value="/position/rookie/listSkills.do">
				<spring:param name="positionId" value="${row.id}" />
			</spring:url>
			<a href="${requiredSkills}"> <spring:message code="skills.show"
					var="show" /> <jstl:out
					value="${show} (${row.requiredSkills.size()})" />
			</a>
		</display:column>

		<display:column titleKey="position.requiredTecnologies">
			<spring:url var="requiredTecnologies"
				value="/position/rookie/listTechnologies.do">
				<spring:param name="positionId" value="${row.id}" />
			</spring:url>
			<a href="${requiredTecnologies}"> <spring:message
					code="technologies.show" var="show" /> <jstl:out
					value="${show} (${row.requiredTecnologies.size()})" />
			</a>
		</display:column>

		<display:column titleKey="position.audits">

			<spring:url var="auditsUrl"
				value="/position/rookie/audit/list.do?positionId={positionId}">
				<spring:param name="positionId" value="${row.id}" />
				<spring:param name="assignable" value="${true}" />
			</spring:url>

			<a href="${auditsUrl}"> <spring:message var="viewAudits"
					code="position.viewAudits" /> <jstl:out value="${viewAudits}" />
			</a>

		</display:column>

		<display:column titleKey="position.offeredSalary" >
				<jstl:out value="${row.offeredSalary}" />
			</display:column>

		<display:column titleKey="position.sponsorship">
			<jstl:if test="${randomSpo.get(row.id).id>0}">
				<a href="${randomSpo.get(row.id).targetUrl}"><img
					src="${randomSpo.get(row.id).banner}"
					style="width: auto; height: 50px;"
					alt="<spring:message code='position.sponsorship'/>" /></a>
			</jstl:if>
		</display:column>

	</display:table>

</security:authorize>
