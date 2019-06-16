<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('ROOKIE')">
	
	<display:table pagesize="5" name="attachments" id="row" class="displaytag" 
					requestURI="/miscellaneousData/rookie/listAttachments.do">
		
		<display:column titleKey="attachments">
			<jstl:out value="${row}"/>
		</display:column> 
		
		<display:column titleKey="miscellaneousData.action">
			<spring:url var="deleteAttachment" value="/miscellaneousData/rookie/deleteAttachment.do">
				<spring:param name="miscellaneousDataId" value="${miscellaneousDataId}"/>
				<spring:param name="attachmentIndex" value="${attachments.indexOf(row)}"/>
			</spring:url>
			<a href="${deleteAttachment}" onclick="return confirm('<spring:message code="miscellaneousData.delete.attachment.confirmation" />')">
				<spring:message code="miscellaneousData.delete.attachment" var="deleteAttachmentMessage" />
				<jstl:out value="${deleteAttachmentMessage}"/>
			</a>
		</display:column>
	
	</display:table>
	
	<spring:url var="newAttachment" value="/miscellaneousData/rookie/newAttachment.do">
		<spring:param name="miscellaneousDataId" value="${miscellaneousDataId}"/>
	</spring:url>
	<p>
		<a href="${newAttachment}">
			<spring:message code="miscellaneousData.new.attachment" var="newAttachmentMessage" />
			<jstl:out value="${newAttachmentMessage}"/>
		</a>
	</p>
	
	<acme:cancel url="/curriculum/rookie/show.do?curriculumId=${curriculumId}" code="rookie.back" /> 

</security:authorize>
