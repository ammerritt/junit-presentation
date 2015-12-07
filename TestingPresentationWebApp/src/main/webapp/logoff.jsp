<%@ taglib uri="http://jakarta.apache.org/taglibs/session-1.0" prefix="sess"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/response-1.0" prefix="res"%>

<html>
    <head>
        <title>Logoff</title>
    </head>
    <body bgcolor="white">
        <sess:invalidate />
        <res:sendRedirect>
            <res:encodeRedirectUrl>jsp/index.jsp</res:encodeRedirectUrl>
        </res:sendRedirect>
    </body>
</html>
