package org.badvision.stanners;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.harawata.appdirs.AppDirsFactory;
import org.badvision.stanners.model.AppState;

/**
 * StannersApp - Provide glue logic between Perceval, Git, and SentiStrength-SE tools
 * for more direct analysis to find corelation between sentiment in text and [defect|fix]-inducing changes
 */
public class StannersApp extends Application {
    public static final String STATE_FILE = AppDirsFactory.getInstance().getUserDataDir("stanners", null, "org.badvision") + "/state.json";
    private static AppState state = new AppState();
    public static AppState getState() {
        return state;
    }

    public static void saveState() throws IOException {
        Gson gson = new Gson();
        File f = new File(STATE_FILE);
        f.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(STATE_FILE)) {
            gson.toJson(state, writer);
            writer.flush();
        }
    }

    public static boolean restoreState() throws FileNotFoundException, IOException {
        Gson gson = new Gson();
        AppState restoredState;
        try (FileReader reader = new FileReader(STATE_FILE)) {
            state = gson.fromJson(reader, AppState.class);
            if (state != null) {
                return true;
            } else {
                state = new AppState();
            }
        }
        return false;
    }

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("app"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StannersApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}