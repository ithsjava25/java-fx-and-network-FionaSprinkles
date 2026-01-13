package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnectionImpl());

    public ListView<NtfyMessageDto> messageView;

    @FXML
    private Label versionLabel;

    @FXML
    private TextArea inputField;

    @FXML
    private void initialize() {
        inputField.textProperty().bindBidirectional(model.messageToSendProperty());
        if (versionLabel != null) {
            versionLabel.setText(model.getGreeting());
        }
        messageView.setItems(model.getMessages());

        //Display only text in messages.
        messageView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(NtfyMessageDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.message());
                }
            }
        });
    }

    public void sendMessage(ActionEvent actionEvent) {
        model.sendMessage();
    }

    public void attachFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose file");
        File file = chooser.showOpenDialog(messageView.getScene().getWindow());

        if (file != null) {
            model.sendFile(file);
        }
    }

}
