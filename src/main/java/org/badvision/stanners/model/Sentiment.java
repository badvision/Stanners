package org.badvision.stanners.model;

import lombok.Data;

/**
 * Sentiment results
 */
@Data
public class Sentiment {
    private String id;
    private String text;
    private int positiveScore;
    private int negativeScore;
}
