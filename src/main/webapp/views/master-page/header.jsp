<%--
 * header.jsp
 *
 * Copyright (C) 2019 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<a href="#"><img src="${imageURL}" height= 150px width= 500px alt="Acme Rookie Co., Inc." /></a>
</div>

<div>
	<ul id="jMenu">
		<!-- Do not forget the "fNiv" class for the first level links !! -->
		<security:authorize access="hasRole('ADMIN')">
			<li><a class="fNiv"><spring:message	code="master.page.administrator" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="administrator/administrator/create.do"><spring:message code="master.page.administrator.createAdmin" /></a></li>		
					<li><a href="administrator/auditor/create.do"><spring:message code="master.page.administrator.createAuditor" /></a></li>			
					<li><a href="statistics/administrator/show.do"><spring:message code="master.page.administrator.statistics" /></a></li>
					<li><a href="configuration/administrator/list.do"><spring:message code="master.page.administrator.configuration" /></a></li>
					<li><a href="broadcast/administrator/send.do"><spring:message code="master.page.administrator.broadcast" /></a></li>
					<li><a href="administrator/administrator/computeScore.do"><spring:message code="master.page.administrator.computeScore" /></a></li>
					
					<jstl:if test="${!isMessageBroadcasted}">
					<li><a href="broadcast/administrator/sendRebranding.do"><spring:message code="master.page.administrator.broadcastRebranding" /></a></li>
					</jstl:if>
					
					<li><a href="administrator/suspicious/list.do"><spring:message code="master.page.administrator.banUnban" /></a></li>
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('COMPANY')">
			<li><a class="fNiv"><spring:message	code="master.page.company" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="position/company/list.do"><spring:message code="master.page.company.PositionList" /></a></li>
					<li><a href="problem/company/list.do"><spring:message code="master.page.company.listProblems" /></a></li>	
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('ROOKIE')">
			<li><a class="fNiv"><spring:message	code="master.page.rookie" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="finder/rookie/list.do"><spring:message code="master.page.rookie.finder" /></a></li>
					<li><a href="curriculum/rookie/list.do"><spring:message code="master.page.rookie.curriculums" /></a></li>
					<li><a href="application/rookie/list.do"><spring:message code="master.page.rookie.application" /></a></li>			
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('PROVIDER')">
			<li><a class="fNiv"><spring:message	code="master.page.provider" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="item/provider/list.do"><spring:message code="master.page.provider.items" /></a></li>
					<li><a href="sponsorship/provider/list.do"><spring:message code="master.page.provider.sponsorship" /></a></li>
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('AUDITOR')">
			<li><a class="fNiv"><spring:message	code="master.page.auditor" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="position/auditor/listAssignablePositions.do"><spring:message code="master.page.assignablePositions" /></a></li>
					<li><a href="audit/auditor/list.do"><spring:message code="master.page.listAudits" /></a></li>
					<!-- CONTROL_CHECK -->
					<li><a href="controlEntity/auditor/list.do"><spring:message code="master.page.company.listControlEntity" /></a></li>
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="isAnonymous()">
			<li><a class="fNiv" href="security/login.do"><spring:message code="master.page.login" /></a></li>
			<li><a class="fNiv"><spring:message	code="master.page.register" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="anonymous/rookie/create.do"><spring:message code="master.page.createRookie" /></a></li>	
					<li><a href="anonymous/company/create.do"><spring:message code="master.page.createCompany" /></a></li>
					<li><a href="anonymous/provider/create.do"><spring:message code="master.page.createProvider" /></a></li>				
				</ul>
			</li>
			
		</security:authorize>
		
		<security:authorize access="isAuthenticated()">
			<li>
				<a class="fNiv"> 
					<spring:message code="master.page.profile" /> 
			        (<security:authentication property="principal.username" />)
				</a>
				<ul>
					<li class="arrow"></li>				
					<li><a href="authenticated/showProfile.do"><spring:message code="master.page.myProfile" /> </a></li>
					<li><a href="authenticated/edit.do"><spring:message code="master.page.editPersonalData" /> </a></li>
					<li><a href="message/actor/list.do"><spring:message code="master.page.mailSystem" /> </a></li>
					<li><a href="j_spring_security_logout"><spring:message code="master.page.logout" /> </a></li>
				</ul>
			</li>
			
			
		</security:authorize>
		
		<security:authorize access="permitAll">
			
			<li><a href="anonymous/position/list.do"><spring:message code="master.page.publicPositions" /></a></li>
			<li><a href="anonymous/company/list.do"><spring:message code="master.page.publicCompanies" /></a></li>
			<li><a href="anonymous/provider/list.do"><spring:message code="master.page.publicProviders" /></a></li>
			<li><a href="anonymous/item/list.do"><spring:message code="master.page.provider.items" /></a></li>
			
			<li><a class="fNiv"><spring:message	code="master.page.termsAndConditions" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="anonymous/termsAndConditionsEN.do"><spring:message code="master.page.termsAndConditionsEN" /></a></li>
					<li><a href="anonymous/termsAndConditionsES.do"><spring:message code="master.page.termsAndConditionsES" /></a></li>					
					
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasAnyRole('ROOKIE', 'COMPANY', 'AUDITOR', 'PROVIDER')">
		<li><a href="authenticated/deleteUser.do" onClick="return confirm('<spring:message code="delete.user.confirmation" />')"><spring:message code="master.page.deleteUser" /> </a></li>
		</security:authorize>
		
		
	</ul>
</div>

<div>
	<a href="?language=en">en</a> | <a href="?language=es">es</a>
</div>

