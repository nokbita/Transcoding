package priv.nokbita.app;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import priv.nokbita.entity.TransFile;
import priv.nokbita.util.TransUtil;

public class AppUI {

    private Menu menuFile;
    private Menu menuHelp;
    private Menu menuAbout;
    private MenuBar menuBar;

    private TableView<TransFile> tableView;
    private TableColumn<TransFile,String> srcFile;
    private TableColumn<TransFile,String> srcFileCoding;
    private TableColumn<TransFile,String> operate;
    private TableColumn<TransFile,String> tip;

    private ProgressBar progressBar;

    private VBox vBoxDest;
    private ComboBox<String> comboBoxDestCoding;
    private HBox hBoxDestDirectoryRegion;
    private ComboBox<String> comboBoxDestDirectory;
    private String StringSelectedDestDirectory;
    private Button buttonClear;
    private HBox hBoxStart;
    private Label labelFinishTips;
    private Button buttonOpenDestDirectory;
    private Button buttonCancel;
    private Button buttonContinue;
    private Button buttonStart;

    private TextField textFieldSrcFilePath;
    private Label labelFileInfo;
    private Label labelLog;

    private TextArea textAreaLog;
    private Button buttonRefresh;
    private Button buttonClearLog;

    private ScrollPane scrollPaneHelp;
    private ScrollPane scrollPaneAbout;

    public void start(Stage primaryStage) throws Exception {
        // 边框面板
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(800);
        borderPane.setPrefHeight(480);

        // top 菜单
        MenuBar menuBar = drawHBoxMenuBar(primaryStage, borderPane);
        borderPane.setTop(menuBar);
        borderPane.setMargin(menuBar,new Insets(10,10,10,10));

        // center 网格面板
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0,10,0,10));
        gridPane.prefWidthProperty().bind(borderPane.widthProperty());
        gridPane.prefHeightProperty().bind(borderPane.heightProperty());
        borderPane.setCenter(gridPane);

        // 表格
        TableView<TransFile> drawTable = drawTable(primaryStage);
        drawTable.prefWidthProperty().bind(gridPane.widthProperty());
        drawTable.prefHeightProperty().bind(gridPane.heightProperty());

        gridPane.add(drawTable,0,0);
//        gridPane.setMargin(drawTable,new Insets(0,10,10,10));

        // 进度条
        ProgressBar progressBar = drawProgressBar(primaryStage);
        progressBar.prefWidthProperty().bind(gridPane.prefWidthProperty());
        gridPane.add(progressBar,0,1);
//        gridPane.setMargin(progressBar,new Insets(0,10,10,10));

        // 开始按钮区域
        HBox drawStartRegion = drawStartRegion(primaryStage);
        drawStartRegion.prefWidthProperty().bind(gridPane.prefWidthProperty());
        gridPane.add(drawStartRegion,0,2);
//        gridPane.setMargin(drawStartRegion,new Insets(5,10,10,10));

        // bottom 日志区域
        VBox drawBottomRegion = drawStatusBar();
        borderPane.setBottom(drawBottomRegion);
        borderPane.setMargin(drawBottomRegion,new Insets(10,10,5,10));

        // 滚动面板 响应菜单
        scrollPaneHelp = drawScrollPane(borderPane,true);
        scrollPaneAbout = drawScrollPane(borderPane,false);

        // 场景，舞台
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 事件
        control(primaryStage);
    }

    public void control(Stage primaryStage) {
        // 以下是事件
        AppController appController = AppController.getInstance();

        appController.dragIntoFiles(tableView,labelFileInfo, hBoxStart, progressBar);

        // 菜单事件
        appController.menuFileAction(primaryStage, menuFile, tableView, labelFileInfo, hBoxStart, progressBar);
        appController.menuHelpAction((BorderPane) primaryStage.getScene().getRoot(), scrollPaneHelp, menuHelp);
        appController.menuAboutAction((BorderPane) primaryStage.getScene().getRoot(), scrollPaneAbout, menuAbout);

        // 更新状态栏文件路径 事件
        appController.updateStatusBarFilePath(tableView,textFieldSrcFilePath);

        // 双击打开表格中文件 事件
        appController.doubleClickOpenFile(tableView);

        // 清空列表事件
        appController.clearTableData(buttonClear,tableView,textFieldSrcFilePath,labelFileInfo,progressBar);

        // 选择目标目录
        appController.selectDestDirectory(primaryStage,comboBoxDestDirectory);

        // 开始转码
        appController.startTranscoding(menuFile,tableView,progressBar,
                vBoxDest,comboBoxDestCoding,comboBoxDestDirectory,
                hBoxStart, buttonClear,
                labelFinishTips, buttonOpenDestDirectory, buttonStart, buttonCancel, buttonContinue,
                textFieldSrcFilePath, labelFileInfo);

        // 日志新窗口
        Stage stageLog = drawStageLog(primaryStage);
        appController.checkLog(primaryStage, stageLog, labelLog, textAreaLog, buttonRefresh, buttonClearLog);
    }

    private MenuBar drawHBoxMenuBar(Stage stage, BorderPane borderPane) {

        menuFile = new Menu("文件");
        MenuItem menuItemOpenFile = new MenuItem("打开文件");
        MenuItem menuItemOpenDirectory = new MenuItem("打开文件夹");
        menuFile.getItems().addAll(menuItemOpenFile,menuItemOpenDirectory);


        menuHelp = new Menu("帮助");
        MenuItem menuItemIntro = new MenuItem("说明");
        menuHelp.getItems().addAll(menuItemIntro);


        menuAbout = new Menu("关于");
        MenuItem menuItemMe = new MenuItem("作者");
        menuAbout.getItems().addAll(menuItemMe);

        menuBar = new MenuBar();
        menuBar.setPadding(new Insets(0));
        menuBar.setStyle("-fx-background-color: rgba(219,219,219,0.4)");
        menuBar.getMenus().addAll(menuFile,menuHelp,menuAbout);

        return menuBar;
    }

    // 右侧滚动面板，配合菜单
    private ScrollPane drawScrollPane(BorderPane borderPane, boolean isHelp) {
        // 滚动面板
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(200);
        if (!isHelp) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }

        // 面板根节点盒子
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        if (isHelp) {
            vBox.setPrefWidth(160);
        } else {
            vBox.setPrefWidth(180);
        }
        vBox.setPadding(new Insets(10));
        vBox.setStyle("-fx-background-color: rgba(244,244,244,1)");

        // 顶部盒子
        HBox top = new HBox();
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0,5,0,5));
        top.prefWidthProperty().bind(vBox.prefWidthProperty());
        top.setStyle("-fx-background-color: rgba(219,219,219,0.4)");

        // 顶部关闭
        Label labelClose = new Label("关闭");
        top.getChildren().add(labelClose);

        // 简介
        Text textInfo = new Text();
        textInfo.setFont(new Font(12));
        textInfo.wrappingWidthProperty().bind(vBox.prefWidthProperty());

        // 策略，程序介绍
        Text textScheme = new Text();
        textScheme.setFont(new Font(12));
        textScheme.wrappingWidthProperty().bind(vBox.prefWidthProperty());

        // 支持，图片展示
        Image supportMe = new Image(getClass().getResourceAsStream("/images/supportMe.png"));
        ImageView imageView = new ImageView(supportMe);
        imageView.fitWidthProperty().bind(vBox.prefWidthProperty());

        // 其他文字
        Text text = new Text();
        text.setFont(new Font(12));
        text.wrappingWidthProperty().bind(vBox.prefWidthProperty());

        if (isHelp) {
            vBox.getChildren().addAll(top, textInfo, textScheme, text);
        } else {
            vBox.getChildren().addAll(top, textInfo, text, imageView);
        }
        // 将以上界面加入滚动面板
        scrollPane.setContent(vBox);

        if (isHelp) {
            // 受支持的编码格式
            StringBuilder coding = new StringBuilder();
            for (String s : TransUtil.getSupportedCoding()) {
                coding.append(s);
                coding.append(", ");
            }
            coding.delete(coding.length() - 2, coding.length());

            // 受支持的文件扩展名
            StringBuilder extension = new StringBuilder();
            for (String s : TransUtil.getSupportedExtension()) {
                extension.append(s);
                extension.append(", ");
            }
            extension.delete(extension.length() - 2, extension.length());

            textInfo.setText("说明：\n文件编码转换，支持对单个文件或批量文件进行转换。" + "\n\n" +
                    "支持的文件编码：" + "\n" +
                    coding.toString() + "." + "\n\n" +
                    "支持的文件扩展名：" + "\n" +
                    extension.toString() + "." + "\n\n" +
                    "特色：" + "\n" +
                    "支持拖拽导入；" + "\n" +
                    "支持单击列表文件查看文件路径；" + "\n" +
                    "支持双击列表文件“源文件”列打开文件；" + "\n" +
                    "支持查看最近一次转码日志；等等。");
            textScheme.setText("策略：\n本程序会将用户导入的文件或文件夹复制一份进行转码，并不会对原始文件产生破坏。");
            text.setText("注意：\n空文件、无扩展名、不支持的扩展名、不支持的文件编码默认复制，不进行转码。" +
                    "关于追加导入，虽然支持，但不建议这么做，因为有一些限制：" +
                    "如果追加导入同名文件，转码后，后导入的文件将覆盖前导入的文件；" +
                    "如果追加导入的是不同根目录下的文件夹，程序将不能正确转码。" +
                    "综上所述，该软件的最佳使用方法有两种：" +
                    "1.导入文件夹，然后转码（不追加导入）；2.导入文件，然后转码（可选的追加导入不同名文件）。\n\n" +
                    "更新预告：" + "\n" +
                    "1.无扩展名转码的支持；" + "\n" +
                    "2.优化追加方案；" + "\n" +
                    "3.优化软件性能；" + "\n" +
                    "4.……");
        } else {
            textInfo.setText("软件名：\nTranscoding v0.7.1" + "\n\n" +
                    "作者：\nnokbita" + "\n\n" +
                    "反馈：\n2101807119@qq.com" + "\n\n" +
                    "QQ交流群：\n1036865424");
            text.setText("\n" + "支持该项目：");
        }

        // 单击关闭
        labelClose.setOnMouseClicked(event -> {
            borderPane.setRight(null);
        });

        // close hover特效
        labelClose.setOnMouseEntered(event -> {
            labelClose.setTextFill(Color.RED);
        });
        labelClose.setOnMouseExited(event -> {
            labelClose.setTextFill(Color.BLACK);
        });

        return scrollPane;
    }


    private TableView<TransFile> drawTable(Stage stage) {
        // 演示数据
//        ObservableList<TransFile> data = FXCollections.observableArrayList(
//                new TransFile(new File("E:\\nokbi\\Desktop\\transcoding_out\\一级目录"),"UTF-8","复制","无"),
//                new TransFile(new File("E:\\nokbi\\Desktop\\transcoding_out\\一级目录"),"UTF-8","复制","无"),
//                new TransFile(new File("E:\\nokbi\\Desktop\\transcoding_out\\一级目录\\简繁体英文utf6le.txt"),"UTF-8","复制","无")
//        );

        tableView = new TableView<>();
        tableView.setPlaceholder(new Text("将文件拖拽到这里"));
        tableView.setEditable(true);
        tableView.setPrefHeight(300);

        // 列标题
        srcFile = new TableColumn<>("源文件");
        srcFileCoding = new TableColumn<>("原始编码");
        operate = new TableColumn<>("预操作");
        tip = new TableColumn<>("提示");
        srcFile.setPrefWidth(200);
        srcFileCoding.setPrefWidth(100);
        operate.setPrefWidth(120);
        tip.setPrefWidth(120);

        // 将表格数据模型的属性和列标题绑定
        srcFile.setCellValueFactory(new PropertyValueFactory<TransFile,String>("srcFileName"));
        srcFileCoding.setCellValueFactory(new PropertyValueFactory<TransFile,String>("srcFileCoding"));
        operate.setCellValueFactory(new PropertyValueFactory<TransFile,String>("operate"));
        tip.setCellValueFactory(new PropertyValueFactory<TransFile,String>("tip"));

        // 添加列标题到表格中
        tableView.getColumns().addAll(srcFile,srcFileCoding,operate,tip);

        return tableView;
    }


    private ProgressBar drawProgressBar(Stage stage) {
        // 进度条
        progressBar = new ProgressBar(0);
        progressBar.setMinHeight(16);

        return progressBar;
    }


    private HBox drawStartRegion(Stage stage) {
        // 目标编码区
        Label destCoding = new Label("目标编码：");

        comboBoxDestCoding = new ComboBox<>();
        comboBoxDestCoding.setEditable(false);
        comboBoxDestCoding.getItems().addAll(TransUtil.getSupportedCoding());
        comboBoxDestCoding.setValue("UTF-8");

        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.BASELINE_LEFT);
        hBox1.getChildren().addAll(destCoding, comboBoxDestCoding);

        // 目标目录区
        Label destDirectory = new Label("目标目录：");

        //      组合框
        String defaultDestDirectory = TransUtil.getDefaultDestDirectory().getAbsolutePath();
        comboBoxDestDirectory = new ComboBox<>();
        comboBoxDestDirectory.setEditable(true);
        comboBoxDestDirectory.setValue(defaultDestDirectory);

        //      组合框中的仿制按钮
        StringSelectedDestDirectory = "选择...";

        comboBoxDestDirectory.getItems().addAll(defaultDestDirectory, StringSelectedDestDirectory);

        //      目标目录区所在盒子
        hBoxDestDirectoryRegion = new HBox();
        hBoxDestDirectoryRegion.setAlignment(Pos.BASELINE_LEFT);
        hBoxDestDirectoryRegion.getChildren().addAll(destDirectory, comboBoxDestDirectory);

        // 将目标编码与目标目录区域组合
        vBoxDest = new VBox();
        vBoxDest.setSpacing(10);
        vBoxDest.getChildren().addAll(hBox1,hBoxDestDirectoryRegion);

        // 开始按钮区
        Font font12 = new Font(12);
        buttonClear = new Button("清空列表");
        buttonClear.setFont(font12);

        //      转换完成才显示的 提示标签
        labelFinishTips = new Label("转码中...");
        labelFinishTips.setFont(font12);
        labelFinishTips.setTextFill(Color.GREEN);

        //      转换完成后才显示的 打开目标目录按钮
        buttonOpenDestDirectory = new Button("打开");
        buttonOpenDestDirectory.setFont(font12);

        //      转换过程中才显示的 取消按钮
        buttonCancel = new Button("取消");
        buttonCancel.setFont(font12);

        //      转换过程中才显示的 继续按钮
        buttonContinue = new Button("继续");
        buttonContinue.setFont(font12);

        buttonStart = new Button("开始转换");
        buttonStart.setFont(font12);

        hBoxStart = new HBox();
        hBoxStart.setSpacing(10);
        hBoxStart.setAlignment(Pos.CENTER_RIGHT);
        hBoxStart.getChildren().addAll(buttonStart);

        VBox vBox2 = new VBox();
        vBox2.setSpacing(10);
        vBox2.setAlignment(Pos.CENTER_RIGHT);
        vBox2.getChildren().addAll(buttonClear, hBoxStart);

        Pane pane = new Pane();

        // 将以上各区组合
        HBox hBox3 = new HBox();
        hBox3.setHgrow(pane,Priority.ALWAYS);
        hBox3.getChildren().addAll(vBoxDest,pane,vBox2);

        return hBox3;
    }


    private VBox drawStatusBar() {

        Font font = new Font(10);

        textFieldSrcFilePath = new TextField("Empty");
        textFieldSrcFilePath.setDisable(true);
        textFieldSrcFilePath.setFont(font);
        textFieldSrcFilePath.setPrefWidth(500);
        textFieldSrcFilePath.setPadding(Insets.EMPTY);
        textFieldSrcFilePath.setBackground(new Background(new BackgroundFill(Color.web("#00000000"),CornerRadii.EMPTY,Insets.EMPTY)));

        labelFileInfo = new Label("文件：" + 0 + "    文件夹：" + 0);
        labelFileInfo.setFont(font);

        Pane pane = new Pane();


        labelLog = new Label("日志");
        labelLog.setFont(font);

        Separator separatorV1 = new Separator(Orientation.VERTICAL);
        Separator separatorV2 = new Separator(Orientation.VERTICAL);
        Separator separatorH = new Separator();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setSpacing(1);
        hBox.setHgrow(pane,Priority.ALWAYS);
        hBox.getChildren().addAll(textFieldSrcFilePath,separatorV1, labelFileInfo,pane,separatorV2,labelLog);

        VBox vBox = new VBox();
        vBox.setSpacing(1);
        vBox.getChildren().addAll(separatorH,hBox);

        return vBox;
    }


    private Stage drawStageLog(Stage primaryStage) {

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setPrefWidth(300);
        borderPane.prefHeightProperty().bind(primaryStage.getScene().heightProperty());

        // 文本域
        textAreaLog = new TextArea();
        textAreaLog.setEditable(false);
        textAreaLog.setWrapText(true);

        // 刷新
        buttonRefresh = new Button("刷新");
        buttonRefresh.setFont(new Font(12));

        // buttonClearLog
        buttonClearLog = new Button("清空");
        buttonClearLog.setFont(new Font(12));

        //
        HBox hBoxBottomLog = new HBox();
        hBoxBottomLog.setSpacing(10);
        hBoxBottomLog.setAlignment(Pos.BASELINE_RIGHT);
        hBoxBottomLog.getChildren().addAll(buttonRefresh,buttonClearLog);

        borderPane.setCenter(textAreaLog);
        borderPane.setMargin(hBoxBottomLog,new Insets(10,0,0,0));
        borderPane.setBottom(hBoxBottomLog);

        Scene scene = new Scene(borderPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(primaryStage);

        stage.setTitle("日志");
        Image icon16 = new Image(getClass().getResourceAsStream("/images/icons/Transcoding_icon16.png"));
        Image icon32 = new Image(getClass().getResourceAsStream("/images/icons/Transcoding_icon32.png"));
        Image icon64 = new Image(getClass().getResourceAsStream("/images/icons/Transcoding_icon64.png"));
        stage.getIcons().setAll(icon16,icon32,icon64);

        return stage;
    }

}
