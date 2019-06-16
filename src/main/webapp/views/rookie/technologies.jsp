<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('ROOKIE')">
	
	<display:table pagesize="5" name="technologies" id="row" class="displaytag" 
					requestURI="/position/rookie/listTechnologies.do">
		
		<display:column titleKey="technologies">
			<jstl:out value="${row}"/>
		</display:column> 
	
	</display:table>
	
	<acme:cancel url="/finder/rookie/list.do" code="rookie.back" /> 

</security:authorize>