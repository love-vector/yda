package ai.yda.framework.intent;

import java.util.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntentService {

    private final IntentRelationalStorage intentRelationalStorage;
    private final IntentVectorStorage intentVectorStorage;

    public Set<Intent> getIntents() {
        return intentRelationalStorage.getIntents();
    }

    public Intent craeteIntent(final Intent intent) {
        var document = intentVectorStorage.createIntent(intent);
        intent.setVectorId(UUID.fromString(document.getId()));
        return intentRelationalStorage.createIntent(intent);
    }

    public void deleteIntent(final Intent intent) {
        intentRelationalStorage.deleteIntent(intent);
        intentVectorStorage.deleteIntent(intent);
    }

    public List<IntentApproximation> similaritySearch(final String message) {
        return intentVectorStorage.search(message).stream()
                .map(document -> {
                    var intent = intentRelationalStorage.getIntentByVectorId(UUID.fromString(document.getId()));
                    var distance = (Float) document.getMetadata().get("distance");
                    return IntentMapper.INSTANCE.toApproximation(intent, distance);
                })
                .toList();
    }
}
