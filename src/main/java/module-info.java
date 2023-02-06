module com.example.thirdprojectphasetwo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires lombok;
    requires com.google.common;

    opens com.example.thirdprojectphasetwo to javafx.fxml;
    exports com.example.thirdprojectphasetwo;
}