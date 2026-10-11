package multipatternscanner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public final class DocumentLoader {

    private DocumentLoader() {
    }

    public static String load(Path path) throws IOException {
        String fileName = path.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".txt")) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }

        if (fileName.endsWith(".pdf")) {
            return loadPdf(path);
        }

        throw new IOException("Unsupported file type. Please use .txt or .pdf.");
    }

    private static String loadPdf(Path path) throws IOException {
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
