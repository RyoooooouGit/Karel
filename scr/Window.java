package scr;

import java.util.*;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

public class Window extends Application {
    private double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    private double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    private Font font = Font.loadFont("file:fonts\\HYYaKuHeiJ.ttf", 15);
    private String fontOf11 = String.format("-fx-font: 11 \"%s\";", font.getFamily());
    private String fontOf12 = String.format("-fx-font: 12 \"%s\";", font.getFamily());
    private String fontOf13 = String.format("-fx-font: 13 \"%s\";", font.getFamily());
    private String fontOf15 = String.format("-fx-font: 15 \"%s\";", font.getFamily());

    private Save[] saves = { new Save(0), new Save(1), new Save(2), new Save(3) };
    private Save[] originSaves = { new Save(0), new Save(1), new Save(2), new Save(3) };
    private Save save = saves[0];
    private int mapIndex = 0;
    private boolean ifSaved = true, ifCustomed = false;

    private boolean isKeyPressedCtrl = false;

    private int nowChooseElement = 0, previewRobotFace = 0;
    private double previewElementWidth = 60;

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("file:graph\\Robot.png"));
        stage.setTitle("Karel Robot");
        stage.setResizable(false);
        stage.setX(screenWidth / 2 - 500);
        stage.setY(screenHeight / 2 - 400);

        // 初始界面
        Button customMapButton = new Button("创建关卡");
        customMapButton.setStyle("-fx-background-radius:5;");
        customMapButton.setPrefSize(150, 50);
        customMapButton.setFont(font);
        Button chooseMapButton = new Button("选择该关卡");
        chooseMapButton.setStyle("-fx-background-radius:5;");
        chooseMapButton.setPrefSize(150, 50);
        chooseMapButton.setFont(font);
        ComboBox<String> mapChoiceComboBox = new ComboBox<>(refreshSaveList());
        mapChoiceComboBox.getSelectionModel().selectFirst();
        mapChoiceComboBox.setStyle("-fx-background-radius:5;" + fontOf15);
        mapChoiceComboBox.setPrefSize(310, 50);
        ImageView robotHi = new ImageView();
        robotHi.setImage(new Image("file:graph\\Robot.png"));
        robotHi.setFitHeight(300);
        robotHi.setFitWidth(300);

        HBox startHBox = new HBox(10, customMapButton, chooseMapButton);
        startHBox.setAlignment(Pos.CENTER);
        VBox startVBox = new VBox(10, robotHi, mapChoiceComboBox, startHBox);
        startVBox.setAlignment(Pos.CENTER);
        BorderPane startBorderPane = new BorderPane();
        startBorderPane.setCenter(startVBox);

        Scene startScene = new Scene(startBorderPane, 1000, 800);
        stage.setScene(startScene);
        stage.show();

        // 地图界面
        Label codeTextLabel = new Label("代码区域");
        codeTextLabel.setFont(font);
        TextArea codeText = new TextArea();
        codeText.setPrefSize(250, 600);
        VBox codeTextVBox = new VBox(codeTextLabel, codeText);
        codeTextVBox.setAlignment(Pos.CENTER);

        Button exitButton = new Button("退出关卡");
        exitButton.setPrefSize(120, 60);
        exitButton.setFont(font);
        Button runCodeButton = new Button("运行代码");
        runCodeButton.setPrefSize(120, 60);
        runCodeButton.setFont(font);
        Button restartButton = new Button("重新开始");
        restartButton.setPrefSize(120, 60);
        restartButton.setFont(font);
        HBox exitAndRunHBox = new HBox(10, exitButton, runCodeButton);
        exitAndRunHBox.setAlignment(Pos.CENTER);

        VBox leftVBox = new VBox(20, codeTextVBox, exitAndRunHBox);
        leftVBox.setAlignment(Pos.CENTER);

        GridPane[] mapGridPane = { save.map.output() };
        VBox mapVBox = new VBox();
        mapVBox.setAlignment(Pos.CENTER);
        HBox mapHBox = new HBox(mapVBox);
        mapHBox.setAlignment(Pos.CENTER);
        BorderPane mapBorderPane = new BorderPane();
        mapBorderPane.setPrefSize(620, 510);
        mapBorderPane.setCenter(mapHBox);

        Label wrongTextLabel = new Label("错误提示");
        wrongTextLabel.setFont(font);
        TextArea wrongText = new TextArea();
        wrongText.setEditable(false);
        wrongText.setPrefSize(300, 150);
        VBox wrongTextVBox = new VBox(wrongTextLabel, wrongText);
        wrongTextVBox.setAlignment(Pos.CENTER);

        Label logTextLabel = new Label("日志消息");
        logTextLabel.setFont(font);
        TextArea logText = new TextArea();
        logText.setEditable(false);
        logText.setPrefSize(300, 150);
        VBox logTextVBox = new VBox(logTextLabel, logText);
        logTextVBox.setAlignment(Pos.CENTER);

        HBox wrongAndLogHBox = new HBox(20, wrongTextVBox, logTextVBox);
        wrongAndLogHBox.setAlignment(Pos.CENTER);

        VBox rightVBox = new VBox(20, mapBorderPane, wrongAndLogHBox);
        rightVBox.setAlignment(Pos.CENTER);

        HBox mainHBox = new HBox(50, leftVBox, rightVBox);
        mainHBox.setAlignment(Pos.CENTER);

        Menu changeMenu = new Menu("切换关卡");
        for (String saveMenuItemName : refreshSaveList()) {
            MenuItem saveMenuItem = new MenuItem(saveMenuItemName);
            changeMenu.getItems().add(saveMenuItem);
        }
        MenuItem saveMenuItem = new MenuItem("存档");
        MenuItem readMenuItem = new MenuItem("读档");
        Menu saveMenu = new Menu("存/读档");
        saveMenu.getItems().addAll(saveMenuItem, readMenuItem);
        MenuItem wayMenuItem = new MenuItem("游戏玩法");
        MenuItem aimMenuItem = new MenuItem("游戏目标");
        MenuItem codesMenuItem = new MenuItem("指令列表");
        MenuItem easyCodesMenuItem = new MenuItem("快捷键列表");
        MenuItem allMenuItem = new MenuItem("全部帮助");
        Menu helpMenu = new Menu("帮助");
        helpMenu.getItems().addAll(wayMenuItem, aimMenuItem, codesMenuItem, easyCodesMenuItem, allMenuItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(changeMenu, saveMenu, helpMenu);
        menuBar.setStyle("-fx-background-color:#AAAAAA;" + fontOf12);

        VBox mainVBox = new VBox(20, menuBar, mainHBox);
        mainVBox.setAlignment(Pos.CENTER);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(mainVBox);

        Scene mainScene = new Scene(mainPane, 1000, 800);

        // 创建地图界面
        Label mapSizeLabel = new Label("请输入所需地图尺寸:");
        mapSizeLabel.setFont(font);
        Label mapLengthLabel = new Label("横向宽度");
        mapLengthLabel.setFont(font);
        TextField mapLengthTextField = new TextField();
        mapLengthTextField.setPrefSize(100, 20);
        HBox mapLengthHBox = new HBox(5, mapLengthLabel, mapLengthTextField);
        mapLengthHBox.setAlignment(Pos.CENTER);
        Label mapWidthLabel = new Label("纵向高度");
        mapWidthLabel.setFont(font);
        TextField mapWidthTextField = new TextField();
        mapWidthTextField.setPrefSize(100, 20);
        HBox mapWidthHBox = new HBox(5, mapWidthLabel, mapWidthTextField);
        mapWidthHBox.setAlignment(Pos.CENTER);
        HBox mapSizeInputHBox = new HBox(15, mapLengthHBox, mapWidthHBox);
        mapSizeInputHBox.setAlignment(Pos.CENTER);
        Button checkMapSizeButton = new Button("确定尺寸");
        checkMapSizeButton.setPrefSize(80, 30);
        checkMapSizeButton.setFont(font);
        HBox mapSizeHBox = new HBox(20, mapSizeLabel, mapSizeInputHBox, checkMapSizeButton);
        mapSizeHBox.setAlignment(Pos.CENTER);

        GridPane previewMapGridPane = new GridPane();
        previewMapGridPane.setHgap(0.3);
        previewMapGridPane.setVgap(0.3);
        previewMapGridPane.setStyle("-fx-background-color: #000000; -fx-border-color: #000000; -fx-border-width: 0.3;");
        HBox previewMapHBox = new HBox(previewMapGridPane);
        previewMapHBox.setAlignment(Pos.CENTER);
        VBox previewMapVBox = new VBox(previewMapHBox);
        previewMapVBox.setAlignment(Pos.CENTER);
        BorderPane previewMapBorderPane = new BorderPane();
        previewMapBorderPane.setCenter(previewMapVBox);
        previewMapBorderPane.setPrefSize(610, 610);
        VBox customMapVBox = new VBox(20, mapSizeHBox, previewMapBorderPane);
        customMapVBox.setAlignment(Pos.CENTER);

        Label chooseElementLabel = new Label("点击下方图标选择所需放置的元素\n当前放置：Karel机器人");
        chooseElementLabel.setFont(font);
        ImageView[] chooseRobotImageViews = {
                new ImageView(new Image("file:graph\\Robot_R.png")),
                new ImageView(new Image("file:graph\\Robot_U.png")),
                new ImageView(new Image("file:graph\\Robot_L.png")),
                new ImageView(new Image("file:graph\\Robot_D.png")) };
        Button[] chooseRobotButtons = { new Button(), new Button(), new Button(), new Button() };
        Label[] chooseRobotLabels = {
                new Label("Karel机器人\n方向(右键改变)：→"),
                new Label("Karel机器人\n方向(右键改变)：↑"),
                new Label("Karel机器人\n方向(右键改变)：←"),
                new Label("Karel机器人\n方向(右键改变)：↓") };
        HBox[] chooseRobotHBoxs = { new HBox(10), new HBox(10), new HBox(10), new HBox(10) };
        HBox chooseRobotHBox_2 = new HBox(chooseRobotHBoxs[0]);
        for (int i = 0; i < 4; i++) {
            chooseRobotImageViews[i].setFitHeight(60);
            chooseRobotImageViews[i].setFitWidth(60);
            chooseRobotButtons[i] = new Button();
            chooseRobotButtons[i].setGraphic(chooseRobotImageViews[i]);
            chooseRobotButtons[i].setStyle(
                    "-fx-background-color: transparent; -fx-padding: 0;"
                            + " -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 5, 0, 4, 4);");
            int index = i;
            chooseRobotButtons[i].setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    chooseRobotHBox_2.getChildren().setAll(chooseRobotHBoxs[(index + 1) % 4]);
                    previewRobotFace = (previewRobotFace + 1) % 4;
                }
                if (event.getButton() == MouseButton.PRIMARY) {
                    nowChooseElement = 0;
                    chooseElementLabel.setText("点击下方图标选择所需放置的元素\n当前放置：Karel机器人");
                }
            });
            chooseRobotLabels[i].setFont(font);
            chooseRobotHBoxs[i].getChildren().setAll(chooseRobotButtons[i], chooseRobotLabels[i]);
            chooseRobotHBoxs[i].setAlignment(Pos.CENTER);
        }
        chooseRobotHBox_2.setAlignment(Pos.TOP_LEFT);
        ImageView chooseRockImageView = new ImageView(new Image("file:graph\\Rock.png"));
        chooseRockImageView.setFitHeight(60);
        chooseRockImageView.setFitWidth(60);
        Button chooseRockButton = new Button();
        chooseRockButton.setGraphic(chooseRockImageView);
        chooseRockButton.setStyle(
                "-fx-background-color: transparent; -fx-padding: 0;"
                        + " -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 5, 0, 4, 4);");
        chooseRockButton.setOnAction(event -> {
            nowChooseElement = 1;
            chooseElementLabel.setText("点击下方图标选择所需放置的元素\n当前放置：石头");
        });
        Label chooseRockLabel = new Label("石头");
        chooseRockLabel.setFont(font);
        HBox chooseRockHBox = new HBox(10, chooseRockButton, chooseRockLabel);
        chooseRockHBox.setAlignment(Pos.CENTER);
        HBox chooseRockHBox_2 = new HBox(chooseRockHBox);
        chooseRockHBox_2.setAlignment(Pos.TOP_LEFT);
        ImageView chooseWallImageView = new ImageView(new Image("file:graph\\Wall.png"));
        chooseWallImageView.setFitHeight(60);
        chooseWallImageView.setFitWidth(60);
        Button chooseWallButton = new Button();
        chooseWallButton.setGraphic(chooseWallImageView);
        chooseWallButton.setStyle(
                "-fx-background-color: transparent; -fx-padding: 0;"
                        + " -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 5, 0, 4, 4);");
        chooseWallButton.setOnAction(event -> {
            nowChooseElement = 2;
            chooseElementLabel.setText("点击下方图标选择所需放置的元素\n当前放置：墙壁");
        });
        Label chooseWallLabel = new Label("墙壁");
        chooseWallLabel.setFont(font);
        HBox chooseWallHBox = new HBox(10, chooseWallButton, chooseWallLabel);
        chooseWallHBox.setAlignment(Pos.CENTER);
        HBox chooseWallHBox_2 = new HBox(chooseWallHBox);
        chooseWallHBox_2.setAlignment(Pos.TOP_LEFT);
        ImageView chooseTrapImageView = new ImageView(new Image("file:graph\\Trap.png"));
        chooseTrapImageView.setFitHeight(60);
        chooseTrapImageView.setFitWidth(60);
        Button chooseTrapButton = new Button();
        chooseTrapButton.setGraphic(chooseTrapImageView);
        chooseTrapButton.setStyle(
                "-fx-background-color: transparent; -fx-padding: 0;"
                        + " -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 5, 0, 4, 4);");
        chooseTrapButton.setOnAction(event -> {
            nowChooseElement = 3;
            chooseElementLabel.setText("点击下方图标选择所需放置的元素\n当前放置：陷阱");
        });
        Label chooseTrapLabel = new Label("陷阱");
        chooseTrapLabel.setFont(font);
        HBox chooseTrapHBox = new HBox(10, chooseTrapButton, chooseTrapLabel);
        chooseTrapHBox.setAlignment(Pos.CENTER);
        HBox chooseTrapHBox_2 = new HBox(chooseTrapHBox);
        chooseTrapHBox_2.setAlignment(Pos.TOP_LEFT);
        ImageView chooseGroundImageView = new ImageView(new Image("file:graph\\Ground.png"));
        chooseGroundImageView.setFitHeight(60);
        chooseGroundImageView.setFitWidth(60);
        Button chooseGroundButton = new Button();
        chooseGroundButton.setGraphic(chooseGroundImageView);
        chooseGroundButton.setStyle(
                "-fx-background-color: transparent; -fx-padding: 0;"
                        + " -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 5, 0, 4, 4);");
        chooseGroundButton.setOnAction(event -> {
            nowChooseElement = 4;
            chooseElementLabel.setText("点击下方图标选择所需放置的元素\n当前放置：清除当前元素");
        });
        Label chooseGroundLabel = new Label("清除当前元素");
        chooseGroundLabel.setFont(font);
        HBox chooseGroundHBox = new HBox(10, chooseGroundButton, chooseGroundLabel);
        chooseGroundHBox.setAlignment(Pos.CENTER);
        HBox chooseGroundHBox_2 = new HBox(chooseGroundHBox);
        chooseGroundHBox_2.setAlignment(Pos.TOP_LEFT);

        Button doneMapButton = new Button("完成");
        doneMapButton.setFont(font);
        doneMapButton.setPrefSize(110, 50);
        Button quitButton = new Button("退出");
        quitButton.setFont(font);
        quitButton.setPrefSize(110, 50);
        HBox doneAndQuitHBox = new HBox(30, doneMapButton, quitButton);
        VBox chooseElementVBox = new VBox(20, chooseElementLabel, chooseRobotHBox_2, chooseRockHBox_2,
                chooseWallHBox_2, chooseTrapHBox_2, chooseGroundHBox_2, doneAndQuitHBox);
        chooseElementVBox.setAlignment(Pos.CENTER);
        chooseElementVBox.setPrefWidth(250);

        HBox customMapHBox = new HBox(50, chooseElementVBox, customMapVBox);
        customMapHBox.setAlignment(Pos.CENTER);
        BorderPane customMapPane = new BorderPane();
        customMapPane.setCenter(customMapHBox);
        Scene customMapScene = new Scene(customMapPane, 1000, 800);

        // 帮助界面
        double gapBetweenFirst = 10, gapBetweenSecond = 5, gapBetweenThird = 3;
        Label helpWayTitleLabel = new Label("游戏玩法");
        helpWayTitleLabel.setStyle(fontOf15);
        Label helpRunWayTitleLabel = new Label("关于运行代码");
        helpRunWayTitleLabel.setStyle(fontOf13);
        Label helpRunWayLabel = new Label(
                "请在代码区域按格式输入指令(详见指令列表)，点\n击运行代码按钮，Karel机器人会跳过错误代码，\n根据正确的代码作出反应与行动，错误的代码会在\n错误提示中指出，游戏信息会在日志消息中显示");
        helpRunWayLabel.setStyle(fontOf11);
        VBox helpRunWayVBox = new VBox(gapBetweenSecond, helpRunWayTitleLabel, helpRunWayLabel);
        Label helpCustomWayTitleLabel = new Label("关于自定义地图");
        helpCustomWayTitleLabel.setStyle(fontOf13);
        Label helpCustomWayLabel_1 = new Label("本游戏支持玩家自行创建地图进行游玩\n");
        helpCustomWayLabel_1.setStyle(fontOf11);
        Label helpCustomWayLabel_2 = new Label("在创建关卡界面点击图标选中需放置的元素后，\n点击地图中的点位即可将该元素放置在相应的位置\n");
        helpCustomWayLabel_2.setStyle(fontOf11);
        Label helpCustomWayLabel_3 = new Label("注：地图中需要存在有且仅有一个Karel机器人和\n至少一块石头，其他不限");
        helpCustomWayLabel_3.setStyle(fontOf11);
        VBox helpCustomWayVBox = new VBox(gapBetweenSecond, helpCustomWayTitleLabel,
                helpCustomWayLabel_1, helpCustomWayLabel_2, helpCustomWayLabel_3);
        Label helpAimTitleLabel = new Label("游戏目标");
        helpAimTitleLabel.setStyle(fontOf15);
        Label helpAimLabel_1 = new Label("收集地图中除陷阱中石头以外的全部石头即可获得\n胜利，掉进未填平的陷阱则游戏失败");
        helpAimLabel_1.setStyle(fontOf11);
        Label helpAimLabel_2 = new Label("请通过捡起地上的石头并填平地上的陷阱来到达原\n本不可到达的地方\n");
        helpAimLabel_2.setStyle(fontOf11);
        Label helpAimLabel_3 = new Label("注：陷阱无需全部填平");
        helpAimLabel_3.setStyle(fontOf11);
        VBox helpAimVBox = new VBox(gapBetweenSecond, helpAimLabel_1, helpAimLabel_2, helpAimLabel_3);
        Label helpCodeTitleLabel = new Label("指令列表");
        helpCodeTitleLabel.setStyle(fontOf15);
        Label helpActionCodeTitleLabel = new Label("行动指令");
        helpActionCodeTitleLabel.setStyle(fontOf13);
        Label helpActionCodeLabel = new Label(
                "move()  移动一步\nmove(x)  移动x步\nturnLeft()  左转\npickRock()  捡起石头放入背包\nputRock()  放下石头填入陷阱\n");
        helpActionCodeLabel.setStyle(fontOf11);
        VBox helpActionCodeVBox = new VBox(gapBetweenSecond, helpActionCodeTitleLabel, helpActionCodeLabel);
        Label helpInformationCodeTitleLabel = new Label("信息指令");
        helpInformationCodeTitleLabel.setStyle(fontOf13);
        Label helpInformationCodeLabel = new Label("showInformation()\n显示地图信息\n(剩余石头数、背包中石头数、最近石头距离)\n");
        helpInformationCodeLabel.setStyle(fontOf11);
        VBox helpInformationCodeVBox = new VBox(gapBetweenSecond,
                helpInformationCodeTitleLabel, helpInformationCodeLabel);
        Label helpBooleanCodeTitleLabel = new Label("判断指令");
        helpBooleanCodeTitleLabel.setStyle(fontOf13);
        Label helpBooleanCodeLabel = new Label("noRockPresent()  前方一格是否没有石头\nnoRockInBag()  包中是否没有石头\n");
        helpBooleanCodeLabel.setStyle(fontOf11);
        VBox helpBooleanCodeVBox = new VBox(gapBetweenSecond, helpBooleanCodeTitleLabel, helpBooleanCodeLabel);
        Label helpBlockCodeTitleLabel = new Label("块指令");
        helpBlockCodeTitleLabel.setStyle(fontOf13);
        Label helpBlockCodeLabel = new Label(
                "xxxxx() {\n\txxx();\n\txxx();\n}\n自定义函数，之后可以通过xxxxx();来调用，\n自定义新函数后会覆盖前一个自定义函数\n");
        helpBlockCodeLabel.setStyle(fontOf11);
        Label helpBlockCodeLabel_2 = new Label(
                "if (xxx()) {\n\txxx();\n} else {\n\txxx();\n}\nif-else语句，括号里填入判断指令，\n若为真则执行第一段指令，否则执行第二段指令\n(else块可省略)\n");
        helpBlockCodeLabel_2.setStyle(fontOf11);
        VBox helpBlockCodeVBox = new VBox(gapBetweenThird, helpBlockCodeLabel, helpBlockCodeLabel_2);
        VBox helpBlockCodeVBoxWithTitle = new VBox(gapBetweenSecond, helpBlockCodeTitleLabel, helpBlockCodeVBox);
        Label helpInputWayTitleLabel = new Label("指令输入方式");
        helpInputWayTitleLabel.setStyle(fontOf13);
        Label helpInputWayLabel = new Label("单条指令输入需单独占一行且末尾加上\";\"，\n块指令输入须按上述格式，但\"{}\"内指令数无限制\n");
        helpInputWayLabel.setStyle(fontOf11);
        VBox helpInputWayVBox = new VBox(gapBetweenSecond, helpInputWayTitleLabel, helpInputWayLabel);
        Label helpEasyCodeTitleLabel = new Label("快捷键列表");
        helpEasyCodeTitleLabel.setStyle(fontOf15);
        Label helpEasyCodeLabel_1 = new Label("Ctrl+Enter 运行指令\nCtrl+S 存档\nCtrl+Z 读档\nCtrl+Q 退出本关卡\nCtrl+R 游戏结束后重新开始");
        helpEasyCodeLabel_1.setStyle(fontOf11);
        Label helpEasyCodeLabel_2 = new Label("注：以上快捷键仅在此界面生效，且按顺序按下的\n效果完全等同于点击画面中相应的按钮");
        helpEasyCodeLabel_2.setStyle(fontOf11);
        VBox helpEasyCodeVBox = new VBox(gapBetweenSecond, helpEasyCodeLabel_1, helpEasyCodeLabel_2);
        VBox helpVBox = new VBox(gapBetweenFirst);
        helpVBox.setPadding(new Insets(3));
        ScrollPane helpScrollPane = new ScrollPane(helpVBox);
        helpScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        helpScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        Scene helpScene = new Scene(helpScrollPane, 250, 800);
        Stage helpStage = new Stage();
        helpStage.getIcons().add(new Image("file:graph\\Robot.png"));
        helpStage.setTitle("Karel Robot Helper");
        helpStage.setScene(helpScene);
        helpStage.setResizable(false);
        helpStage.initOwner(stage);
        helpStage.setX(screenWidth / 2 - 760);
        helpStage.setY(screenHeight / 2 - 400);

        // 创建地图界面交互
        checkMapSizeButton.setOnAction(event -> {
            String lengthString = mapLengthTextField.getText();
            String widthString = mapWidthTextField.getText();
            if (lengthString.matches("\\d+") && widthString.matches("\\d+")) {
                int length = Integer.parseInt(lengthString);
                int width = Integer.parseInt(widthString);
                if (length > 0 && width > 0) {
                    previewMapGridPane.getChildren().clear();
                    saves[3].map = new Map(4);
                    saves[3].map.length = length;
                    saves[3].map.width = width;
                    saves[3].map.mapElementWidth = saves[3].map.getElementWidth();
                    previewElementWidth = saves[3].map.length > saves[3].map.width ? 600 / saves[3].map.length
                            : 600 / saves[3].map.width;
                    for (int i = 0; i < saves[3].map.length; i++) {
                        for (int j = 0; j < saves[3].map.width; j++) {
                            ImageView customMapElementImageView = new ImageView(new Image("file:graph\\Ground.png"));
                            customMapElementImageView.setFitHeight(previewElementWidth);
                            customMapElementImageView.setFitWidth(previewElementWidth);
                            previewMapGridPane.add(customMapElementImageView, i, j);
                        }
                    }
                    previewMapHBox.getChildren().setAll(previewMapGridPane);
                }
            }
        });
        doneMapButton.setOnAction(event -> {
            ifCustomed = true;
            boolean robotExist = false, extraRobot = false;
            ArrayList<Obstacle> wall = new ArrayList<>(), rock = new ArrayList<>(), trap = new ArrayList<>();
            for (Node node : previewMapGridPane.getChildren()) {
                if (node instanceof ImageView) {
                    int x = GridPane.getColumnIndex(node);
                    int y = GridPane.getRowIndex(node);
                    String nodeUrl = ((ImageView) node).getImage().getUrl();
                    if (nodeUrl.equals("file:graph/Ground.png")) {
                        ;
                    } else if (nodeUrl.equals("file:graph/Rock.png")) {
                        rock.add(new Rock(x, y));
                    } else if (nodeUrl.equals("file:graph/Wall.png")) {
                        wall.add(new Wall(x, y));
                    } else if (nodeUrl.equals("file:graph/Trap.png")) {
                        trap.add(new Trap(x, y));
                    } else if (!robotExist) {
                        saves[3].map.karel = new Robot(0, 0, 0);
                        String[] faceUrl = { "file:graph/Robot_R.png", "file:graph/Robot_U.png",
                                "file:graph/Robot_L.png", "file:graph/Robot_D.png" };
                        for (int k = 0; k < 4; k++) {
                            if (nodeUrl.equals(faceUrl[k])) {
                                saves[3].map.karel = new Robot(x, y, k);
                                robotExist = true;
                                break;
                            }
                        }
                    } else {
                        extraRobot = true;
                    }
                }
            }
            if (extraRobot || !robotExist) {
                String textInLabel = "地图中需要存在有且仅有一个Karel机器人，请修改地图";
                wrongHint(stage, textInLabel);
            } else if (rock.size() == 0) {
                String textInLabel = "地图中需要存在至少一个石头，请修改地图";
                wrongHint(stage, textInLabel);
            } else {
                saves[3].map.rock = new Rock[rock.size()];
                saves[3].map.wall = new Wall[wall.size()];
                saves[3].map.trap = new Trap[trap.size()];
                rock.toArray(saves[3].map.rock);
                wall.toArray(saves[3].map.wall);
                trap.toArray(saves[3].map.trap);
                saves[3].map.setIntoObstacle();
                originSaves[3] = saves[3].cloneSave();
                mapIndex = 3;
                readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                        exitAndRunHBox, exitButton, runCodeButton, restartButton);
                stage.setScene(mainScene);
            }
        });
        customMapScene.setOnMouseClicked(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double xDouble = (mouseX - 650) / previewElementWidth + ((double) saves[3].map.length) / 2;
            double yDouble = (mouseY - 426) / previewElementWidth + ((double) saves[3].map.width) / 2;
            int x = (int) xDouble;
            int y = (int) yDouble;
            if (xDouble >= 0 && xDouble <= saves[3].map.length && yDouble >= 0 && yDouble <= saves[3].map.width) {
                ImageView previewChangeImageView = new ImageView();
                previewChangeImageView.setFitHeight(previewElementWidth);
                previewChangeImageView.setFitWidth(previewElementWidth);
                if (nowChooseElement == 0) {
                    Image[] previewRobotImage = {
                            new Image("file:graph\\Robot_R.png"), new Image("file:graph\\Robot_U.png"),
                            new Image("file:graph\\Robot_L.png"), new Image("file:graph\\Robot_D.png") };
                    previewChangeImageView.setImage(previewRobotImage[previewRobotFace]);
                } else if (nowChooseElement == 1) {
                    previewChangeImageView.setImage(new Image("file:graph\\Rock.png"));
                } else if (nowChooseElement == 2) {
                    previewChangeImageView.setImage(new Image("file:graph\\Wall.png"));
                } else if (nowChooseElement == 3) {
                    previewChangeImageView.setImage(new Image("file:graph\\Trap.png"));
                } else if (nowChooseElement == 4) {
                    previewChangeImageView.setImage(new Image("file:graph\\Ground.png"));
                }
                previewMapGridPane.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == x
                        && GridPane.getRowIndex(node) == y);
                previewMapGridPane.add(previewChangeImageView, x, y);
            }
        });
        quitButton.setOnAction(event -> {
            saves[3] = originSaves[3].cloneSave();
            stage.setScene(startScene);
        });

        // 初始界面交互
        chooseMapButton.setOnAction(event -> {
            ifSaved = true;
            mapIndex = mapChoiceComboBox.getSelectionModel().getSelectedIndex();
            if ((mapIndex >= 0 && mapIndex < 3) || (mapIndex == 3 && ifCustomed)) {
                if (saves[mapIndex].ifOver) {
                    saves[mapIndex] = originSaves[mapIndex].cloneSave();
                }
                readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                        exitAndRunHBox, exitButton, runCodeButton, restartButton);
                stage.setScene(mainScene);
            } else if (mapIndex == 3 && !ifCustomed) {
                wrongHint(stage, "自定义地图尚未创建");
            }
        });
        customMapButton.setOnAction(event -> {
            nowChooseElement = 0;
            previewRobotFace = 0;
            chooseRobotHBox_2.getChildren().setAll(chooseRobotHBoxs[0]);
            chooseElementLabel.setText("点击下方图标选择所需放置的元素\n当前放置：Karel机器人");
            saves[3] = new Save(3);
            previewMapGridPane.getChildren().clear();
            for (int i = 0; i < saves[3].map.length; i++) {
                for (int j = 0; j < saves[3].map.width; j++) {
                    ImageView customMapElementImageView = new ImageView(new Image("file:graph\\Ground.png"));
                    customMapElementImageView.setFitHeight(previewElementWidth);
                    customMapElementImageView.setFitWidth(previewElementWidth);
                    previewMapGridPane.add(customMapElementImageView, i, j);
                }
            }
            stage.setScene(customMapScene);
        });

        // 地图界面交互
        runCodeButton.setOnAction(event -> {
            ifSaved = false;
            runCodeInTextArea(logText, wrongText, codeText, mapGridPane[0], mapVBox, exitAndRunHBox, exitButton,
                    restartButton, stage);
        });
        exitButton.setOnAction(event -> {
            if (!ifSaved) {
                askIfSave(stage, mapChoiceComboBox, changeMenu, codeText.getText(), save, mapIndex);
            }
            stage.setScene(startScene);
        });
        restartButton.setOnAction(event -> {
            saves[mapIndex] = originSaves[mapIndex].cloneSave();
            readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                    exitAndRunHBox, exitButton, runCodeButton, restartButton);
        });

        for (int i = 0; i < 4; i++) {
            int index = i;
            changeMenu.getItems().get(index).setOnAction(event -> {
                if (mapIndex != index && (index < 3 || (index == 3 && ifCustomed))) {
                    if (!ifSaved) {
                        askIfSave(stage, mapChoiceComboBox, changeMenu, codeText.getText(), save, mapIndex);
                    }
                    ifSaved = true;
                    mapIndex = index;
                    if (saves[mapIndex].ifOver) {
                        saves[mapIndex] = originSaves[mapIndex].cloneSave();
                    }
                    readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                            exitAndRunHBox, exitButton, runCodeButton, restartButton);
                } else if (index == 3 && !ifCustomed) {
                    wrongHint(stage, "自定义地图尚未创建");
                }
            });
        }

        saveMenuItem.setOnAction(event -> {
            ifSaved = true;
            save(mapChoiceComboBox, changeMenu, codeText.getText(), save, mapIndex);
        });
        readMenuItem.setOnAction(event -> {
            readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                    exitAndRunHBox, exitButton, runCodeButton, restartButton);
        });

        wayMenuItem.setOnAction(event -> {
            if (helpStage.isShowing()) {
                helpStage.close();
            }
            helpVBox.getChildren().setAll(helpWayTitleLabel, helpRunWayVBox, helpCustomWayVBox);
            helpStage.show();
        });
        aimMenuItem.setOnAction(event -> {
            if (helpStage.isShowing()) {
                helpStage.close();
            }
            helpVBox.getChildren().setAll(helpAimTitleLabel, helpAimVBox);
            helpStage.show();
        });
        codesMenuItem.setOnAction(event -> {
            if (helpStage.isShowing()) {
                helpStage.close();
            }
            helpVBox.getChildren().setAll(helpCodeTitleLabel, helpActionCodeVBox, helpInformationCodeVBox,
                    helpBooleanCodeVBox, helpBlockCodeVBoxWithTitle, helpInputWayVBox);
            helpStage.show();
        });
        easyCodesMenuItem.setOnAction(event -> {
            if (helpStage.isShowing()) {
                helpStage.close();
            }
            helpVBox.getChildren().setAll(helpEasyCodeTitleLabel, helpEasyCodeVBox);
            helpStage.show();
        });
        allMenuItem.setOnAction(event -> {
            if (helpStage.isShowing()) {
                helpStage.close();
            }
            helpVBox.getChildren().setAll(helpWayTitleLabel, helpRunWayVBox, helpCustomWayVBox, helpAimTitleLabel,
                    helpAimVBox, helpCodeTitleLabel, helpActionCodeVBox, helpInformationCodeVBox,
                    helpBooleanCodeVBox, helpBlockCodeVBoxWithTitle, helpInputWayVBox, helpEasyCodeTitleLabel,
                    helpEasyCodeVBox);
            helpStage.show();
        });

        // 地图界面快捷键
        mainScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                isKeyPressedCtrl = true;
            } else if (isKeyPressedCtrl) {
                if (event.getCode() == KeyCode.S) {
                    ifSaved = true;
                    save(mapChoiceComboBox, changeMenu, codeText.getText(), save, mapIndex);
                } else if (event.getCode() == KeyCode.Z) {
                    readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                            exitAndRunHBox, exitButton, runCodeButton, restartButton);
                } else if (event.getCode() == KeyCode.Q) {
                    if (!ifSaved) {
                        askIfSave(stage, mapChoiceComboBox, changeMenu, codeText.getText(), save, mapIndex);
                    }
                    stage.setScene(startScene);
                } else if (event.getCode() == KeyCode.R) {
                    if (save.ifOver) {
                        saves[mapIndex] = originSaves[mapIndex].cloneSave();
                        readSave(mapGridPane[0], mapVBox, logText, wrongText, codeText,
                                exitAndRunHBox, exitButton, runCodeButton, restartButton);
                    }
                } else if (event.getCode() == KeyCode.ENTER) {
                    if (!save.ifOver) {
                        ifSaved = false;
                        runCodeInTextArea(logText, wrongText, codeText, mapGridPane[0], mapVBox, exitAndRunHBox,
                                exitButton, restartButton, stage);
                    }
                }
            }
        });
        mainScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                isKeyPressedCtrl = false;
            }
        });
    }

    private String playCondition(Save save) {
        if (!save.ifBegin) {
            return "未游玩";
        } else if (save.ifSuccess) {
            return "已通关";
        } else if (save.ifOver) {
            return "已失败";
        } else {
            return "已游玩";
        }
    }

    private ObservableList<String> refreshSaveList() {
        List<String> mapList = Arrays.asList(
                "系统关卡1(" + playCondition(saves[0]) + ")",
                "系统关卡2(" + playCondition(saves[1]) + ")",
                "系统关卡3(" + playCondition(saves[2]) + ")",
                "自定义关卡(" + playCondition(saves[3]) + ")");
        ObservableList<String> obList = FXCollections.observableArrayList(mapList);
        return obList;
    }

    private void askIfSave(Stage stage, ComboBox<String> mapChoiceComboBox, Menu changeMenu, String codeText, Save save,
            int mapIndex) {
        Label ifSaveLabel = new Label("您即将退出当前存档，是否保存当前进度?");
        ifSaveLabel.setStyle(fontOf13);
        Button yesButton = new Button("是");
        yesButton.setPrefSize(110, 40);
        yesButton.setStyle(fontOf15);
        Button noButton = new Button("否");
        noButton.setPrefSize(110, 40);
        noButton.setStyle(fontOf15);
        HBox yesOrNoHBox = new HBox(20, yesButton, noButton);
        yesOrNoHBox.setAlignment(Pos.CENTER);
        VBox ifSaveVBox = new VBox(10, ifSaveLabel, yesOrNoHBox);
        ifSaveVBox.setAlignment(Pos.CENTER);

        BorderPane ifSaveBorderPane = new BorderPane();
        ifSaveBorderPane.setCenter(ifSaveVBox);
        Scene ifSaveScene = new Scene(ifSaveBorderPane, 400, 150);
        Stage ifSaveStage = new Stage();
        ifSaveStage.getIcons().add(new Image("file:graph\\Robot.png"));
        ifSaveStage.setTitle("Karel Robot");
        ifSaveStage.setScene(ifSaveScene);
        ifSaveStage.setResizable(false);
        ifSaveStage.setX(Screen.getPrimary().getVisualBounds().getWidth() / 2 - 200);
        ifSaveStage.setY(Screen.getPrimary().getVisualBounds().getHeight() / 2 - 75);
        ifSaveStage.initOwner(stage);
        ifSaveStage.initModality(Modality.WINDOW_MODAL);
        ifSaveStage.show();

        yesButton.setOnAction(event -> {
            ifSaveStage.close();
            save(mapChoiceComboBox, changeMenu, codeText, save, mapIndex);
        });
        noButton.setOnAction(event -> {
            ifSaveStage.close();
        });
    }

    private void save(ComboBox<String> mapChoiceComboBox, Menu changeMenu, String codeText, Save save, int mapIndex) {
        save.codeInTextArea = codeText;
        saves[mapIndex] = save.cloneSave();
        mapChoiceComboBox.setItems(refreshSaveList());
        mapChoiceComboBox.getSelectionModel().selectFirst();
        ObservableList<String> saveMenuItemNames = refreshSaveList();
        for (int i = 0; i < 4; i++) {
            changeMenu.getItems().get(i).setText(saveMenuItemNames.get(i));
        }
    }

    private void readSave(GridPane mapGridPane, VBox mapVBox, TextArea logText, TextArea wrongText, TextArea codeText,
            HBox exitAndRunHBox, Button exitButton, Button runCodeButton, Button restartButton) {
        save = saves[mapIndex].cloneSave();
        mapGridPane = save.map.output();
        mapVBox.getChildren().setAll(mapGridPane);
        logText.setText(save.logOutput);
        wrongText.setText(save.wrongOutput);
        codeText.setText(save.codeInTextArea);
        if (save.ifOver) {
            if (!save.ifSuccess) {
                ImageView robotDeadImageView = new ImageView(new Image("file:graph\\Dead.png"));
                robotDeadImageView.setFitHeight(save.map.mapElementWidth);
                robotDeadImageView.setFitWidth(save.map.mapElementWidth);
                mapGridPane.add(robotDeadImageView, save.map.karel.x, save.map.karel.y);
            }
            exitAndRunHBox.getChildren().setAll(exitButton, restartButton);
        } else {
            exitAndRunHBox.getChildren().setAll(exitButton, runCodeButton);
        }
    }

    private void wrongHint(Stage stage, String textInLabel) {
        Label wrongHintLabel = new Label(textInLabel);
        wrongHintLabel.setStyle(fontOf13);
        Button okButton = new Button("好的");
        okButton.setPrefSize(110, 40);
        okButton.setStyle(fontOf15);
        VBox wrongHintVBox = new VBox(10, wrongHintLabel, okButton);
        wrongHintVBox.setAlignment(Pos.CENTER);

        BorderPane wrongHintBorderPane = new BorderPane();
        wrongHintBorderPane.setCenter(wrongHintVBox);
        Scene wrongHintScene = new Scene(wrongHintBorderPane, 400, 150);
        Stage wrongHintStage = new Stage();
        wrongHintStage.getIcons().add(new Image("file:graph\\Robot.png"));
        wrongHintStage.setTitle("Karel Robot");
        wrongHintStage.setScene(wrongHintScene);
        wrongHintStage.setResizable(false);
        wrongHintStage.setX(Screen.getPrimary().getVisualBounds().getWidth() / 2 - 200);
        wrongHintStage.setY(Screen.getPrimary().getVisualBounds().getHeight() / 2 - 75);
        wrongHintStage.initOwner(stage);
        wrongHintStage.initModality(Modality.WINDOW_MODAL);
        wrongHintStage.show();

        okButton.setOnAction(event -> {
            wrongHintStage.close();
        });
    }

    private void runCodeInTextArea(TextArea logText, TextArea wrongText, TextArea codeText, GridPane mapGridPane,
            VBox mapVBox, HBox exitAndRunHBox, Button exitButton, Button restartButton, Stage stage) {
        save.runCodeInTextArea(codeText.getText());
        mapGridPane = save.map.output();
        mapVBox.getChildren().setAll(mapGridPane);
        logText.setText(save.logOutput);
        wrongText.setText(save.wrongOutput);
        if (save.ifOver) {
            if (save.ifSuccess) {
                wrongHint(stage, "恭喜你！！成功通关本地图！！");
            } else {
                ImageView robotDeadImageView = new ImageView(new Image("file:graph\\Dead.png"));
                robotDeadImageView.setFitHeight(save.map.mapElementWidth);
                robotDeadImageView.setFitWidth(save.map.mapElementWidth);
                mapGridPane.add(robotDeadImageView, save.map.karel.x, save.map.karel.y);
                wrongHint(stage, "你掉进陷阱里啦！很遗憾…请重新开始游玩");
            }
            exitAndRunHBox.getChildren().setAll(exitButton, restartButton);
        }
    }
}