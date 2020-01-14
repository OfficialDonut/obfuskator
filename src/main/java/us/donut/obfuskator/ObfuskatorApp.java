package us.donut.obfuskator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ObfuskatorApp extends Application {

    private String version = "v" + ObfuskatorApp.class.getPackage().getSpecificationVersion();
    private BorderPane inputPane = new BorderPane();
    private BorderPane outputPane = new BorderPane();
    private TextArea outputTextArea = new TextArea();
    private SplitPane splitPane = new SplitPane(inputPane, outputPane);
    private Scene scene = new Scene(splitPane, 600, 400);
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Obfuskator " + version);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("/style.css");
        setupInputPane();
        setupOutputPane();
        primaryStage.show();
    }

    private void setupInputPane() {
        TextArea textArea = new TextArea();
        textArea.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });
        textArea.setOnDragDropped(e -> {
            try {
                File scriptFile = e.getDragboard().getFiles().get(0);
                textArea.setText(String.join("\n", Files.readAllLines(scriptFile.toPath(), StandardCharsets.UTF_8)));
                e.setDropCompleted(true);
            } catch (IOException ex) {
                displayException("Failed to read file", ex);
            }
        });

        Button selectFileButton = new Button("Select file");
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select script");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scripts", "*.sk"));
            File scriptFile = fileChooser.showOpenDialog(primaryStage);
            if (scriptFile != null) {
                try {
                    textArea.setText(String.join("\n", Files.readAllLines(scriptFile.toPath(), StandardCharsets.UTF_8)));
                } catch (IOException ex) {
                    displayException("Failed to read file", ex);
                }
            }
        });

        Button obfuscateButton = new Button("Obfuscate");
        obfuscateButton.setOnAction(e -> {
            String script = textArea.getText();
            outputTextArea.setText(Obfuscator.obfuscate(script));
        });

        inputPane.setCenter(textArea);
        inputPane.setBottom(new ToolBar(obfuscateButton, selectFileButton));
    }

    private void setupOutputPane() {
        Button copyButton = new Button("Copy text");
        copyButton.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(outputTextArea.getText());
            Clipboard.getSystemClipboard().setContent(content);
        });

        Button saveButton = new Button("Save file");
        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save script");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Script", "*.sk"));
            File scriptFile = fileChooser.showSaveDialog(primaryStage);
            if (scriptFile != null) {
                try (BufferedWriter writer = Files.newBufferedWriter(scriptFile.toPath(), StandardCharsets.UTF_8)) {
                    writer.write(outputTextArea.getText());
                } catch (IOException ex) {
                    displayException("Failed to read file", ex);
                }
            }
        });

        outputTextArea.setEditable(false);
        outputPane.setCenter(outputTextArea);
        outputPane.setBottom(new ToolBar(copyButton, saveButton));
    }

    public static void displayException(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, null, ButtonType.CLOSE);
        alert.getDialogPane().setContent(new VBox(5, new Text(message), new TextArea(ExceptionUtils.getStackTrace(e))));
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.showAndWait();
    }
}
