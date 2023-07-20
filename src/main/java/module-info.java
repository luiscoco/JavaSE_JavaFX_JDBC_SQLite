module com.sample3 {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.sample3 to javafx.fxml;
    opens com.sample3.model to javafx.base; // Open the com.sample3.model package to javafx.base module

    exports com.sample3;
}
