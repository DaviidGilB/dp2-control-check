<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<p><spring:message code="mail.myMessages" /></p>

<security:authorize access="isAuthenticated()">


<display:table
	pagesize="4" name="messages" id="row"
	requestURI="message/actor/list.do" >
	
	<!-- Date -->
	<display:column	titleKey="mail.message.moment">
			<jstl:out value="${row.moment}" />
	</display:column>
					
	<display:column	titleKey="mail.message.sender">
			<jstl:out value="${row.sender}" />
	</display:column>

	<display:column	titleKey="mail.message.receiver">
			<jstl:out value="${row.receiver}" />
	</display:column>
	
	<display:column titleKey="mail.message.subject">
			<jstl:out value="${row.subject}" />
	</display:column>
	
	<display:column	sortable ="true" titleKey="mail.message.tags">
			<jstl:out value="${row.tags}" />
	</display:column>
					
	<display:column	titleKey="mail.message.body">
			<jstl:out value="${row.body}" />
	</display:column>
				
	<display:column>
		
			<spring:url var="deleteMessage" value="/message/actor/delete.do?rowId={rowId}">
				<spring:param name="rowId" value="${row.id}"/>
			</spring:url>
		
				<a href="${deleteMessage}" onclick="return confirm('<spring:message code="mail.delete" />')">
				 <spring:message code="message.delete"/>
				 </a>

		</display:column>	
	

															
</display:table>

<!-- Enlaces parte inferior -->
<spring:url var="newMessage" value="/message/actor/create.do"/>

<p><a href="${newMessage}"><spring:message code="mail.message.new" /></a></p>


</security:authorize>