<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

	
	<display:table pagesize="5"  name="companies" id="row" requestURI="${requestURI}" >
	
		<display:column titleKey="companies.name" >
			<jstl:out value="${row.name}" /> 
		</display:column>
		<display:column titleKey="companies.VATNumber" > 
			<jstl:out value="${row.VATNumber}" />
		</display:column>
		<display:column titleKey="companies.photo" > 
			<jstl:out value="${row.photo}" />
		</display:column>
		<display:column titleKey="companies.email" > 
			<jstl:out value="${row.email}" />
		</display:column>
		<display:column titleKey="companies.phone" > 
			<jstl:out value="${row.phone}" />
		</display:column>
		<display:column titleKey="companies.address" >
			<jstl:out value="${row.address}" />
		</display:column>
		
		<display:column  titleKey="actor.score" >
			<jstl:choose>
				<jstl:when test="${row.score == null}">
					<spring:message code="actor.nil" />
				</jstl:when><jstl:otherwise>
					<jstl:out value="${row.score}"></jstl:out>
				</jstl:otherwise>
			</jstl:choose>
		</display:column>  
		
		<display:column titleKey="companies.positions">
    

    	
    		
       		<spring:url var="positionsUrl" value="/anonymous/company/positions.do?idCompany={idCompany}">
            	<spring:param name="idCompany" value="${row.id}"/>
        	</spring:url>
        	
        	<a href="${positionsUrl}">
              <spring:message var ="viewPositions" code="position.viewPositions" />
             <jstl:out value="${viewPositions}" />   
        	</a>
        	
        </display:column>
	
	</display:table>
	

