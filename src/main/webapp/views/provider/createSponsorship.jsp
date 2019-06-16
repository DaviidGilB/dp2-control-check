<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<security:authorize access="hasRole('PROVIDER')">

<form:form action="sponsorship/provider/save.do" modelAttribute="formObject" >

<fieldset>
  <legend> <spring:message code="sponsorship.data" /> </legend>
  	<form:input path="id" hidden="true"/>
  	
  	<br />

	<acme:input code="sponsorship.banner" path="banner"/>	
	<br />
	
	<acme:input code="sponsorship.targetURL" path="targetURL"/>	
	<br />
	
	<acme:selectNotPred code="sponsorship.position.title" path="position"
					items="${positions}" itemLabel="title" />
	
</fieldset>

<fieldset>
  <legend> <spring:message code="creditCard.data" /> </legend>
  
  	<br />
  
	<acme:input code="creditCard.holderName" path="holderName"/>	
	<br />
	
	<acme:selectString code="creditCard.brandName" path="brandName" items="${cardType}" itemsName="${cardType}"/>	
	<br />
	
	<acme:input code="creditCard.number" path="number"/>	
	<br />
	
	<acme:input code="creditCard.expirationMonth" path="expirationMonth"/>	
	<br />
	
	<acme:input code="creditCard.expirationYear" path="expirationYear"/>	
	<br />
	
	<acme:input code="creditCard.cvvCode" path="cvvCode"/>	
	<br />
	
</fieldset>

	<br />
	
	<jstl:if test="${formObject.id==0}">
		<acme:submit code="sponsorship.createButton" name="save" />
	</jstl:if>
	<jstl:if test="${formObject.id>0}">
		<acme:submit code="sponsorship.update" name="save" />
	</jstl:if>
	<acme:cancel url="/sponsorship/provider/list.do" code="sponsorship.cancel" /> 
	
</form:form> 

</security:authorize>