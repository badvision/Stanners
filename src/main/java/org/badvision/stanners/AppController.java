/**
 * Sample Skeleton for 'app.fxml' Controller Class
 */

package org.badvision.stanners;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.badvision.stanners.process.AnalysisEngine;
import org.badvision.stanners.process.CommitSemtimentGenerator;
import org.badvision.stanners.process.LoadPercevalData;
import org.badvision.stanners.process.LoadSZZData;
import org.badvision.stanners.process.LoadSentimentData;

public class AppController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="percevalStatus"
    private Label percevalStatus; // Value injected by FXMLLoader

    @FXML // fx:id="szzStatus"
    private Label szzStatus; // Value injected by FXMLLoader

    @FXML // fx:id="log"
    private TextFlow log; // Value injected by FXMLLoader

    void runProcess(Runnable r) {
        new Thread(() -> {
            try {
                r.run();
                StannersApp.saveState();
            } catch (IOException ex) {
                System.out.println("------ERROR!!!!!-----");
                ex.printStackTrace(System.out);
                System.out.println();
                System.out.println("---------------------");
            }
        }).start();
    }

    @FXML
    void loadPercevalLog(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Perceval json output file");
        StannersApp.getState().setToCurrentDirectory(chooser);
        File selected = chooser.showOpenDialog(null);
        if (selected != null) {
            StannersApp.getState().updateCurrentDirectory(selected);
            LoadPercevalData percevalLoader = new LoadPercevalData(selected);
            percevalLoader.setStatusProperty(percevalStatus.textProperty());
            runProcess(percevalLoader::runProcess);
        }
    }

    @FXML
    void loadSZZData(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select szz results folder");
        StannersApp.getState().setToCurrentDirectory(chooser);
        File selected = chooser.showDialog(null);
        if (selected != null) {
            StannersApp.getState().updateCurrentDirectory(selected);
            LoadSZZData szzLoader = new LoadSZZData(selected);
            szzLoader.setStatusProperty(szzStatus.textProperty());
            runProcess(szzLoader::runProcess);
        }
    }


    @FXML
    void loadSentimentScores(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select sentistrength-se output file");
        StannersApp.getState().setToCurrentDirectory(chooser);
        File selected = chooser.showOpenDialog(null);
        if (selected != null) {
            StannersApp.getState().updateCurrentDirectory(selected);
            LoadSentimentData sentistrengthLoader = new LoadSentimentData(selected);
            runProcess(sentistrengthLoader::runProcess);
        }
    }

    @FXML
    void createCommitMessageSentimentFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select destination file");
        StannersApp.getState().setToCurrentDirectory(chooser);
        File selected = chooser.showSaveDialog(null);
        if (selected != null) {
            StannersApp.getState().updateCurrentDirectory(selected);
            CommitSemtimentGenerator generator = new CommitSemtimentGenerator(selected);
            runProcess(generator::runProcess);
        }
    }

    @FXML
    void performAnalysis(ActionEvent event) {
        AnalysisEngine analyzer = new AnalysisEngine();
        runProcess(analyzer::runProcess);

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert percevalStatus != null : "fx:id=\"percevalStatus\" was not injected: check your FXML file 'app.fxml'.";
        assert szzStatus != null : "fx:id=\"szzStatus\" was not injected: check your FXML file 'app.fxml'.";
        assert log != null : "fx:id=\"log\" was not injected: check your FXML file 'app.fxml'.";
        initSystemOut();
        try {
            StannersApp.restoreState();
            System.out.println("Restored state, " + StannersApp.getState().getAllCommits().size() + " selected commits and " + StannersApp.getState().getSzzResults().size() + " SZZ records.");
            if (!StannersApp.getState().getAllCommits().isEmpty()) {
                percevalStatus.setText("Restored " + StannersApp.getState().getAllCommits().size() + " commit records.");
            }
            if (!StannersApp.getState().getSzzResults().isEmpty()) {
                szzStatus.setText("Restored " + StannersApp.getState().getSzzResults().size() + " SZZ records.");
            }
        } catch (IOException ex) {
        }
    }

    private void initSystemOut() {
        PrintStream out = System.out;
        System.setOut(new PrintStream(out, true) {
            @Override
            public void print(String x) {
                super.print(x);
                log(x);
            }

            @Override
            public void println(String s) {
                print(s + "\n");
                flush();
            }
        });
    }

    private void log(String text) {
        Platform.runLater(()->{
            log.getChildren().add(new Text(text));
        });
    }
}
