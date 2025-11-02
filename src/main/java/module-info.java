module com.example.calcoringinterface {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.calcoringinterface to javafx.fxml;
    exports com.example.calcoringinterface;
}