package com.cha.markit.session;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, List<byte[]>> sessionStore = new ConcurrentHashMap<>();

    public String createSession(List<byte[]> value) {
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        return sessionId;
    }

    public List<byte[]> getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }

    public void expireSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
}
