package org.badvision.stanners.model;

import lombok.Data;

/**
 * Combine statistics about a given commit
 */
@Data
public class CommitStatistics {
    private String hash;
    private int bugFixCount;
    private int bugContributeCount;
    private int bugCreateCount;
    private int filesInCommit;
    private int linesAdded;
    private int linesRemoved;
    private double positiveSentiment;
    private double negativeSentiment;
}
