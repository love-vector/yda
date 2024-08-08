package ai.yda.framework.channel.core;

public interface Channel<REQUEST, RESPONSE> {

    RESPONSE processRequest(REQUEST request);
}
