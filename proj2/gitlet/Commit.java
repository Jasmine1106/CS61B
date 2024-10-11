package gitlet;

// TODO: any imports you need here

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *  @author Jasmine1106
 *  1.Each commit should contain the date and time it was made.
 *  2.Each commit has a log message associated with it that describes the changes to the files in the commit.
 *   This is specified by the user. The entire message should take up only one entry in the array args that is passed to main.
 *   To include multiword messages, you’ll have to surround them in quotes.
 *  3.Each commit is identified by its SHA-1 id, which must include the file (blob) references of its files,
 *   parent reference, log message, and commit time.
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private final String message;
    /** use java.time and java.time.DatetimeFormatter class rather than spec recommending.
     * The timestamps  */
    private final String timestamp;
    /** the sha1 code of this commit*/
    private final String commitID;
    /** a treemap for storing blobs, file paths is key, and blob_id is value */
    private final Map<String, String> pathToBlobID;
    /** a Linked list for sorting all parents' commitID*/
    private final List<String> parents;
    // File storing the commit object
    private final transient File commitFile;



    /* TODO: fill in the rest of this class. */
    /** creat a commit object */
    public Commit(String message, List<String> parents, Map<String, String> pathToBlobID) {
        this.message = message;
        this.timestamp = makeTimestamp();
        this.commitID = generateID();
        this.commitFile = generateCommitFile();
        this.parents = parents;
        this.pathToBlobID = pathToBlobID;
    }
    // initial commit
    public Commit() {
        this.message = "initial commit";
        this.parents = new ArrayList<>();
        this.pathToBlobID = new TreeMap<>();
        this.timestamp = makeInitialTimestamp();
        this.commitID = generateID();
        this.commitFile = generateCommitFile();
        saveHEAD(this.commitID);      // write this commitID into HEAD
    }

    // generate a commitID
    private String generateID() {
        String patentsString = parents != null ? parents.toString() : "";
        String pathToBlobIDString = pathToBlobID != null ? pathToBlobID.toString() : "";
        return Utils.sha1(timestamp, message, patentsString, pathToBlobIDString);
    }

    private File generateCommitFile() {
        return join(COMMIT_DIR, commitID);
    }

    // save current commitID into HEAD file
    public static void saveHEAD(String commitId) {
        writeContents(HEAD, commitId);
    }

    public void save() {
        writeObject(commitFile, this);
    }

    // get a commit object from commitID
    public static Commit getCommitByID(String commitId) {
        File commitFile = join(COMMIT_DIR, commitId);
        if (!commitFile.exists()) {
            exit("No commit with that id exists.");
        }
        return readObject(commitFile, Commit.class);
    }


    /** some get method */
    public List<String> getParents() {
        return parents;
    }

    public Map<String, String> getPathToBlobID() {
        return pathToBlobID;
    }

    public String getCommitID() {
        return commitID;
    }

    public String getMessage() {
        return message;
    }

    public List<Blob> getBlobList() {
        List<Blob> blobList = new LinkedList<>();
        for (String path : pathToBlobID.keySet()) {
            String blob_id = pathToBlobID.get(path);
            Blob blob = Stage.getBlobByID(blob_id);
            blobList.add(blob);
        }
        return blobList;
    }

    // current timestamp
    public static String makeTimestamp() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        return formatTimestamp(now);
    }

    // initial timestamp
    public static String makeInitialTimestamp() {
        Date date = new Date(0);
        ZonedDateTime epoch = ZonedDateTime.ofInstant(new java.util.Date(0).toInstant(), ZoneId.systemDefault());
        return formatTimestamp(epoch);
    }

    /** @source: ChatGPT
     * problem: My output is  Thu Jan 01 08:00:00 1970 +08:00,
     * but need  Thu Jan 01 08:00:00 1970 +0800
     * ask GPT how to get rid of : between 08:00
     */
    private static String formatTimestamp(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        String formatted = dateTime.format(formatter);
        return formatted.replaceAll("([+-]\\d{2}):?(\\d{2})", "$1$2");
    }

    /** print as following format
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     */

    public void print() {
        System.out.println("===");
        System.out.println("commit " + commitID);
        System.out.println("Date: " + timestamp);
        System.out.println(message);
        System.out.println();
    }

    public static String getCommitIDByAbbreb(String abbrevCommitID) {
        File[] commitFiles = COMMIT_DIR.listFiles();
        int count = 0;
        String commitID = null;
        for (File commitFile : commitFiles) {
            if (commitFile.getName().startsWith(abbrevCommitID)) {
                commitID = commitFile.getName();
                count += 1;
            }
        }
        if (count == 1) {
            return commitID;
        } else if (count == 0) {
            System.out.println("No commit with that abbreviation exists.");
        } else {
            System.out.println("Abbreviation is not unique.");
        }
        return null;
    }


}
