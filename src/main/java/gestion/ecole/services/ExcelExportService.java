package gestion.ecole.services;

import gestion.ecole.models.Etudiant;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class ExcelExportService {

    public void generateExcel(ObservableList<Etudiant> students, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Liste des étudiants");

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Titre
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Liste des Étudiants");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Date d'export
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Date d'export: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dateCell.setCellStyle(dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

            // En-têtes
            Row headerRow = sheet.createRow(3);
            String[] headers = {"Matricule", "Nom", "Prénom", "Date de Naissance", "Email", "Promotion"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            int rowNum = 4;
            for (Etudiant student : students) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(student.getMatricule());
                row.createCell(1).setCellValue(student.getNom());
                row.createCell(2).setCellValue(student.getPrenom());

                Cell dateCell2 = row.createCell(3);
                dateCell2.setCellValue(student.getDateNaissance());
                dateCell2.setCellStyle(dateStyle);

                row.createCell(4).setCellValue(student.getEmail());
                row.createCell(5).setCellValue(student.getPromotion());

                // Appliquer le style aux cellules
                for (int i = 0; i < 6; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ajouter un pied de page
            Row footerRow = sheet.createRow(rowNum + 1);
            Cell footerCell = footerRow.createCell(0);
            footerCell.setCellValue("Nombre total d'étudiants: " + students.size());
            footerCell.setCellStyle(dataStyle);

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
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/MM/yyyy"));
        return style;
    }
}