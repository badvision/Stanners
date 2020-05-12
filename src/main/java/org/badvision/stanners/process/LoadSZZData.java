package org.badvision.stanners.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.badvision.stanners.StannersApp;
import org.badvision.stanners.model.SzzResults;
import org.badvision.stanners.model.SzzResults.ResultGraph;

/**
 * Process SZZ Unleashed processing data
 */
public class LoadSZZData {

    AtomicInteger totalMergeConflicts = new AtomicInteger();
    StringProperty statusProperty;
    File inputDirectory;

    public LoadSZZData(File input) {
        inputDirectory = input;
    }

    public void setStatusProperty(StringProperty textProperty) {
        statusProperty = textProperty;
    }

    public void updateStatus(String text) {
        Platform.runLater(() -> statusProperty.set(text));
        System.out.println("SZZ loader status: " + text);
    }

    public void runProcess() {
        StannersApp.getState().getSzzResults().clear();
        for (File subfolder : inputDirectory.listFiles()) {
            for (File annotation : subfolder.listFiles(f -> f.getName().equalsIgnoreCase("annotations.json"))) {
                processAnnotationFile(annotation);
            }
        }
        updateStatus("Finished loading all SZZ data: Read " + StannersApp.getState().getSzzResults().size() + " records and " + totalMergeConflicts.get() + " merge conflicts.");
    }

    private void processAnnotationFile(File annotationFile) {
        try (InputStream is = new FileInputStream(annotationFile)) {
            updateStatus("Reading results from " + annotationFile.getAbsolutePath());
            Reader r = new InputStreamReader(is, "UTF-8");
            Gson gson = new GsonBuilder().create();
            SzzResults results = gson.fromJson(r, SzzResults.class);
            mergeResults(results);
            updateStatus("Read " + results.size() + " records.");
        } catch (IOException | JsonSyntaxException ex) {
            updateStatus("Error " + ex);
            ex.printStackTrace(System.out);
        }
    }

    private void mergeResults(SzzResults results) {
        SzzResults allResults = StannersApp.getState().getSzzResults();
        results.forEach((key, graph) -> {
            if (allResults.containsKey(key)) {
                AtomicInteger conflicts = new AtomicInteger();
                graph.forEach(fileGraph -> {
                    Optional<ResultGraph> existingGraph = allResults.get(key).stream().filter((ResultGraph rg) -> rg.getFilePath().equals(fileGraph.getFilePath())).findFirst();
                    if (existingGraph.isPresent() && !fileGraph.isIdenticalTo(existingGraph.get())) {
                        updateStatus("WARNING: Merge conflict over file " + fileGraph.getFilePath());
                        conflicts.incrementAndGet();
                        totalMergeConflicts.incrementAndGet();
                    } else if (existingGraph.isEmpty()) {
                        allResults.get(key).add(fileGraph);
                    }
                });
                if (conflicts.get() > 0) {
                    updateStatus("Commit hash " + key + " had " + conflicts.get() + " merge conflicts.");
                }
            } else {
                allResults.put(key, results.get(key));
            }
        });
    }
}
