<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('AUDITOR')">

	<form:form modelAttribute="audit"
		action="audit/auditor/edit.do">
		<!--Hidden Attributes -->
		<form:hidden path="id" />
		<form:hidden path="version" />
		<form:hidden path="position"/>



		<acme:textarea code="audit.freeText" path="freeText" />
		<acme:boolean code="position.isDraftMode" trueCode="position.true" falseCode="position.false" path="isDraftMode"/>	
		<br />
		<acme:selectNumber max="10" min="0" code="audit.score" path="score"/>
		
		<jstl:choose>
			<jstl:when test="${audit.id == 0}">
				<acme:submit name="save" code="rookie.save" />
			</jstl:when>
			<jstl:otherwise>
				<acme:submit name="save" code="rookie.edit" />
				<acme:submit name="delete" code="rookie.delete" />
			</jstl:otherwise>
		</jstl:choose>


	</form:form>
	
	<jstl:choose>
		<jstl:when test="${audit.id == 0}">
			<acme:cancel url="/position/auditor/listAssignablePositions.do" code="rookie.cancel" />
		</jstl:when>
		<jstl:otherwise>
			<acme:cancel url="/audit/auditor/list.do" code="rookie.cancel" />
		</jstl:otherwise>
	</jstl:choose>
	
	

</security:authorize>