package gestion.ecole.services;

import gestion.ecole.models.Inscription;
import gestion.ecole.models.Etudiant;
import gestion.ecole.models.Module;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InscriptionSecretaireExportService {
    private final EtudiantService etudiantService = new EtudiantService();
    private final ModuleService moduleService = new ModuleService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void generateExcel(List<Inscription> inscriptions, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Liste des inscriptions");

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Titre du document
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Liste des Inscriptions");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            // Date d'export
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Date d'export: " + java.time.LocalDate.now().format(dateFormatter));
            dateCell.setCellStyle(dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

            // Ligne vide pour l'espacement
            sheet.createRow(2);

            // En-têtes des colonnes
            Row headerRow = sheet.createRow(3);
            String[] headers = {"ID", "Matricule", "Étudiant", "Module", "Date d'inscription"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            int rowNum = 4;
            for (Inscription inscription : inscriptions) {
                Row row = sheet.createRow(rowNum++);
                Etudiant etudiant = etudiantService.get(inscription.getEtudiantId());
                Module module = moduleService.get(inscription.getModuleId());

                // ID
                Cell idCell = row.createCell(0);
                idCell.setCellValue(inscription.getId());
                idCell.setCellStyle(dataStyle);

                // Matricule
                Cell matriculeCell = row.createCell(1);
                matriculeCell.setCellValue(etudiant.getMatricule());
                matriculeCell.setCellStyle(dataStyle);

                // Étudiant
                Cell etudiantCell = row.createCell(2);
                etudiantCell.setCellValue(etudiant.getNom() + " " + etudiant.getPrenom());
                etudiantCell.setCellStyle(dataStyle);

                // Module
                Cell moduleCell = row.createCell(3);
                moduleCell.setCellValue(module.getNomModule());
                moduleCell.setCellStyle(dataStyle);

                // Date d'inscription
                Cell inscriptionDateCell = row.createCell(4);
                inscriptionDateCell.setCellValue(inscription.getDateInscription().format(dateFormatter));
                inscriptionDateCell.setCellStyle(dateStyle);
            }

            // Ligne vide avant le résumé
            sheet.createRow(rowNum++);

            // Résumé
            Row summaryRow = sheet.createRow(rowNum);
            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue("Nombre total d'inscriptions: " + inscriptions.size());
            summaryCell.setCellStyle(dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));

            // Ajuster la largeur des colonnes
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sauvegarder le fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel: " + e.getMessage());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}