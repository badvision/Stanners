package org.badvision.stanners.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.badvision.stanners.StannersApp;
import org.badvision.stanners.model.Commit;

/**
 * Create tab-delimited file of commit messages for passing into SentiStrength-SE
 */
public class CommitSemtimentGenerator {
    File out;
    public CommitSemtimentGenerator(File o) {
        out = o;
    }

    public void runProcess() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(out));
            long written = StannersApp.getState().getAllCommits().values().stream()
                    .filter(this::hasCommitMessage)
                    .map(this::convertToOutputFormat)
                    .peek(writer::println)
                    .count();
            System.out.println("Finished writing SentiStrength-SE commit message file, wrote " + written + " lines");
        } catch (IOException ex) {
            Logger.getLogger(CommitSemtimentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }

    public boolean hasCommitMessage(Commit c) {
        return c.getData().getMessage() != null && !c.getData().getMessage().isBlank();
    }

    public String convertToOutputFormat(Commit c) {
        return String.format("%s\t%s", c.getData().getCommitHash(), stripMessage(c.getData().getMessage()));
    }

    public String stripMessage(String s) {
        if (s == null) {
            return "";
        }
        String str = s.replaceAll("[\\n\\t\\s]+", " ");
        str = str.replaceAll("[^0-9a-zA-Z\\s]", "");
        str = str.replaceAll("[\\n\\t\\s]+", " ");
        return str;
    }
}
