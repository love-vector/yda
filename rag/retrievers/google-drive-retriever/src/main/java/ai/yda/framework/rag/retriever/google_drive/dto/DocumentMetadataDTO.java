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
package ai.yda.framework.rag.retriever.google_drive.dto;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.*;

/**
 * DTO for transferring document metadata information.
 * Contains only the fields necessary for external usage, without persistence annotations.
 *
 * @since 0.2.0
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadataDTO {

    private String documentId;
    private String name;
    private String description;
    private String summary;
    private String webViewLink;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
    private String mimeType;
    private String driveId;

    private String parentId;

    @Singular
    private List<DocumentContentDTO> documentContents;

    /**
     * Convenience method to distinguish folders from files.
     */
    public boolean isFolder() {
        return "application/vnd.google-apps.folder".equalsIgnoreCase(this.mimeType);
    }
}
