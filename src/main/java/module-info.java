module gestion.ecole {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires io.github.cdimascio.dotenv.java;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jbcrypt;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    requires org.apache.pdfbox;


    opens gestion.ecole to javafx.fxml;
    opens gestion.ecole.controllers to javafx.fxml;
    opens gestion.ecole.controllers.secretaire to javafx.fxml;
    opens gestion.ecole.controllers.admin to javafx.fxml;
    opens gestion.ecole.controllers.professeur to javafx.fxml;



    exports gestion.ecole;
    exports gestion.ecole.controllers;
    exports gestion.ecole.models;
    exports gestion.ecole.dao;
    exports gestion.ecole.controllers.admin;
    exports gestion.ecole.controllers.professeur;
    exports gestion.ecole.controllers.secretaire;
    exports gestion.ecole.services;

}
