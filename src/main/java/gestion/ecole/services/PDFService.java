package gestion.ecole.services;

import gestion.ecole.models.Etudiant;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PDFService {

    public void generatePDF(List<Etudiant> students, String moduleName, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.setLeading(20f);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Etudiants du module: " + moduleName);
            contentStream.newLine();
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            for (Etudiant student : students) {
                contentStream.showText(student.getMatricule() + " - " + student.getNom() + " " + student.getPrenom());
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.close();

            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateCSV(List<Etudiant> students, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Matricule,Nom,Prenom,Date de Naissance,Email,Promotion\n");
            for (Etudiant student : students) {
                writer.append(student.getMatricule()).append(",")
                        .append(student.getNom()).append(",")
                        .append(student.getPrenom()).append(",")
                        .append(student.getDateNaissance().toString()).append(",")
                        .append(student.getEmail()).append(",")
                        .append(student.getPromotion()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
