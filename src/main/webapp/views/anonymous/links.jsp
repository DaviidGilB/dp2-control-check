<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

	
	<display:table pagesize="5" name="links" id="row" class="displaytag" 
					requestURI="anonymous/item/listLinks.do">
		
		<display:column titleKey="item.links">
			<jstl:out value="${row}"/>
		</display:column> 
	
	</display:table>

	<jstl:choose>
	
		<jstl:when test="${param.back=='itemList'}">
		
		<spring:url var="back" value="anonymous/item/list.do">
			<spring:param name="providerId" value="${param.providerId}"/>
		</spring:url>
			
		<acme:cancel url="${back}" code="provider.backToTheItemList" />
		</jstl:when>
		
		<jstl:when test="${param.back=='providerProfile'}">
		<spring:url var="back" value="anonymous/provider/listOne.do">
			<spring:param name="providerId" value="${param.providerId}"/>
		</spring:url>
		
		<acme:cancel url="${back}" code="provider.backToTheProviderProfile" />
		</jstl:when>
	
	
	</jstl:choose>
 

