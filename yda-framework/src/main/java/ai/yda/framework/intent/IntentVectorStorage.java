package ai.yda.framework.intent;

import java.util.List;

import org.springframework.ai.document.Document;

public interface IntentVectorStorage {

    Document createIntent(Intent intent);

    void deleteIntent(Intent intent);

    List<Document> search(String message);
}
