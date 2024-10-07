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
package ai.yda.framework.rag.retriever.website.extractor.pipeline;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import ai.yda.framework.rag.retriever.website.extractor.model.ExtractionResult;
import ai.yda.framework.rag.retriever.website.extractor.util.ExtractionConstant;
import ai.yda.framework.rag.retriever.website.extractor.util.WebUtil;

/**
 * A {@link Pipeline} implementation for collecting extraction results during web crawling.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public class ResultingPipeline implements Pipeline {

    /**
     * Map for storing extraction results, where the key is the URL and the value is the {@link ExtractionResult}.
     */
    private final Map<String, ExtractionResult> results = new ConcurrentHashMap<>();

    /**
     * Default constructor for {@link ResultingPipeline}.
     */
    public ResultingPipeline() {}

    /**
     * Collects and stores extraction results of crawling task ({@link PageProcessor#process(Page)}).
     *
     * @param resultItems the result items containing the extraction results.
     * @param task        the crawling task being processed.
     */
    @Override
    public void process(final ResultItems resultItems, final Task task) {
        var url = resultItems.getRequest().getUrl();
        if (!WebUtil.isSitemapUrl(url)) {
            results.put(
                    url,
                    ExtractionResult.builder()
                            .url(url)
                            .content(resultItems.get(ExtractionConstant.PAGE_TEXT_KEY))
                            .build());
        }
    }

    /**
     * Returns the set of extracted results.
     *
     * @return a set of {@link ExtractionResult} objects.
     */
    public Set<ExtractionResult> getResults() {
        return new HashSet<>(results.values());
    }
}
