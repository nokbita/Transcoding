package priv.nokbita.app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import priv.nokbita.service.TransFileService;

public class App extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        TransFileService.getInstance().clearStaticTransFiles();
        TransFileService.getInstance().clearStaticTransFilesCopy();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Transcoding 文件编码");

        Image icon16 = new Image(getClass().getResourceAsStream("/images/icons/Transcoding_icon16.png"));
        Image icon32 = new Image(getClass().getResourceAsStream("/images/icons/Transcoding_icon32.png"));
        Image icon64 = new Image(getClass().getResourceAsStream("/images/icons/Transcoding_icon64.png"));
        primaryStage.getIcons().setAll(icon16,icon32,icon64);


        new AppUI().start(primaryStage);
    }
}
