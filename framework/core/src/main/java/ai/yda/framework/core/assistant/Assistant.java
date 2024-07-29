package ai.yda.framework.core.assistant;

public interface Assistant<REQUEST, RESPONSE> {

    RESPONSE processRequest(REQUEST request);
}
