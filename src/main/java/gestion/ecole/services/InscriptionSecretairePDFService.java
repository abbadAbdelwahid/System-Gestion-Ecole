package gestion.ecole.services;

import gestion.ecole.models.Inscription;
import gestion.ecole.models.Etudiant;
import gestion.ecole.models.Module;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;
import java.time.LocalDateTime;

public class InscriptionSecretairePDFService {
    private final EtudiantService etudiantService = new EtudiantService();
    private final ModuleService moduleService = new ModuleService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Constantes de style
    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 20;
    private static final float TABLE_TOP_Y = 650;
    private static final float CELL_MARGIN = 5;
    private static final float[] COLUMN_WIDTHS = new float[]{60, 200, 150, 100};
    private static final float TABLE_WIDTH = 510; // Somme des largeurs de colonnes

    public void generatePDF(List<Inscription> inscriptions, String fileName) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            int pageNumber = 1;

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // En-tête du document
            drawHeader(contentStream, page);

            // Titre du document
            drawTitle(contentStream);

            // Table header
            float yPosition = TABLE_TOP_Y;
            drawTableHeader(contentStream, yPosition);
            yPosition -= ROW_HEIGHT;

            // Contenu de la table
            for (Inscription inscription : inscriptions) {
                // Nouvelle page si nécessaire
                if (yPosition <= 100) {
                    drawFooter(contentStream, page, pageNumber);
                    contentStream.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    pageNumber++;
                    contentStream = new PDPageContentStream(document, page);

                    drawHeader(contentStream, page);
                    drawTableHeader(contentStream, TABLE_TOP_Y);
                    yPosition = TABLE_TOP_Y - ROW_HEIGHT;
                }

                drawTableRow(contentStream, inscription, yPosition);
                yPosition -= ROW_HEIGHT;
            }

            // Pied de page de la dernière page
            drawFooter(contentStream, page, pageNumber);
            contentStream.close();

            document.save(fileName);
        }
    }

    private void drawHeader(PDPageContentStream contentStream, PDPage page) throws Exception {
        float pageHeight = page.getMediaBox().getHeight();

        // Logo ou titre de l'école
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, pageHeight - MARGIN);
        contentStream.showText("Gestion École");
        contentStream.endText();

        // Date du rapport
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        String dateStr = "Date: " + LocalDateTime.now().format(dateFormatter);
        contentStream.beginText();
        contentStream.newLineAtOffset(page.getMediaBox().getWidth() - MARGIN - 150, pageHeight - MARGIN);
        contentStream.showText(dateStr);
        contentStream.endText();
    }

    private void drawTitle(PDPageContentStream contentStream) throws Exception {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, TABLE_TOP_Y + 50);
        contentStream.showText("Liste des Inscriptions");
        contentStream.endText();
    }

    private void drawTableHeader(PDPageContentStream contentStream, float y) throws Exception {
        // Fond gris pour l'en-tête
        contentStream.setNonStrokingColor(Color.LIGHT_GRAY);
        contentStream.addRect(MARGIN, y - 15, TABLE_WIDTH, ROW_HEIGHT);
        contentStream.fill();

        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        float xPosition = MARGIN;
        String[] headers = {"ID", "Étudiant", "Module", "Date"};

        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition + CELL_MARGIN, y - 10);
            contentStream.showText(headers[i]);
            contentStream.endText();
            xPosition += COLUMN_WIDTHS[i];
        }
    }

    private void drawTableRow(PDPageContentStream contentStream, Inscription inscription, float y) throws Exception {
        // Alternance de couleurs pour les lignes
        if ((int)((TABLE_TOP_Y - y) / ROW_HEIGHT) % 2 == 0) {
            contentStream.setNonStrokingColor(new Color(245, 245, 245));
            contentStream.addRect(MARGIN, y - 15, TABLE_WIDTH, ROW_HEIGHT);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);
        }

        contentStream.setFont(PDType1Font.HELVETICA, 10);
        float xPosition = MARGIN;

        // ID
        contentStream.beginText();
        contentStream.newLineAtOffset(xPosition + CELL_MARGIN, y - 10);
        contentStream.showText(String.valueOf(inscription.getId()));
        contentStream.endText();
        xPosition += COLUMN_WIDTHS[0];

        // Étudiant
        Etudiant etudiant = etudiantService.get(inscription.getEtudiantId());
        if (etudiant != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition + CELL_MARGIN, y - 10);
            contentStream.showText(etudiant.getNom() + " " + etudiant.getPrenom());
            contentStream.endText();
        }
        xPosition += COLUMN_WIDTHS[1];

        // Module
        Module module = moduleService.get(inscription.getModuleId());
        if (module != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition + CELL_MARGIN, y - 10);
            contentStream.showText(module.getNomModule());
            contentStream.endText();
        }
        xPosition += COLUMN_WIDTHS[2];

        // Date
        contentStream.beginText();
        contentStream.newLineAtOffset(xPosition + CELL_MARGIN, y - 10);
        contentStream.showText(inscription.getDateInscription().format(dateFormatter));
        contentStream.endText();
    }

    private void drawFooter(PDPageContentStream contentStream, PDPage page, int pageNumber) throws Exception {
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        float pageWidth = page.getMediaBox().getWidth();

        contentStream.beginText();
        contentStream.newLineAtOffset(pageWidth / 2 - 40, MARGIN);
        contentStream.showText("Page " + pageNumber);
        contentStream.endText();
    }
}