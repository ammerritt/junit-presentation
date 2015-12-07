<%@ taglib uri="http://jakarta.apache.org/taglibs/request-1.0" prefix="req"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/response-1.0" prefix="res"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<html>
    <head>
        <title><bean:message key="page.index.title" /></title>
        <style type="text/css" media="all">
            @import "../css/main.css";
        </style>
        <html:javascript formName="encryptForm"/>
    </head>
    <body>

        <%-- Get a handle on the request. --%>
        <req:request id="req" />
    
        <div class="left"><bean:message key="label.loggedinas.value" />: 
        <b><jsp:getProperty name="req" property="remoteUser" /></b></div>
        <div class="right"><a href="<res:encodeUrl>../logoff.jsp</res:encodeUrl>"><bean:message key="label.logout.value" /></a></div>
        
        <br/>
        <br/>
        <html:errors/>
        <br/>
        <br/>
   
        <req:isUserInRole role="USER">
            <html:form action="/jsp/Encrypt.do" onsubmit="return validateEncryptForm(this);" styleId="encryptForm">
                <html:hidden property="dispatch" value="encrypt" />
                <bean:message key="label.password.value" />:&nbsp;<html:text property="password" styleId="encryptPassword" size="30" />
                <br />
                <br />
                <bean:message key="label.encrypted.value" />:&nbsp;<html:text property="encPassword" styleId="encryptEncPassword" size="100" />
                <br />
                <br />
                <html:submit title="Submit" styleId="encryptSubmit">
                    <bean:message key="button.encrypt.submit.value" />
                </html:submit>
            </html:form>
        </req:isUserInRole>
        <req:isUserInRole role="USER" value="false">
            You are not in the correct group to encrypt passwords.
        </req:isUserInRole>
        
        <!-- TODO: would need to change this role to something like PWORD_DECRYPT -->
            <req:isUserInRole role="USER">
                <html:form action="/jsp/Decrypt.do" styleId="decryptForm">
                    <input type="hidden" tabindex="-1" name="dispatch" value="decrypt" />
                    <bean:message key="label.encrypted.value" />:&nbsp;<html:text property="encPassword" styleId="decryptEncPassword" size="100" />
                    <br />
                    <br />
                    <bean:message key="label.password.value" />:&nbsp;<html:text property="password" styleId="decryptPassword" size="30" />
                    <br />
                    <br />
                    <html:submit title="Submit" styleId="decryptSubmit">
                        <bean:message key="button.decrypt.submit.value" />
                    </html:submit>
                </html:form>
            </req:isUserInRole>
    </body>
</html>
