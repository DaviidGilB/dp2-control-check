<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

	<spring:message code="curriculum.title" var="title"/>
	<h3><jstl:out value="${title}: ${curriculum.title}"/></h3>
	
	<spring:message code="personalData"	var="perData"/>
	<h4><jstl:out value="${perData}"/></h4>
	
	<display:table name="personalData" id="row">
			
		<display:column titleKey="personalData.fullName" > 
			<jstl:out value="${row.fullName}" />
		</display:column>
		<display:column titleKey="personalData.statement" > 
			<jstl:out value="${row.statement}" />
		</display:column>
		<display:column titleKey="personalData.phoneNumber" > 
			<jstl:out value="${row.phoneNumber}" />
		</display:column>
		<display:column titleKey="personalData.gitHubProfile" > 
			<jstl:out value="${row.gitHubProfile}" />
		</display:column>
		<display:column titleKey="personalData.linkedinProfile" > 
			<jstl:out value="${row.linkedinProfile}" />
		</display:column>
	
	</display:table>
	
	<spring:message code="positionData"	var="posData"/>
	<h4><jstl:out value="${posData}"/></h4>
	
	<display:table name="positionData" id="row" pagesize="5" class="displaytag" 
					requestURI="${requestURI}">
			
		<display:column titleKey="positionData.title" > 
			<jstl:out value="${row.title}" />
		</display:column>
		<display:column titleKey="positionData.description" > 
			<jstl:out value="${row.description}" />
		</display:column>
		<display:column titleKey="positionData.startDate" > 
			<jstl:out value="${row.startDate}" />
		</display:column>
		<display:column titleKey="positionData.endDate" >
			<jstl:out value="${row.endDate}" />
		</display:column>
	
	</display:table>

	
	<spring:message code="educationData" var="eduData"/>
	<h4><jstl:out value="${eduData}"/></h4>
	
	<display:table name="educationData" id="row" pagesize="5" class="displaytag" 
					requestURI="${requestURI}">
			
		<display:column titleKey="educationData.degree" > 
			<jstl:out value="${row.degree}" />
		</display:column>
		<display:column titleKey="educationData.institution" > 
			<jstl:out value="${row.institution}" />
		</display:column>
		<display:column titleKey="educationData.mark" > 
			<jstl:out value="${row.mark}" />
		</display:column>
		<display:column titleKey="educationData.startDate" > 
			<jstl:out value="${row.startDate}" />
		</display:column>
		<display:column titleKey="educationData.endDate" >
			<jstl:out value="${row.endDate}" />
		</display:column>
	
	</display:table>
	
	<spring:message code="miscellaneousData" var="misData"/>
	<h4><jstl:out value="${misData}"/></h4>
	
	<display:table name="miscellaneousData" id="row" pagesize="5" class="displaytag" 
					requestURI="${requestURI}">
			
		<display:column titleKey="miscellaneousData.freeText" >
			<jstl:out value="${row.freeText}" />
		</display:column>
		
		<display:column titleKey="miscellaneousData.attachment" > 
			<jstl:out value="${row.attachments}" />
		</display:column>
		
	</display:table>
	
	<br>
	<br>
	
	<jstl:if test="${!assignable}">
  	<a href="anonymous/position/list.do"><spring:message code="position.backToPublicData" /></a>
  	
  	</jstl:if>
  	
  	  <security:authorize access="hasAnyRole('AUDITOR')">
    <jstl:if test="${assignable}">
  <a href="position/auditor/listAssignablePositions.do"><spring:message code="position.backToAssignablePositions" /></a>
  </jstl:if> 
  
  </security:authorize>
