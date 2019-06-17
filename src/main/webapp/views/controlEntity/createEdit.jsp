<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('AUDITOR')">

	<form:form modelAttribute="controlEntity" action="controlEntity/company/save.do">
		<!--Hidden Attributes -->
		<form:hidden path="id" />
		<form:hidden path="version" />
		<form:hidden path="ticker" />
		<form:hidden path="publicationMoment" />
		
		<jstl:if test="${controlEntity.id == 0}">
			<input type="hidden" name="auditId" value="${auditId}">
		</jstl:if>

		<acme:textarea code="controlEntity.body" path="body" />
		<acme:input code="controlEntity.picture" path="picture" />
		<acme:boolean code="controlEntity.isDraftMode" trueCode="controlEntity.isDraftMode.true" falseCode="controlEntity.isDraftMode.false" path="isDraftMode"/>	
		
		<br/><br/>
		
		<jstl:choose>
			<jstl:when test="${controlEntity.id == 0}">
				<acme:submit name="save" code="controlEntity.save" />
			</jstl:when>
			<jstl:otherwise>
				<acme:submit name="save" code="controlEntity.edit" />
				
				<spring:message code="controlEntity.delete" var="deleteText"/>
				<input type="submit" name="delete" value="${deleteText}" onclick="return confirm('<spring:message code="controlEntity.delete.confirmation" />')"/>
			</jstl:otherwise>
		</jstl:choose>
		
		<acme:cancel url="/controlEntity/company/list.do" code="controlEntity.cancel" />

	</form:form>
	
</security:authorize>