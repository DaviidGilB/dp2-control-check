<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

	
	<display:table name="providers" id="row" pagesize="5" class="displaytag" 
					requestURI="/anonymous/provider/list.do">
	
		<display:column titleKey="provider.make" sortable="true"> 
			<jstl:out value="${row.make}" />
		</display:column>
		<display:column titleKey="companies.name" sortable="true">
			<jstl:out value="${row.name}" /> 
		</display:column>
		<display:column titleKey="companies.VATNumber" sortable="true"> 
			<jstl:out value="${row.VATNumber}" />
		</display:column>
		<display:column titleKey="companies.photo" sortable="true"> 
			<jstl:out value="${row.photo}" />
		</display:column>
		<display:column titleKey="companies.email" sortable="true">
			<jstl:out value="${row.email}" /> 
		</display:column>
		<display:column titleKey="companies.phone" sortable="true"> 
			<jstl:out value="${row.phone}" />
		</display:column>
		<display:column titleKey="companies.address" sortable="true"> 
			<jstl:out value="${row.address}" />
		</display:column>
		
		
		<display:column titleKey="provider.items">
    		
       		<spring:url var="itemsUrl" value="/anonymous/item/list.do?providerId={providerId}">
            	<spring:param name="providerId" value="${row.id}"/>
        	</spring:url>
        	
        	<a href="${itemsUrl}">
              <spring:message var ="viewItems" code="provider.viewItems" />
             <jstl:out value="${viewItems}" />   
        	</a>
        	
        </display:column>
	
	</display:table>
	

