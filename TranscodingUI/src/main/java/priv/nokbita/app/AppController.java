package priv.nokbita.app;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import priv.nokbita.entity.TransFile;
import priv.nokbita.service.TransFileService;
import priv.nokbita.util.TransUtil;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AppController {

    private static volatile AppController appController = null;
    private final TransFileService transFileService = TransFileService.getInstance();
    private static Task task;
    private static File destDirectory;

    private AppController() { }

    public static AppController getInstance() {
        if (appController == null) {
            synchronized (AppController.class) {
                if (appController == null) {
                    appController = new AppController();
                }
            }
        }
        return appController;
    }


    // 拖入文件
    public void dragIntoFiles(TableView<TransFile> tableView, Label labelFileInfo, HBox hBoxStart, ProgressBar progressBar) {

        Node placeholder = tableView.getPlaceholder();
        Text text = new Text("松手放入");
        text.setFill(Color.GREEN);

        tableView.setOnDragEntered(event -> {

            tableView.setPlaceholder(text);
        });

        tableView.setOnDragExited(event -> {
            tableView.setPlaceholder(placeholder);
        });

        tableView.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
        });

        tableView.setOnDragDropped(event -> {
            progressBar.setProgress(0);

            if (hBoxStart != null && hBoxStart.getChildren().size() == 3) {
                hBoxStart.getChildren().remove(0,2);
            }

            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                List<File> files = dragboard.getFiles();
                ObservableList<TransFile> observableList = transFileService.getObservableList(files);

                tableView.getItems().setAll(observableList);
                updateStatusBarFilesInfo(tableView,labelFileInfo);
            }
        });

    }

    // 文件菜单事件
    public void menuFileAction(Stage stage, Menu menuFile, TableView<TransFile> tableView, Label labelFileInfo, HBox hBoxStart, ProgressBar progressBar) {

        MenuItem menuItemOpenFile = menuFile.getItems().get(0);
        MenuItem menuItemOpenDirectory = menuFile.getItems().get(1);

        File systemDesktopDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

        menuItemOpenFile.setOnAction(event -> {
            progressBar.setProgress(0);

            if (hBoxStart != null && hBoxStart.getChildren().size() == 3) {
                hBoxStart.getChildren().remove(0,2);
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("打开文件");
            fileChooser.setInitialDirectory(systemDesktopDirectory);

            Collection<FileChooser.ExtensionFilter> extensionFilters = TransUtil.getExtensionFilter();
            fileChooser.getExtensionFilters().addAll(extensionFilters);

            List<File> files = fileChooser.showOpenMultipleDialog(stage);
            if (files != null && !files.isEmpty()) {
                ObservableList<TransFile> observableList = transFileService.getObservableList(files);

                tableView.getItems().setAll(observableList);
                updateStatusBarFilesInfo(tableView,labelFileInfo);
            }

        });

        menuItemOpenDirectory.setOnAction(event -> {
            progressBar.setProgress(0);

            if (hBoxStart != null && hBoxStart.getChildren().size() == 3) {
                hBoxStart.getChildren().remove(0,2);
            }

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("打开文件夹");
            directoryChooser.setInitialDirectory(systemDesktopDirectory);

            File fileDirectory = directoryChooser.showDialog(stage);
            if (fileDirectory != null) {
                ObservableList<TransFile> observableList = transFileService.getObservableList(fileDirectory);

                tableView.getItems().setAll(observableList);
                updateStatusBarFilesInfo(tableView,labelFileInfo);
            }

        });

    }

    // 内部方法，更新状态栏文件信息
    private void updateStatusBarFilesInfo(TableView<TransFile> tableView, Label labelFileInfo) {
        int fileNum = 0;
        int directory = 0;

        for (TransFile tf : tableView.getItems()) {
            if (tf.getSrcFile().isFile()) {
                fileNum++;
            } else {
                directory++;
            }
        }

        labelFileInfo.setText("文件：" + fileNum + "    文件夹：" + directory);
    }

    public void menuHelpAction(BorderPane borderPane, ScrollPane scrollPaneHelp, Menu menuHelp) {
        // 打开右侧滚动面板
        menuHelp.getItems().get(0).setOnAction(event -> {
            borderPane.setRight(scrollPaneHelp);
        });
    }

    public void menuAboutAction(BorderPane borderPane, ScrollPane scrollPaneAbout, Menu menuAbout) {
        // 打开右侧滚动面板
        menuAbout.getItems().get(0).setOnAction(event -> {
            borderPane.setRight(scrollPaneAbout);
        });
    }

    // 更新状态栏文件路径
    public void updateStatusBarFilePath(TableView<TransFile> tableView, TextField textFieldSrcFilePath) {

        ReadOnlyObjectProperty<TransFile> transFileReadOnlyObjectProperty = tableView.getSelectionModel().selectedItemProperty();

        if (transFileReadOnlyObjectProperty == null) {
            return;
        }

        transFileReadOnlyObjectProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textFieldSrcFilePath.setDisable(false);
                textFieldSrcFilePath.setText(newValue.getSrcFile().getAbsolutePath());
            }

        });
    }

    // 双击打开文件
    public void doubleClickOpenFile(TableView<TransFile> tableView) {
        tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            if (tableView.getItems() == null || tableView.getItems().size() <= 0) {
                return;
            }

            if (tableView.getFocusModel().getFocusedCell().getColumn() != 0) {
                return;
            }

            if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                TableView.TableViewSelectionModel<TransFile> selectionModel = tableView.getSelectionModel();
                if (selectionModel.getSelectedItem() == null) {
                    return;
                }
                File srcFile = selectionModel.getSelectedItem().getSrcFile();

                try {
                    Desktop.getDesktop().open(srcFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    // 清空列表
    public void clearTableData(Button buttonClear, TableView<TransFile> tableView, TextField textFieldSrcFilePath, Label labelFileInfo, ProgressBar progressBar) {
        buttonClear.setOnAction(event -> {
            progressBar.setProgress(0);

            tableView.getItems().clear();
            textFieldSrcFilePath.setText("Empty");
            textFieldSrcFilePath.setDisable(true);
            labelFileInfo.setText("文件：" + 0 + "    文件夹：" + 0);
            transFileService.clearStaticTransFiles();
        });
    }

    public void selectDestDirectory(Stage stage, ComboBox<String> comboBoxDestDirectory) {

        ReadOnlyObjectProperty<String> stringReadOnlyObjectProperty = comboBoxDestDirectory.getSelectionModel().selectedItemProperty();
        ObservableList<String> items = comboBoxDestDirectory.getItems();
        String lastItem = items.get(items.size() - 1);

        stringReadOnlyObjectProperty.addListener((observable, oldValue, newValue) -> {

            if (newValue.equals(lastItem)) {
                comboBoxDestDirectory.setValue(items.get(0));

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("选择文件夹");
                directoryChooser.setInitialDirectory(TransUtil.getSystemDeskTopDirectory());

                File fileDirectory = directoryChooser.showDialog(stage);
                if (fileDirectory != null && fileDirectory.exists()) {
                    if (Objects.requireNonNull(fileDirectory.list()).length != 0) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("警告");
                        alert.setHeaderText("这是一个非空文件夹！");
                        alert.setContentText("程序可能会覆盖文件夹内的文件，为了安全起见，请您选择一个空的文件夹。\n\n本次操作将被忽略。");
                        alert.showAndWait();
                    } else {
                        String userSelectedDirectory = fileDirectory.getAbsolutePath();

                        boolean b = false;
                        for (String s : items) {
                            if (s.equals(userSelectedDirectory)) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) {
                            items.remove(lastItem);
                            items.addAll(userSelectedDirectory,lastItem);
                            comboBoxDestDirectory.setValue(userSelectedDirectory);
                        }
                    }
                }
            }
        });
    }

    public void startTranscoding(Menu menuFile, TableView<TransFile> tableView, ProgressBar progressBar,
                                 VBox vBoxDest, ComboBox<String> comboBoxDestCoding, ComboBox<String> comboBoxDestDirectory,
                                 HBox hBoxStart, Button buttonClear,
                                 Label labelFinishTips,Button buttonOpenDestDirectory, Button buttonStart, Button buttonCancel, Button buttonContinue,
                                 TextField textFieldSrcFilePath, Label labelFileInfo) {

        buttonStart.setOnAction(event -> {
            progressBar.setProgress(0);

            if (hBoxStart.getChildren().size() == 3) {
                hBoxStart.getChildren().removeAll(labelFinishTips, buttonOpenDestDirectory);
            }

            // 表格为空
            if (tableView.getItems().size() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("没有文件");
                alert.setContentText("没有文件可供转码，您应该先在菜单栏点击“文件>打开文件/打开文件夹”，导入文件后开始转码。" +
                        "\n\n本次操作将被忽略。");
                alert.showAndWait();
                return;
            }

            // 禁用表格、目标编码组合框、目标目录组合框、清除列表按钮
            menuFile.setDisable(true);
            tableView.setDisable(true);
            vBoxDest.setDisable(true);
            buttonClear.setDisable(true);

            // 目标编码
            String destCoding = comboBoxDestCoding.getSelectionModel().getSelectedItem();

            // 目标目录
            if (comboBoxDestDirectory.getSelectionModel().getSelectedIndex() == comboBoxDestDirectory.getItems().size() - 1) {
                return;
            }
            String selectedItem = comboBoxDestDirectory.getValue();
            destDirectory = new File(selectedItem);

            // 生成目标目录
            if (!destDirectory.exists()) {
                try {
                    destDirectory.mkdirs();
                } catch (SecurityException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("目标目录不合法！");
                    alert.setContentText("根据指定路径无法创建目录，请重新选择目标目录。\n\n本次操作将被忽略。");
                    alert.showAndWait();

                    menuFile.setDisable(false);
                    tableView.setDisable(false);
                    vBoxDest.setDisable(false);
                    buttonClear.setDisable(false);
                    return;
                }
            }

            hBoxStart.getChildren().remove(buttonStart);
            hBoxStart.getChildren().addAll(labelFinishTips,buttonCancel);

            task = transFileService.startTranscoding(destCoding, destDirectory);
            progressBar.progressProperty().bind(task.progressProperty());
            labelFinishTips.textProperty().bind(task.messageProperty());
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            task.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && (Boolean) newValue) {
                    hBoxStart.getChildren().remove(buttonCancel);
                    hBoxStart.getChildren().add(1,buttonOpenDestDirectory);
                    hBoxStart.getChildren().add(2,buttonStart);

                    progressBar.progressProperty().unbind();

                    // 清空列表
                    tableView.getItems().clear();
                    textFieldSrcFilePath.setText("Empty");
                    textFieldSrcFilePath.setDisable(true);
                    labelFileInfo.setText("文件：" + 0 + "    文件夹：" + 0);
                    transFileService.clearStaticTransFiles();

                    menuFile.setDisable(false);
                    tableView.setDisable(false);
                    vBoxDest.setDisable(false);
                    buttonClear.setDisable(false);
                }
            });

        });

        buttonCancel.setOnAction(event -> {
            if(task != null) {
                task.cancel();
            }

            hBoxStart.getChildren().remove(buttonCancel);
            hBoxStart.getChildren().add(1,buttonOpenDestDirectory);
            hBoxStart.getChildren().add(2,buttonStart);

            progressBar.progressProperty().unbind();

            // 清空列表
            tableView.getItems().clear();
            textFieldSrcFilePath.setText("Empty");
            textFieldSrcFilePath.setDisable(true);
            labelFileInfo.setText("文件：" + 0 + "    文件夹：" + 0);
            transFileService.clearStaticTransFiles();

            menuFile.setDisable(false);
            tableView.setDisable(false);
            vBoxDest.setDisable(false);
            buttonClear.setDisable(false);
        });

        buttonOpenDestDirectory.setOnAction(event -> {
            try {
                if (destDirectory != null) {
                    Desktop.getDesktop().open(destDirectory);
                }

                progressBar.setProgress(0);

                hBoxStart.getChildren().removeAll(labelFinishTips,buttonOpenDestDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void checkLog(Stage primaryStage, Stage stageLog, Label labelLog, TextArea textAreaLog, Button buttonRefresh, Button buttonClearLog) {

        SimpleDoubleProperty xp = new SimpleDoubleProperty();
        SimpleDoubleProperty yp = new SimpleDoubleProperty();

        labelLog.setOnMouseClicked(event -> {
            if (stageLog.isShowing()) {
                stageLog.close();
                return;
            }

            stageLog.setX(primaryStage.getX()+primaryStage.getScene().getWidth());
            stageLog.setY(primaryStage.getY());

            // 生成日志
            String log = TransUtil.log(transFileService.getStaticTransFilesCopy());
            textAreaLog.setText(log);
            stageLog.show();

            // 绑定并监听窗口相对位置
            xp.bind(primaryStage.xProperty());
            yp.bind(primaryStage.yProperty());

            xp.addListener((observable, oldValue, newValue) -> {
                stageLog.setX(xp.getValue()+primaryStage.getScene().getWidth());
            });
            yp.addListener((observable, oldValue, newValue) -> {
                stageLog.setY(yp.getValue());
            });
        });

        buttonRefresh.setOnAction(event -> {
            String log = TransUtil.log(transFileService.getStaticTransFilesCopy());
            textAreaLog.setText(log);
            stageLog.show();
        });

        buttonClearLog.setOnAction(event -> {
            transFileService.clearStaticTransFilesCopy();
            String log = TransUtil.log(transFileService.getStaticTransFilesCopy());
            textAreaLog.setText(log);
        });

        ((BorderPane)stageLog.getScene().getRoot()).getCenter().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            xp.unbind();
            yp.unbind();
        });
    }

}
