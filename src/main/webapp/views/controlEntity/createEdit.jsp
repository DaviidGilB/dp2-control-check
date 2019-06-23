<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<%
	String backUrl = "";
	if(request.getSession().getAttribute("backUrl") != null) {
		backUrl = request.getSession().getAttribute("backUrl").toString();
	}
%>
<jstl:set var="backUrl" value="<%=backUrl%>"/>

<security:authorize access="hasRole('ROOKIE')">

	<form:form modelAttribute="controlEntity" action="controlEntity/rookie/save.do">
		<!--Hidden Attributes -->
		<form:hidden path="id" />
		
		<jstl:if test="${controlEntity.id > 0}">
			<form:hidden path="ticker" />
		</jstl:if>
		
		<jstl:if test="${controlEntity.id == 0}">
			<input type="hidden" name="applicationId" value="${applicationId}">
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
		
		<a href="${backUrl}"><input type="button" value="<spring:message code='controlEntity.cancel'/>"/></a>

	</form:form>
	
</security:authorize>