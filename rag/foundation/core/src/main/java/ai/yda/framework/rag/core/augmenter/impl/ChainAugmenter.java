package ai.yda.framework.rag.core.augmenter.impl;

import java.util.ArrayList;
import java.util.List;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import lombok.NoArgsConstructor;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.model.RagContext;

/**
 * ChainAugmenter is an implementation of the Augmenter interface that allows for
 * the sequential application of multiple Augmenter instances.
 * <p>
 * This class maintains a list of augmenters and applies them in order to the context,
 * enabling a chain of responsibility pattern for context augmentation.
 *
 * @param <REQUEST> the type of the request object
 * @param <CONTEXT> the type of the context object
 */
@NoArgsConstructor
public class ChainAugmenter<REQUEST extends AssistantRequest, CONTEXT extends RagContext>
        implements Augmenter<REQUEST, CONTEXT> {

    private final List<Augmenter<REQUEST, CONTEXT>> augmenters = new ArrayList<>();

    @Override
    public REQUEST augment(REQUEST request, CONTEXT context) {
        for (Augmenter<REQUEST, CONTEXT> augmenter : augmenters) {
            augmenter.augment(request, context);
        }
        return request;
    }

    public void addAugmenter(Augmenter<REQUEST, CONTEXT> augmenter) {
        this.augmenters.add(augmenter);
    }
}
