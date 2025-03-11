package com.resume.analyzer.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class PDFService {
    
    /**
     * Extracts text content from a PDF file
     * @param pdfFile The PDF file to extract text from
     * @return The extracted text content
     * @throws IOException If there is an error processing the PDF
     */
    public String extractTextFromPDF(MultipartFile pdfFile) throws IOException {
        PagePdfDocumentReader pdfDocumentReader=new PagePdfDocumentReader(pdfFile.getResource(),
                PdfDocumentReaderConfig
                        .builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter
                                .builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        return pdfDocumentReader.read().toString();
    }
}