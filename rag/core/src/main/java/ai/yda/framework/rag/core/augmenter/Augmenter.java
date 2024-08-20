/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.rag.core.augmenter;

import java.util.List;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

/**
 * Provides a generic mechanism for modifying or enriching the retrieved contexts to enhance the final response
 * generation. This can involve filtering, re-ranking, or adding new information.
 *
 * @param <REQUEST> the generic type of the request from the user, which must extend {@link RagRequest}.
 * @param <CONTEXT> the generic type of the context data that will be augmented or enhanced based on the given request,
 *                  which must extend {@link RagContext}.
 * @author Nikita Litvinov
 * @see RagRequest
 * @see RagContext
 * @since 0.1.0
 */
public interface Augmenter<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    /**
     * Augments the given list of context objects based on the provided request.
     *
     * @param request  the request object that contains query data from the user.
     * @param contexts a list of context objects to be augmented. These contexts are modified or enriched according to
     *                 the logic defined in the implementation of this method.
     * @return a list of augmented context objects, which may be the same as the input list or a modified version,
     * depending on the augmentation logic.
     */
    List<CONTEXT> augment(REQUEST request, List<CONTEXT> contexts);
}
