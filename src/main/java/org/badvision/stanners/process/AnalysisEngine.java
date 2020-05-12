/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.badvision.stanners.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.badvision.stanners.StannersApp;
import org.badvision.stanners.model.AppState;
import org.badvision.stanners.model.Commit;
import org.badvision.stanners.model.Commit.CommitFile;
import org.badvision.stanners.model.CommitStatistics;

/**
 *
 * @author brobert
 */
public class AnalysisEngine {
    public void updateStatus(String text) {
        System.out.println("Analysis status: " + text);
    }

    public void runProcess() {
        AppState state = StannersApp.getState();
        validatePrerequisites(state);
        updateStatus("Prerequisites met, analysis starting.");

        state.getCommitStats().clear();

        copySentimentValues();
        mergeCodeStats();
        updateStatus("Anaysis complete.  Hope your hypothesis was good, human.");
        writeOutput();
    }

    private void validatePrerequisites(AppState state) {
        updateStatus("Checking if sentiment scores are loaded.");
        assert(!state.getSentimentScores().isEmpty());
        updateStatus("Checking if SZZ results are loaded.");
        assert(!state.getSzzResults().isEmpty());
        updateStatus("Checking if GIT Logs are loaded.");
        assert(!state.getAllCommits().isEmpty());

    }

    private void copySentimentValues() {
        AppState state = StannersApp.getState();
        state.getSentimentScores().values().forEach(s -> {
            CommitStatistics stat = getOrCreateCommit(s.getId());
            stat.setPositiveSentiment(s.getPositiveScore());
            stat.setNegativeSentiment(s.getNegativeScore());
        });
    }

    private void mergeCodeStats() {
        AppState state = StannersApp.getState();

        state.getSzzResults().forEach((hash, commitResults) -> {
            CommitStatistics fix = getOrCreateCommit(commitResults.getFixCommitHash());
            fix.setBugFixCount(fix.getBugFixCount() + 1);
            commitResults.getContributingHashes().forEach((contributingHash, count) -> {
                CommitStatistics contributor = getOrCreateCommit(contributingHash);
                contributor.setBugContributeCount(contributor.getBugContributeCount() + count);
            });
            commitResults.getBugInducingHashes().forEach((bugHash, count) -> {
                CommitStatistics bug = getOrCreateCommit(bugHash);
                bug.setBugCreateCount(bug.getBugCreateCount() + count);
            });
        });
    }

    private void writeOutput() {
        AppState state = StannersApp.getState();
        File output = new File(state.getCurrentDirectory(), "stanners_out.txt");
        try (FileWriter writer = new FileWriter(output)) {
            writer.append(getOutputHeader());
            state.getCommitStats().values().forEach(s -> {
                try {
                    writer.append(getOutputLine(s));
                } catch (IOException ex) {
                    Logger.getLogger(AnalysisEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(AnalysisEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getOutputHeader() {
        return String.join("\t", "Hash", "Files", "Lines Added", "Lines Removed", "Bugs Induced", "Bugs Contributed", "Bugs Fixed", "Positive Sentiment", "Negative Sentiment") + "\n";
    }

    private String getOutputLine(CommitStatistics s) {
        return String.format("%s\t%d\t%d\t%d\t%d\t%d\t%d\t%1.1f\t%1.1f\n",
                s.getHash(), s.getFilesInCommit(), s.getLinesAdded(), s.getLinesRemoved(),
                s.getBugCreateCount(), s.getBugContributeCount(), s.getBugFixCount(),
                s.getPositiveSentiment(), s.getNegativeSentiment());
    }

    private CommitStatistics getOrCreateCommit(String hash) {
        AppState state = StannersApp.getState();
        CommitStatistics s = state.getCommitStats().get(hash);
        if (s == null) {
            s = new CommitStatistics();
            s.setHash(hash);
            Commit c = state.getAllCommits().get(hash);
            if (c != null) {
                s.setFilesInCommit(c.getData().getFiles().size());
                s.setLinesAdded(c.getData().getFiles().stream().collect(Collectors.summingInt(CommitFile::getAdded)));
                s.setLinesRemoved(c.getData().getFiles().stream().collect(Collectors.summingInt(CommitFile::getRemoved)));
            }
            state.getCommitStats().put(hash, s);
        }
        return s;
    }
}
