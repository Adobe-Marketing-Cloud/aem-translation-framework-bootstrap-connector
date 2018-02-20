<%--
/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2014 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
--%>
<%@page session="false"
            contentType="text/html"
            pageEncoding="utf-8"
            import="com.day.cq.i18n.I18n"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/hideeditok.jsp"%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"%>
<%
    I18n i18n = new I18n(request);
    String resPath = resource.getPath().replace("/jcr:content", "");
%>
<div>
    <h3><%= i18n.get("Bootstrap Translation Connector Settings") %></h3>
    <img src="<%= xssAPI.encodeForHTMLAttr(thumbnailPath) %>" alt="<%= xssAPI.encodeForHTMLAttr(serviceName) %>" style="float: left;" />
    <ul style="float: left; margin: 0px;">
        <li><div class="li-bullet"><strong><%=
        i18n.get("To get a free trial account visit www.adobe.com/bootstrap.html") %>
        </strong></div></li>
        <li><div class="li-bullet"><strong><%= i18n.get("Demo Server URL") %>: </strong><%=
        xssAPI.encodeForHTML(properties.get("dummyserverurl", "")) %></div></li>
        <li><div class="li-bullet"><strong><%= i18n.get("Demo Configuration ID") %>: </strong><%=
        xssAPI.encodeForHTML(properties.get("dummyconfigid", "").replaceAll(".", "*")) %></div></li>
        <li class="config-successful-message when-config-successful" style="display: none">
            <%=
            i18n.get("Bootstrap Translator Connector configuration is successful.") %></li>
    </ul>
</div>
