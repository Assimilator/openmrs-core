<ul class="navList">	
	<li id="homeNavLink" class="firstChild">
		<a href="${pageContext.request.contextPath}/"><openmrs:message code="Navigation.home"/></a>
	</li>
	<openmrs:hasPrivilege privilege="Add Patients">
		<li id="findPatientNavLink">
			<a href="${pageContext.request.contextPath}/findPatient.htm">
					<openmrs:message code="Navigation.findCreatePatient"/>
			</a>
		</li>	
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Add Patients" inverse="true">
		<openmrs:hasPrivilege privilege="View Patients">
			<li id="findPatientNavLink">
				<a href="${pageContext.request.contextPath}/findPatient.htm">
						<openmrs:message code="Navigation.findPatient"/>
				</a>
			</li>
		</openmrs:hasPrivilege>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="View Concepts">
		<li id="dictionaryNavLink">
			<a href="${pageContext.request.contextPath}/dictionary"><openmrs:message code="Navigation.dictionary"/></a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:extensionPoint pointId="org.openmrs.gutter.tools" type="html" 
		requiredClass="org.openmrs.module.web.extension.LinkExt">
		<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
			<li>
			<a href="${pageContext.request.contextPath}/${extension.url}"><openmrs:message code="${extension.label}"/></a>
			</li>
		</openmrs:hasPrivilege>
	</openmrs:extensionPoint>
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li id="administrationNavLink">
			<a href="${pageContext.request.contextPath}/admin"><openmrs:message code="Navigation.administration"/></a>
		</li>
	</openmrs:hasPrivilege>
</ul>