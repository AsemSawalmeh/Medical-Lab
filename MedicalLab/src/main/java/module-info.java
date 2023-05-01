module com.example.medicallab {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires itextpdf;


    opens com.example.medicallab to javafx.fxml;
    exports com.example.medicallab;
}