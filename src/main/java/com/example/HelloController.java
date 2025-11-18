package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnectionImpl());

    public ListView<NtfyMessageDto> messageView;

    @FXML
    private Label versionLabel;

    @FXML
    private TextField inputField;

    @FXML
    private void initialize() {
        inputField.textProperty().bindBidirectional(model.messageToSendProperty());
        if (versionLabel != null) {
            versionLabel.setText(model.getGreeting());
        }
        messageView.setItems(model.getMessages());
        //todo fixa snygga meddelanden
        //messageView.setCellFactory();
    }

    public void sendMessage(ActionEvent actionEvent) {
        model.sendMessage();
    }
}
