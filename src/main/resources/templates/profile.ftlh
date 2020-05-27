<#import "parts/common.ftlh" as c>

<h5>${username}</h5>
<@c.page>
<form method="post">
    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Password: </label>
        <div class="col-sm-6">
            <input type="password" name="password" class="form-control ${(error??)?string('is-invalid', '')}" placeholder="password"/>
            <#if error??>
                <div class="invalid-feedback">
                    ${error}
                </div>
            </#if>
        </div>
    </div>
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Email: </label>
            <div class="col-sm-6">
                <input type="email" name="email" value="${email!''}"
                       class="form-control ${(error??)?string('is-invalid', '')}"
                       placeholder="some@some.com"/>
                <#if error??>
                    <div class="invalid-feedback">
                        ${error}
                    </div>
                </#if>
            </div>
        </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button type="submit" class="btn btn-primary">Save</button>
</form>
</@c.page>