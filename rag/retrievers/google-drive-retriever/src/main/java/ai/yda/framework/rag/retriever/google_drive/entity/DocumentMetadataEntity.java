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

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;

/**
 * Represents metadata information about a document in the Google Drive.
 * This entity is mapped to the "document_metadata" table in the database.
 * It stores information such as the document's ID, name, description, URI,
 * and timestamps for creation and modification.
 *
 * @author Iryna Kopchak
 * @since 0.2.0
 */
@Entity
@Table(name = "document_metadata")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadataEntity {

    @Id
    @Column(name = "document_id")
    private String documentId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "ai_description")
    private String aiDescription;

    @Column(name = "webViewLink")
    private String webViewLink;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "modified_at")
    private OffsetDateTime modifiedAt;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "drive_id")
    private String driveId;

    /**
     * Parent folder relationship:
     * This references another entity in the same table.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private DocumentMetadataEntity parent;

    /**
     * This field represents the list of document content entities associated with this document metadata.
     * It is the inverse side of the relationship, mapped by the 'documentMetadata' field in the DocumentContentEntity.
     */
    @Singular
    @OneToMany(mappedBy = "documentMetadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentContentEntity> documentContents;

    /**
     * Convenience method to distinguish folders from files.
     */
    @Transient
    public boolean isFolder() {
        return "application/vnd.google-apps.folder".equalsIgnoreCase(this.mimeType);
    }
}
