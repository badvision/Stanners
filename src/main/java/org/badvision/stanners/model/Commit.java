/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.badvision.stanners.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import lombok.Data;

/**
 *
 * @author brobert
 */
@Data
public class Commit {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d HH:mm:ss YYYY Z");

    String backend_name;
    String backend_version;
    String category;
    String classified_fields_filtered;
    CommitData data;
    String origin;
    String perceval_version;
    String tag;
    long timestamp;
    public Calendar getTimestamp() {
        return parseDate(timestamp);
    }

    long updated_on;
    public Calendar getUpdated() {
        return parseDate(updated_on);
    }
    String uuid;
    SearchField search_fields;

    public static Calendar parseDate(long val) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(val);
        return cal;
    }

    public static Calendar parseDate(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        try {
            long dateMillis = Long.parseLong(str);
            cal.setTimeInMillis(dateMillis);
        } catch (NumberFormatException ex) {
            try {
                cal.setTime(DATE_FORMAT.parse(str));
            } catch (ParseException ex1) {
                System.out.println("Unable to parse date " + str);
                return null;
            }

        }
        return cal;
    }

    public static int parseInt(String str) {
        if (str == null || str.isBlank() || str.equals("-")) {
            return 0;
        } else {
            return Integer.parseInt(str);
        }
    }

    @Data
    public static class CommitData {

        String Author;
//        Calendar AuthorDate;
        String AuthorDate;

        public Calendar getAuthorDate() {
            return parseDate(AuthorDate);
        }
        
        String Commit;
        public String getCommitUser() {
            return Commit;
        }
//        Calendar CommitDate;
        String CommitDate;

        public Calendar getCommitDate() {
            return parseDate(CommitDate);
        }
        String commit;
        public String getCommitHash() {
            return commit;
        }
        String message;
        List<String> parents;
        List<String> refs;
        List<CommitFile> files;
    }

    @Data
    public static class CommitFile {

        String action;
        // int added;
        String added;
        public int getAdded() {
            return parseInt(added);
        }
        // int removed;
        String removed;
        public int getRemoved() {
            return parseInt(removed);
        }
        String file;
        List<String> indexes;
        List<String> modes;

        public boolean isJavaClass() {
            return file.toLowerCase().endsWith(".java") && !file.contains("package-info");
        }
    }

    @Data
    public static class SearchField {

        String item_id;
    }
}
