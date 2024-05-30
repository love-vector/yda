package ai.yda.framework.intent;

import java.util.*;

public interface IntentRelationalStorage {

    Intent getIntentByVectorId(UUID vectorId);

    Set<Intent> getIntents();

    Intent createIntent(Intent intent);

    void deleteIntent(Intent intent);
}
