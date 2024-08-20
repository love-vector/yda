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
package ai.yda.framework.rag.retriever.filesystem.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ai.yda.framework.rag.retriever.shared.RetrieverProperties;

/**
 * Provides configuration properties for filesystem retriever. These properties can be customized through the
 * application’s external configuration, such as a properties file, YAML file, or environment variables. The
 * properties include collectionName, topK, isProcessingEnabled, clearCollectionOnStartup and fileStoragePath settings.
 * <p>
 * The properties are prefixed with {@link #CONFIG_PREFIX} and can be customized by defining values under this prefix
 * in the external configuration.
 * <pre>
 * Example configuration in a YAML file:
 * ai:
 *    yda:
 *      framework:
 *         rag:
 *            retriever:
 *                filesystem:
 *                    collectionName: your-collection-name
 *                    topK: your-top-k
 *                    isProcessingEnabled: true/false
 *                    clearCollectionOnStartup: true/false
 *                    fileStoragePath: your-file-storage-path
 * </pre>
 *
 * @author Dmitry Marchuk
 * @author Iryna Kopchak
 * @since 0.1.0
 */
@Setter
@Getter
@ConfigurationProperties(RetrieverFilesystemProperties.CONFIG_PREFIX)
public class RetrieverFilesystemProperties extends RetrieverProperties {

    /**
     * The configuration prefix used to reference properties related to this channel in application configurations.
     * This prefix is used for binding properties within the particular namespace.
     */
    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.retriever.filesystem";

    private String fileStoragePath;

    /**
     * Default constructor for {@link RetrieverFilesystemProperties}.
     */
    public RetrieverFilesystemProperties() {}
}
