module gestion.ecole {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires io.github.cdimascio.dotenv.java;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jbcrypt;

    requires org.apache.poi.poi;        // Ajoutez cette ligne
    requires org.apache.poi.ooxml;      // Ajoutez cette ligne
    requires java.desktop;
    requires pdfbox.app;              // Ajoutez cette ligne

    // Ouverture pour injection FXML
    opens gestion.ecole to javafx.fxml;
    opens gestion.ecole.controllers to javafx.fxml;
    opens gestion.ecole.controllers.secretaire to javafx.fxml;
    opens gestion.ecole.controllers.admin to javafx.fxml;
    opens gestion.ecole.controllers.professeur to javafx.fxml;


    // Exportation des packages pour d'autres modules
    exports gestion.ecole;
    exports gestion.ecole.controllers;
    exports gestion.ecole.models;
    exports gestion.ecole.dao;
    exports gestion.ecole.controllers.admin;
    exports gestion.ecole.controllers.professeur;
    exports gestion.ecole.controllers.secretaire;
    exports gestion.ecole.services;     // Ajoutez cette ligne
}
