package ai.yda.framework.rag.base.model;

import java.util.List;
import java.util.Map;

public class RagContext implements Rag  {

    List<String> getChunks();

    Map<String, Object> metadata();
}
