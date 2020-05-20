package rumble;

import clojure.java.api.Clojure;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Splash extends Application {

    @Override
    public void start(Stage stage) {
        StackPane pane = new StackPane();
        BackgroundFill fill = new BackgroundFill(
            Color.WHITE,
            CornerRadii.EMPTY,
            Insets.EMPTY);
        BackgroundImage image = new BackgroundImage(
            new Image("/images/rumble.png", 240, 240, false, false, false),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT);
        pane.setBackground(new Background(
                               new BackgroundFill[]{fill},
                               new BackgroundImage[]{image}));
        Scene scene = new Scene(pane, 720, 480, Color.WHITE);
        stage.setScene(scene);
        stage.alwaysOnTopProperty();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        new Thread(() -> {
                Clojure
                    .var("clojure.core", "require")
                    .invoke(Clojure.read("rumble.core"));
                Clojure.var("rumble.core", "-main").invoke();
                Platform.runLater(new Runnable() {
                        @Override public void run() {
                            stage.close();
                        }
                    });
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }

}
