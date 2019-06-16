<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>


<display:table pagesize="5" name="items" id="row" class="displaytag" 
					requestURI="anonymous/item/list.do">
			
		<display:column titleKey="item.name" sortable="true">
			<jstl:out value="${row.name}" />
		</display:column>
		
		<display:column titleKey="item.description" sortable="true">
			<jstl:out value="${row.description}" />
		</display:column>
		
		<display:column titleKey="item.links">
			<spring:url var="links" value="/anonymous/item/listLinks.do">
				<spring:param name="itemId" value="${row.id}"/>
				<spring:param name="back" value="itemList"/>
				<spring:param name="providerId" value="${param.providerId}"/>
			</spring:url>
			<a href="${links}">
				<spring:message code="item.links.show" var ="show" />
				<jstl:out value="${show} (${row.links.size()})"/>
			</a>
		</display:column>
		
		<display:column titleKey="item.pictures">
			<spring:url var="pictures" value="/anonymous/item/listPictures.do">
				<spring:param name="itemId" value="${row.id}"/>
				<spring:param name="back" value="itemList"/>
				<spring:param name="providerId" value="${param.providerId}"/>
			</spring:url>
			<a href="${pictures}">
				<spring:message code="item.pictures.show" var ="show" />
				<jstl:out value="${show} (${row.pictures.size()})"/>
			</a>
		</display:column>
		
		
		<display:column titleKey="item.provider">
			<spring:url var="provider" value="/anonymous/provider/listOne.do">
				<spring:param name="providerId" value="${providersByItem.get(row).id}"/>
			</spring:url>
			<a href="${provider}">
				<spring:message code="provider.viewProviderProfile" var ="show" />
				<jstl:out value="${show}"/>
			</a>
		</display:column>
	
	
		
	</display:table>
	
	<jstl:if test="${publicData}">
  	<a href="anonymous/provider/list.do"><spring:message code="position.backToPublicData" /></a>
 	</jstl:if>