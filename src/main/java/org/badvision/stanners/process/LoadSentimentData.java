package org.badvision.stanners.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.badvision.stanners.StannersApp;
import org.badvision.stanners.model.Sentiment;

/**
 * Process SentiStrength-SE data
 */
public class LoadSentimentData {

    File input;

    public LoadSentimentData(File input) {
        this.input = input;
    }

    public void updateStatus(String text) {
        System.out.println("SentiStrength status: " + text);
    }

    public void runProcess() {
        try {
            StannersApp.getState().getSentimentScores().clear();

            Files.lines(input.toPath()).map(this::parseLine).forEach(s
                    -> StannersApp.getState().getSentimentScores().put(s.getId(), s)
            );

            updateStatus("Finished loading all sentiment data: Read " + StannersApp.getState().getSentimentScores().size() + " records.");
        } catch (IOException ex) {
            Logger.getLogger(LoadSentimentData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Sentiment parseLine(String line) {
        String[] parts = line.split("\\t");
        Sentiment s = new Sentiment();
        s.setId(parts[0]);
        s.setText(parts[1]);
        String[] scores = parts[2].split("\\s+");
        s.setPositiveScore(Integer.parseInt(scores[0]));
        s.setNegativeScore(Integer.parseInt(scores[1]));
        return s;
    }

}
