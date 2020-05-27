
<a class="btn btn-primary" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample">
    Message editor
</a>
<div class="collapse <#if message??>show</#if>" id="collapseExample">
    <div class="form-group mt-3">
        <form method="post" enctype="multipart/form-data">
            <div class="form-group">
               <input type="text" name="text" value="<#if message??>${message.text}</#if>" class="form-control ${(textError??)?string('is-invalid', '')}" placeholder="Введите сообщение" />
                <#if textError??>
                    <div class="invalid-feedback">
                        ${textError}
                    </div>
                </#if>
            </div>
            <div class="form-group">
                <input type="text" value="<#if message??>${message.tag}</#if>" name="tag" class="form-control" placeholder="Введите тэг" />
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input type="file" name="file" class="custom-file-input" id="customFile" />
                    <label class="custom-file-label" for="customFile">Choose File</label>
                </div>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <input type="hidden" name="id" value="<#if message??>${message.id}<#else>-1</#if>" />
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Save message</button>
            </div>
        </form>
    </div>
</div>
