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
package ai.yda.framework.rag.retriever.google_drive.service.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;

/**
 * <p>
 * <b>Limitations:</b> This class relies on {@link DataFormatter} for cell value formatting.
 * For more details on its capabilities and restrictions, refer to the {@link DataFormatter} documentation.
 * </p>
 */
public class ExelDocumentProcessor implements DocumentProcessor {

    private final DocumentContentMapper documentContentMapper;
    private final DataFormatter dataFormatter;

    public ExelDocumentProcessor(final @NonNull DocumentContentMapper documentContentMapper) {
        this.documentContentMapper = documentContentMapper;
        this.dataFormatter = new DataFormatter();
    }

    @Override
    public List<DocumentContentDTO> processDocument(final InputStream inputStream, final String documentMetadataId)
            throws IOException {
        var workbook = new XSSFWorkbook(inputStream);
        var documentContents = new ArrayList<DocumentContentDTO>();
        var evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (var sheet : workbook) {
            var sheetContent = processSheet(sheet, evaluator);
            documentContents.add(documentContentMapper.toDTO(sheet.getSheetName(), sheetContent, documentMetadataId));
        }

        workbook.close();
        return documentContents;
    }

    private String processSheet(final Sheet sheet, final FormulaEvaluator evaluator) {
        var jsonArray = new JSONArray();

        var maxCell = calculateMaxCell(sheet);

        for (var row : sheet) {
            if (row != null) {
                var rowData = processRow(row, maxCell, evaluator);
                if (!isRowEmpty(rowData)) {
                    jsonArray.put(rowData);
                }
            }
        }

        return StringEscapeUtils.unescapeJava(jsonArray.toString());
    }

    private int calculateMaxCell(final Sheet sheet) {
        var maxCell = 0;
        for (var row : sheet) {
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

    private List<String> processRow(final Row row, final int maxCell, final FormulaEvaluator evaluator) {
        var rowData = new ArrayList<String>();

        for (int i = 0; i < maxCell; i++) {
            var cell = row.getCell(i);
            rowData.add((cell == null) ? "" : dataFormatter.formatCellValue(cell, evaluator));
        }

        return rowData;
    }

    private boolean isRowEmpty(final List<String> rowData) {
        return rowData.stream().allMatch(String::isEmpty);
    }
}
