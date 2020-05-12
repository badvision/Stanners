package org.badvision.stanners.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.badvision.stanners.model.SzzResults.CommitResults;
import org.badvision.stanners.model.SzzResults.ResultGraph;

/**
 * Represents the annotations data produced by SZZ Unleashed
 * Key is the commit hash and the value is a list of mapping graphs to previous commits, one for each file in the commit
 */
public class SzzResults extends HashMap<String, CommitResults> {
    public static class CommitResults extends ConcurrentSkipListSet<ResultGraph> {
        /**
         * Get the commit of the fixing commit
         * @return
         */
        public String getFixCommitHash() {
            return this.stream().findFirst().map(g->g.revisions.get(0)).orElse(null);
        }

        /**
         * Get contributors with a count of the number of times a commit showed as a contributor across multiple files
         *
         * @return Map of contributor hashes (key) to instances of each contributors (value)
         */
        public Map<String, Integer> getContributingHashes() {
            return this.stream().flatMap(ResultGraph::getContributingHashes).collect(
                    Collectors.toMap(a->a, a->1, (a,b)->a+b)
            );
        }

        /**
         * Get contributors with a count of the number of times a commit showed as a bug inducing commit across multiple files
         *
         * @return Map of bug inducing hashes (key) to instances of each bug inducer (value)
         */
        public Map<String, Integer> getBugInducingHashes() {
            return this.stream().flatMap(ResultGraph::getBugInducingHashes).collect(
                    Collectors.toMap(a->a, a->1, (a,b)->a+b)
            );
        }
    }

    @Data
    public static class ResultGraph implements Comparable<ResultGraph> {
        private String filePath;
        /**
         * Line mappings are organized by previous commit hash (key) and value is a set of line mappings between the line number of the current version (key) and the line number in the previous commit (value)
         */
        private Map<String, LineMappings> lineMappings;
        /**
         * Subgraphs trace back previous versions of a given file, as represented by the hash of each commit (key) and the graph data (value)
         */
        private Map<String, ResultGraph> subgraphs;
        /**
         * List of file revisions starting from most recent to the oldest
         */
        private List<String> revisions;

        public Stream<String> getContributingHashes() {
            String firstCommit = revisions.get(0);
            String lastCommit = revisions.get(revisions.size() - 1);

            Stream<String> contributors = revisions.stream().filter(s -> !s.equals(firstCommit) && !s.equals(lastCommit));
            if (subgraphs != null) {
                for (ResultGraph subgraph : subgraphs.values()) {
                    contributors = Stream.concat(contributors, subgraph.getContributingHashes());
                }
            }
            return contributors;
        }

        public Stream<String> getBugInducingHashes() {
            Stream<String> bugInducers;
            if (revisions.size() <= 1) {
                bugInducers = Stream.empty();
            } else {
                String lastCommit = revisions.get(revisions.size() - 1);
                bugInducers = Stream.of(lastCommit);
            }

            if (subgraphs != null) {
                for (ResultGraph subgraph : subgraphs.values()) {
                    bugInducers = Stream.concat(bugInducers, subgraph.getBugInducingHashes());
                }
            }
            return bugInducers;
        }

        @Override
        public int compareTo(ResultGraph g) {
            return getFilePath().compareTo(g.getFilePath());
        }

        public boolean isIdenticalTo(ResultGraph graph) {
            return revisions.containsAll(graph.revisions) && revisions.size() == graph.revisions.size();
        }
    }

    public static class CommitLineMappings extends HashMap<String, LineMappings> {}

    // Mappings from the line numbers in the current (most recent version) to previous line numbers from a previous commit
    // Key is the current line number and the value is the previous line number
    public static class LineMappings extends HashMap<String, Integer> {}
}
