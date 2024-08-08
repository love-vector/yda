package ai.yda.framework.rag.retriever.filesystem.util;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;

import ai.yda.framework.rag.retriever.filesystem.exception.FileReadException;

public final class FileUtil {

    public static String readPdf(final File file) {
        try (var document = Loader.loadPDF(file)) {
            return new PDFTextStripper().getText(document);
        } catch (final IOException exception) {
            throw new FileReadException(exception);
        }
    }

    private FileUtil() {}
}
