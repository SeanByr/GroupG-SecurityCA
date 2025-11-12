module com.example.groupgsecurityca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires java.desktop;

    opens com.example.groupgsecurityca to javafx.fxml;
    exports com.example.groupgsecurityca.Server;
    opens com.example.groupgsecurityca.Server to javafx.fxml;
    exports com.example.groupgsecurityca.Client;
    opens com.example.groupgsecurityca.Client to javafx.fxml;
    exports com.example.groupgsecurityca.Controllers;
    opens com.example.groupgsecurityca.Controllers to javafx.fxml;
}