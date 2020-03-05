/*
 ADOBE CONFIDENTIAL
 Copyright 2016 Adobe Systems Incorporated
 All Rights Reserved.
 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any. The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */
(function(document, XSS, $) {

    "use strict";
    var subscriptionKeyComponent = ".bootStrapServerID";

    /*
        Registering a custom validator before form is submitted via save and close
    */
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
      selector: subscriptionKeyComponent,
      validate: function(e) {
          return verifySubscriptionKey(e);
      }
    });


    function verifySubscriptionKey(e) {
        var key = $(subscriptionKeyComponent)[0].value;
        // Partners may write their own logic to verify that the key or other identifier is valid via any ajax call to their server
        var keyValid = false;
        /*
            Some code to check if the key is valid
        */
        keyValid = true;
        if (keyValid) {
            return "";
        } else {
            return Granite.I18n.get("Key invalid");
        }

    }

})(document, _g.XSS, Granite.$);