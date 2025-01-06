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
package ai.yda.framework.rag.retriever.google_drive.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;

import org.springframework.stereotype.Component;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;

@Component
@RequiredArgsConstructor
public class ExelDocumentProcessor implements DocumentProcessor {

    private final DocumentContentMapper documentContentMapper;

    @Override
    public List<DocumentContentEntity> processDocument(
            final InputStream inputStream, final DocumentMetadataEntity documentMetadata) throws IOException {
        var workbook = new XSSFWorkbook(inputStream);
        var documentContents = new ArrayList<DocumentContentEntity>();

        for (Sheet sheet : workbook) {
            var sheetContent = processSheet(sheet);
            documentContents.add(documentContentMapper.toEntity(sheet.getSheetName(), sheetContent, documentMetadata));
        }

        workbook.close();
        return documentContents;
    }

    private String processSheet(final Sheet sheet) {
        var jsonArray = new JSONArray();

        var maxCell = calculateMaxCell(sheet);

        for (Row row : sheet) {
            if (row != null) {
                var rowData = processRow(row, maxCell);
                if (!isRowEmpty(rowData)) {
                    jsonArray.put(rowData);
                }
            }
        }

        return jsonArray.toString();
    }

    private int calculateMaxCell(final Sheet sheet) {
        var maxCell = 0;
        for (Row row : sheet) {
            if (row != null) {
                for (int j = row.getLastCellNum() - 1; j >= 0; j--) {
                    var cell = row.getCell(j);
                    if (cell != null && !cell.toString().trim().isEmpty()) {
                        maxCell = Math.max(maxCell, j + 1);
                        break;
                    }
                }
            }
        }
        return maxCell;
    }

    //TODO: process sheet formulas
    private List<String> processRow(final Row row, final int maxCell) {
        var rowData = new ArrayList<String>();

        for (int i = 0; i < maxCell; i++) {
            Cell cell = row.getCell(i);
            rowData.add(cell != null ? cell.toString().trim() : "");
        }

        return rowData;
    }

    private boolean isRowEmpty(final List<String> rowData) {
        return rowData.stream().allMatch(String::isEmpty);
    }
}
