module org.badvision.stanners {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires com.google.gson;
    requires lombok;
//    requires transitive org.mapstruct.processor;

    opens org.badvision.stanners to javafx.fxml;

    exports org.badvision.stanners;
    requires net.harawata.appdirs;

}