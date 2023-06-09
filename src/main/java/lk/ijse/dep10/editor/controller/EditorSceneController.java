package lk.ijse.dep10.editor.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorSceneController {
    public TextArea txtEditor;
    public Label lblCount;
    public MenuItem mnSaveAs;
    public TextField txtReplace;
    public Button btnReplace;
    public Button btnReplaceAll;
    public Label lblResult;
    public TextField txtFind;
    public Button btnUp;
    public Button btnDown;
    public CheckBox chkCase;
    int count = 0;
    int wordCount;
    ArrayList<Integer> start = new ArrayList<>();
    ArrayList<Integer> end = new ArrayList<>();
    boolean isChecked = true;
    @FXML
    private MenuItem mnAbout;
    @FXML
    private MenuItem mnClose;
    @FXML
    private MenuItem mnNew;
    @FXML
    private MenuItem mnOpen;
    @FXML
    private MenuItem mnPrint;
    @FXML
    private MenuItem mnSave;
    private SimpleStringProperty title;
    private File savedFile;

    public void initialize() {

        btnUp.setDisable(true);
        txtFind.textProperty().addListener((observableValue, s, t1) -> {
            findResultCount();
        });
        txtEditor.textProperty().addListener((observableValue, s, t1) -> {
            findResultCount();
        });

        Platform.runLater(() -> {
            txtEditor.getScene().getWindow().setOnCloseRequest(windowEvent -> {
                mnClose.fire();
            });
        });

        txtEditor.textProperty().addListener((observableValue, s, t1) -> {
            if (title.getValue().charAt(0) == '*') title.setValue(title.getValue());
            if (!(title.getValue().charAt(0) == '*')) title.setValue("*" + title.getValue());
            if (txtEditor.getText().isEmpty()) lblCount.setText("Word Count:0");
            else lblCount.setText("Word Count:" + txtEditor.getText().split(" ").length);
        });

    }

    private void findResultCount() {
        start.clear();
        end.clear();
        count = 0;
        wordCount = 0;
        if (txtFind.getText().isEmpty()) {
            lblResult.setText("0 Results");
            return;
        }
        String text = txtFind.getText();
        Pattern pattern;

        try {
            pattern = isChecked ? Pattern.compile(text) : Pattern.compile(text, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            return;
        }
        Matcher matcher = pattern.matcher(txtEditor.getText());

        while (matcher.find()) {
            wordCount++;
            txtEditor.selectRange(matcher.start(), matcher.end());
            start.add(matcher.start());
            end.add(matcher.end());
        }
        lblResult.setText(wordCount + " Results");

    }

    @FXML
    void mnNewOnAction(ActionEvent event) {

        if (!(title.getValue().charAt(0) == '*')) {
            txtEditor.clear();
            title.setValue("Untitled Document 1");
            savedFile = null;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Save changes to document " + "'" + title.getValue().substring(1) + "'" + " before opening new document?", ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("");
            alert.setHeaderText("Close before Saving !");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                alert.close();
            }
            if (result.get() == ButtonType.NO) {
                txtEditor.clear();
                title.setValue("Untitled Document 1");
                savedFile = null;
            }
            if (result.get() == ButtonType.YES) {
                mnSave.fire();
                txtEditor.clear();
                title.setValue("Untitled Document 1");
                savedFile = null;

            }
        }


    }

    @FXML
    void mnOpenOnAction(ActionEvent event) throws IOException {
        if (!(title.getValue().charAt(0) == '*')) {
            openFile();


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Save changes to document " + "'" + title.getValue().substring(1) + "'" + " before opening new document?", ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("");
            alert.setHeaderText("Close before Saving !");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                alert.close();
            }
            if (result.get() == ButtonType.NO) {
                openFile();
            }
            if (result.get() == ButtonType.YES) {
                mnSave.fire();
                openFile();


            }
        }

    }

    private void openFile() throws IOException {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open a Text File");
        File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());
        if (file == null) return;
        savedFile = file;
        var fileInputStream = new FileInputStream(file);
        byte[] bytes = fileInputStream.readAllBytes();
        fileInputStream.close();

        txtEditor.setText(new String(bytes));
        title.setValue(file.getName() + " (" + file.getPath() + ")");
    }

    @FXML
    void mnSaveOnAction(ActionEvent event) throws IOException {
        if (savedFile != null) {
            var fileOutputStream = new FileOutputStream(savedFile);
            byte[] bytes = txtEditor.getText().getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            title.setValue(savedFile.getName() + " (" + savedFile.getPath() + ")");


        } else {
            var fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());
            if (file == null) return;
            savedFile = file;
            var fileOutputStream = new FileOutputStream(file);
            byte[] bytes = txtEditor.getText().getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            title.setValue(file.getName() + " (" + file.getPath() + ")");

        }


    }

    @FXML
    void mnAboutOnAction(ActionEvent event) throws IOException {
        AnchorPane root = new FXMLLoader(getClass().getResource("/view/AboutScene.fxml")).load();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(txtEditor.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();

    }
    @FXML
    void mnCloseOnAction(ActionEvent event) {
        if (!(title.getValue().charAt(0) == '*')) {
            Platform.exit();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Save changes to document " + "'" + title.getValue().substring(1) + "'" + " before closing?", ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("");
            alert.setHeaderText("Close before Saving !");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                alert.close();
            }
            if (result.get() == ButtonType.NO) {
                Platform.exit();
            }
            if (result.get() == ButtonType.YES) {
                mnSave.fire();
                Platform.exit();
            }
        }


    }

    public void rootOnDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
    }

    public void rootOnDragDropped(DragEvent dragEvent) throws IOException {
        if (!(title.getValue().charAt(0) == '*')) {
            File droppedFile = dragEvent.getDragboard().getFiles().get(0);
            savedFile = droppedFile;
            var fileInputStream = new FileInputStream(droppedFile);
            byte[] bytes = fileInputStream.readAllBytes();
            fileInputStream.close();

            txtEditor.setText(new String(bytes));
            title.setValue(droppedFile.getName() + " (" + droppedFile.getPath() + ")");


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Save changes to document " + "'" + title.getValue().substring(1) + "'" + " before opening new document?", ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("");
            alert.setHeaderText("Close before Saving !");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                alert.close();
            }
            if (result.get() == ButtonType.NO) {
                File droppedFile = dragEvent.getDragboard().getFiles().get(0);
                savedFile = droppedFile;
                var fileInputStream = new FileInputStream(droppedFile);
                byte[] bytes = fileInputStream.readAllBytes();
                fileInputStream.close();

                txtEditor.setText(new String(bytes));
                title.setValue(droppedFile.getName() + " (" + droppedFile.getPath() + ")");
            }
            if (result.get() == ButtonType.YES) {
                mnSave.fire();
                File droppedFile = dragEvent.getDragboard().getFiles().get(0);
                savedFile = droppedFile;
                var fileInputStream = new FileInputStream(droppedFile);
                byte[] bytes = fileInputStream.readAllBytes();
                fileInputStream.close();

                txtEditor.setText(new String(bytes));
                title.setValue(droppedFile.getName() + " (" + droppedFile.getPath() + ")");


            }
        }

    }

    public void initData(SimpleStringProperty input) {
        title = input;
    }

    public void mnSaveAsOnAction(ActionEvent actionEvent) throws IOException {
        var fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());
        if (file == null) return;
        savedFile = file;
        var fileOutputStream = new FileOutputStream(file);
        byte[] bytes = txtEditor.getText().getBytes();
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        title.setValue(file.getName() + " (" + file.getPath() + ")");
    }

    public void btnUpOnAction(ActionEvent actionEvent) {
        if (txtFind.getText().isEmpty()) return;
        count--;
        if (count < 0) {
            count = wordCount;
            return;
        }
        txtEditor.selectRange(start.get(count), end.get(count));
        lblResult.setText(String.format("%d/%d", count + 1, wordCount));
    }

    public void btnDownOnAction(ActionEvent actionEvent) {
        if (txtFind.getText().isEmpty()) return;
        if (count == wordCount) {
            count = 0;
            return;
        }
        btnUp.setDisable(false);
        txtEditor.selectRange(start.get(count), end.get(count));
        lblResult.setText(String.format("%d/%d", count + 1, wordCount));
        count++;
    }

    public void chkCaseOnAction(ActionEvent actionEvent) {
        isChecked = chkCase.isSelected();
        findResultCount();


    }

    public void btnReplaceOnAction(ActionEvent actionEvent) {
        System.out.println(count);
        if (txtReplace.getText().isEmpty()) return;
        txtEditor.setText(txtEditor.getText().substring(0, start.get(count - 1)) + txtReplace.getText() + txtEditor.getText().substring(end.get(count - 1), txtEditor.getLength()));

    }

    public void btnReplaceAllOnAction(ActionEvent actionEvent) {
        if (txtReplace.getText().isEmpty()) return;
        txtEditor.setText(txtEditor.getText().replace(txtEditor.getText().substring(start.get(count), end.get(count)), txtReplace.getText()));
        txtFind.selectAll();
        txtReplace.selectAll();
    }
}
