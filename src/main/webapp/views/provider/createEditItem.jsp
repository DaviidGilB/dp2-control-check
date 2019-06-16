<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('PROVIDER')">

	<form:form action="item/provider/save.do" modelAttribute="formObjectItem" >
	
		<jstl:if test = "${formObjectItem.id>0}">
			<form:input path ="id" hidden="true"/>
		</jstl:if>
		
		<acme:input code="item.name" path="name"/>
		<acme:input code="item.description" path="description"/>
		<acme:input code="item.links" path="links"/>
		<acme:input code="item.pictures" path="pictures"/>
		
		<br/>
		
		<jstl:if test="${formObjectItem.id>0}">
			<input type="submit" name="save" value="<spring:message code="item.updateButton" />"/> 
		</jstl:if>
		<jstl:if test="${formObjectItem.id==0}">
			<input type="submit" name="save" value="<spring:message code="item.createButton" />"/> 
		</jstl:if>
		<acme:cancel url="/item/provider/list.do" code="item.cancelButton" /> 
		
	</form:form> 

</security:authorize>
