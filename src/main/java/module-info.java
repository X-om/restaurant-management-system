module com.rms.rms {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires java.sql;


    opens com.rms.rms to javafx.fxml;
    exports com.rms.rms;
    exports com.rms.rms.models;

}