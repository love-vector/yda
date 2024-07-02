package ai.yda.common.shared.service;

public interface SessionProvider {

    void setSessionId(String sessionId);

    String getThreadId();

    void setThreadId(String threadId);
}
