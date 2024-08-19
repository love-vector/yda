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
package ai.yda.framework.rag.core.util;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.MatchResult;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.TextNode;

public final class ContentUtil {

    /**
     * Preprocesses the content by formatting it, removing HTML tags but preserving links,
     * lemmatizing, removing punctuation, and spaces and paragraphs.
     *
     * @param content the content to be preprocessed
     * @return the preprocessed content as a string
     */
    public static String preprocessContent(final String content) {
        var formattedContent = new String(content.getBytes(), StandardCharsets.UTF_8).toLowerCase();
        formattedContent = removeHtmlButPreserveLinks(formattedContent);
        formattedContent = lemmatizeAndRemovePunctuation(formattedContent);
        formattedContent = removeSpacesAndParagraphs(formattedContent);
        return formattedContent;
    }

    /**
     * Splits the content into chunks with a maximum number of characters and restores corrupted URLs.
     *
     * @param content       the content to be split
     * @param maxCharacters the maximum number of characters per chunk
     * @return a list of string chunks
     */
    public static List<String> splitContent(final String content, final Integer maxCharacters) {
        var equalChunks = StringUtil.splitIntoEqualsParts(content, maxCharacters);
        var urlMatches = RegexUtil.urlMatches(content);
        restoreCorruptedUrlsInChunks(equalChunks, urlMatches);
        return equalChunks;
    }

    /**
     * Removes HTML tags but preserves links in the content.
     *
     * @param content the content with HTML tags
     * @return the content with HTML tags removed but links preserved
     */
    private static String removeHtmlButPreserveLinks(final String content) {
        var document = Jsoup.parse(content);
        var links = document.select("a");
        links.parallelStream()
                .forEach(link ->
                        link.replaceWith(new TextNode(String.format(" %s  %s ", link.text(), link.attr("href")))));
        return document.text();
    }

    /**
     * Lemmatizes and removes punctuation from the content.
     *
     * @param content the content to be lemmatized and cleaned of punctuation
     * @return the content with lemmatized words and punctuation removed
     */
    private static String lemmatizeAndRemovePunctuation(final String content) {
        var properties = PropertiesUtils.asProperties("annotators", "tokenize, pos, lemma");
        var document = new Annotation(content);
        new StanfordCoreNLP(properties).annotate(document);
        var sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        var formattedContent = new StringBuilder();
        for (var sentence : sentences) {
            for (var token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                var pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if (!pos.equals(".") && !pos.equals(",") && !pos.equals("!") && !pos.equals("?")) {
                    var lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    formattedContent.append(lemma).append(" ");
                }
            }
        }
        return formattedContent.toString();
    }

    /**
     * Removes spaces and paragraphs from the content.
     *
     * @param content the content to be cleaned
     * @return the content with spaces and paragraphs removed
     */
    private static String removeSpacesAndParagraphs(final String content) {
        return content.replaceAll("\\s+", " ");
    }

    /**
     * Restores corrupted URLs in the given chunks. Corrupted links are links that have been split
     * across adjacent chunks due to the chunking process. This method reassembles these links by
     * identifying their positions and reconstructing them within the appropriate chunks.
     *
     * @param equalChunks the list of chunks
     * @param urlMatches  the list of URL match results
     */
    private static void restoreCorruptedUrlsInChunks(
            final List<String> equalChunks, final List<MatchResult> urlMatches) {
        if (!equalChunks.isEmpty() && !urlMatches.isEmpty()) {
            var chunksLength = equalChunks.get(0).length();
            for (var urlMatch : urlMatches) {
                var url = urlMatch.group();
                var urlStartPositionInChunk = urlMatch.start() % chunksLength;
                if (urlStartPositionInChunk + url.length() > chunksLength) {
                    var chunkIndex = urlMatch.start() / chunksLength;
                    equalChunks.set(
                            chunkIndex, equalChunks.get(chunkIndex).substring(0, urlStartPositionInChunk) + url);
                    var urlEndPositionInChunk = urlMatch.end() % chunksLength;
                    equalChunks.set(
                            chunkIndex + 1,
                            url + equalChunks.get(chunkIndex + 1).substring(urlEndPositionInChunk));
                }
            }
        }
    }

    private ContentUtil() {}
}
