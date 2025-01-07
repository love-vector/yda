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
package ai.yda.framework.rag.retriever.google_drive.entity;

import jakarta.persistence.*;

import lombok.*;

/**
 * Represents the content of a document stored in the Google Drive.
 * This entity is mapped to the "document_content" table in the database.
 * It stores information about document chunks, including their content and name.
 *
 * @author Iryna Kopchak
 * @since 0.2.0
 */
@Entity
@Table(name = "document_content")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "chunk_name")
    private String chunkName;

    @Column(name = "chunk_content")
    private String chunkContent;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentMetadataEntity documentMetadata;
}
