package ai.yda.framework.rag.base.model;

import ai.yda.framework.rag.core.model.RagResponse;

public class BaseRagResponse implements RagResponse {

    private String response;

    @Override
    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
