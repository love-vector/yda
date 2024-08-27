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
 * Provides a generic mechanism for modifying or enriching the retrieved Contexts to enhance the final Response
 * generation. This can involve filtering, re-ranking, or adding new information.
 *
 * @param <REQUEST> the generic type of the Request from the User, which must extend {@link RagRequest}.
 * @param <CONTEXT> the generic type of the Context data that will be augmented or enhanced based on the given Request,
 *                  which must extend {@link RagContext}.
 * @author Nikita Litvinov
 * @see RagRequest
 * @see RagContext
 * @since 0.1.0
 */
public interface Augmenter<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    /**
     * Augments the given list of Context objects based on the provided Request.
     *
     * @param request  the Request object that contains query data from the User.
     * @param contexts a list of Context objects to be augmented. These Contexts are modified or enriched according to
     *                 the logic defined in the implementation of this method.
     * @return a list of augmented Context objects, which may be the same as the input list or a modified version,
     * depending on the augmentation logic.
     */
    List<CONTEXT> augment(REQUEST request, List<CONTEXT> contexts);
}
