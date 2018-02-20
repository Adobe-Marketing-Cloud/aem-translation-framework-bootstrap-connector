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
<%@page contentType="text/html"
        session="false"
        pageEncoding="utf-8"
        import="javax.jcr.Node,
                java.util.Iterator,
                com.day.cq.wcm.webservicesupport.Configuration,
                com.day.cq.wcm.webservicesupport.Service,
                org.apache.commons.lang.StringEscapeUtils,
                org.apache.sling.api.resource.Resource"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"%>
<%
    String id = currentPage.getName();
    String title = xssAPI.encodeForHTML(properties.get("jcr:title", id));
    String description = xssAPI.encodeForHTML(properties.get("jcr:description", ""));
    String path = resource.getPath();
    String resourceType = resource.getResourceType();
    String dialogPath = resource.getResourceResolver().getResource(resourceType).getPath() + "/dialog";
%><body>
<div><cq:include path="trail" resourceType="cq/cloudserviceconfigs/components/trail"/></div>
<p class="cq-clear-for-ie7"></p>
<h1 style="padding-top: 15px;"><%= title %></h1>

<p><%= description %></p>
<div>
    <script type="text/javascript">
        CQ.WCM.edit({
            "path":"<%= path %>",
            "dialog":"<%= dialogPath %>",
            "type":"<%= resourceType %>",
            "editConfig":{
                "xtype":"editbar",
                "listeners":{
                    "afteredit":"REFRESH_PAGE"
                },
                "inlineEditing":CQ.wcm.EditBase.INLINE_MODE_NEVER,
                "disableTargeting": true,
                "actions":[
                    CQ.I18n.getMessage("Configuration"),
                    {
                        "xtype": "tbseparator"
                    },
                    CQ.wcm.EditBase.EDIT
                    <%
                    if (serviceUrl != null) {
                    %>
                    ,
                    {
                        "xtype": "tbseparator"
                    },
                    {
                        "xtype": "tbtext",
                        "text": "<a href='<%=serviceUrl%>' target='_blank' style='color: #15428B; cursor: pointer; text-decoration: underline'>" + CQ.I18n.getMessage("Getting started") + "</span>"
                    }
                    <%
                    }
                    %>
                ]
            }
        });
    </script>
</div>
<cq:include script="content.jsp" />
    <%
        if (properties.get("dummyserverurl", "").isEmpty()
                || properties.get("dummyconfigid", "").isEmpty()) {
    %>
    <cq:include script="opendialog.jsp" />
    <%
        }
    %>
</body>
