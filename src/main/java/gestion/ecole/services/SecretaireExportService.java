package gestion.ecole.services;

import gestion.ecole.models.Etudiant;
import javafx.collections.ObservableList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SecretaireExportService {

    private static final float MARGIN = 50;
    private static final float CELL_MARGIN = 5;
    private static final float ROW_HEIGHT = 20;
    private static final float[] COLUMN_WIDTHS = {80, 100, 100, 180, 80};

    public void generatePDF(ObservableList<Etudiant> students, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                drawHeader(contentStream);

                float yPosition = page.getMediaBox().getHeight() - 100;

                // Date d'export
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText("Date d'export: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

                // Tableau
                yPosition -= 40;
                String[] headers = {"Matricule", "Nom", "Prénom", "Email", "Promotion"};
                drawTable(contentStream, yPosition, headers, students);

                drawFooter(contentStream, page, students.size());
            }

            document.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }

    private void drawHeader(PDPageContentStream contentStream) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.newLineAtOffset(MARGIN, 780);
        contentStream.showText("Liste des étudiants");
        contentStream.endText();
    }

    private void drawTable(PDPageContentStream contentStream, float y, String[] headers, ObservableList<Etudiant> students) throws IOException {
        float nextX = MARGIN;
        float nextY = y;

        // En-têtes
        contentStream.setLineWidth(1f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        for (int i = 0; i < headers.length; i++) {
            contentStream.addRect(nextX, nextY, COLUMN_WIDTHS[i], ROW_HEIGHT);
            contentStream.beginText();
            contentStream.newLineAtOffset(nextX + CELL_MARGIN, nextY + 5);
            contentStream.showText(headers[i]);
            contentStream.endText();
            nextX += COLUMN_WIDTHS[i];
        }

        // Données
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        for (Etudiant student : students) {
            nextY -= ROW_HEIGHT;
            nextX = MARGIN;

            String[] rowData = {
                    student.getMatricule(),
                    student.getNom(),
                    student.getPrenom(),
                    student.getEmail(),
                    student.getPromotion()
            };

            for (int i = 0; i < rowData.length; i++) {
                contentStream.addRect(nextX, nextY, COLUMN_WIDTHS[i], ROW_HEIGHT);
                contentStream.beginText();
                contentStream.newLineAtOffset(nextX + CELL_MARGIN, nextY + 5);
                contentStream.showText(rowData[i]);
                contentStream.endText();
                nextX += COLUMN_WIDTHS[i];
            }
        }
        contentStream.stroke();
    }

    private void drawFooter(PDPageContentStream contentStream, PDPage page, int totalStudents) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(MARGIN, 30);
        contentStream.showText("Nombre total d'étudiants: " + totalStudents);
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 8);  // Changé de HELVETICA_ITALIC à HELVETICA
        contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 200, 30);
        contentStream.showText("Généré le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        contentStream.endText();
    }

    public void generateCSV(ObservableList<Etudiant> students, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // En-tête du fichier
            writer.append("Liste des étudiants\n");
            writer.append("Date d'export: ").append(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

            // En-têtes des colonnes
            writer.append("Matricule,Nom,Prénom,Date de Naissance,Email,Promotion\n");

            // Données
            for (Etudiant student : students) {
                writer.append(String.format("%s,%s,%s,%s,%s,%s\n",
                        escapeCSV(student.getMatricule()),
                        escapeCSV(student.getNom()),
                        escapeCSV(student.getPrenom()),
                        student.getDateNaissance().toString(),
                        escapeCSV(student.getEmail()),
                        escapeCSV(student.getPromotion())
                ));
            }

            // Pied de page
            writer.append("\nNombre total d'étudiants: ").append(String.valueOf(students.size()));

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du CSV: " + e.getMessage());
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}