package ai.yda.framework.rag.base.model;

import ai.yda.framework.rag.core.model.RagRequest;

public class BaseRagRequest implements RagRequest {

    private String message;

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String  message){
        this.message = message;
    }
}
