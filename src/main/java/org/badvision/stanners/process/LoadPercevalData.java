package org.badvision.stanners.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.badvision.stanners.StannersApp;
import org.badvision.stanners.model.Commit;
import org.badvision.stanners.model.Commit.CommitFile;

/**
 * Process perceval data
 */
public class LoadPercevalData {

    StringProperty statusProperty;
    File in;
    int countOfAllCommits = 0;
    public static int MAX_JAVA_FILES_PER_COMMIT = 20;
    public static Date EARLIEST_COMMIT = Date.valueOf("2000-01-01");

    public LoadPercevalData(File input) {
        in = input;
    }

    public void setStatusProperty(StringProperty textProperty) {
        statusProperty = textProperty;
    }

    public void updateStatus(String text) {
        Platform.runLater(() -> statusProperty.set(text));
        System.out.println("Perceval loader status: " + text);
    }

    public void runProcess() {
        InputStream is = null;
        try {
            is = new FileInputStream(in);
            Reader r = new InputStreamReader(is, "UTF-8");
            Gson gson = new GsonBuilder().create();
            JsonStreamParser p = new JsonStreamParser(r);
            while (p.hasNext()) {
                JsonElement e = p.next();
                if (e.isJsonObject()) {
                    Commit commit = gson.fromJson(e, Commit.class);
                    considerCommit(commit);
                }
            }
            updateStatus("Read " + countOfAllCommits + " commit entries, selected " + StannersApp.getState().getAllCommits().size() + " commits.");
        } catch (JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException ex) {
            updateStatus("Error "+ ex);
            ex.printStackTrace(System.out);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void considerCommit(Commit commit) {
        countOfAllCommits++;
        long numberOfJavaFiles = countJavaFilesInCommit(commit);
        if (numberOfJavaFiles > 0 && numberOfJavaFiles < MAX_JAVA_FILES_PER_COMMIT && isNotTooOld(commit)) {
            StannersApp.getState().getAllCommits().put(commit.getData().getCommitHash(), commit);
        }
    }

    private long countJavaFilesInCommit(Commit commit) {
        return commit.getData().getFiles().stream().filter(CommitFile::isJavaClass).count();
    }

    private boolean isNotTooOld(Commit commit) {
        return commit.getData().getCommitDate().getTimeInMillis() >= EARLIEST_COMMIT.getTime();
    }

}
