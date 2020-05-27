<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
<#if message??>
    <div class="alert alert-danger" role="alert">
        ${message}
    </div>
</#if>
<#if messageOK??>
    <div class="alert alert-primary" role="alert">
        ${messageOK}
    </div>
</#if>
<#if messagePass??>
    <div class="alert alert-primary" role="alert">
        ${messagePass}
    </div>
</#if>
<@l.login "/login" false/>
</@c.page>