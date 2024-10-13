package gitlet;


import static gitlet.Repository.*;
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;


/** Represents a gitlet commit object.
 *  @author Jasmine1106
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private final String message;
    /** use java.time  class rather than spec recommending.
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
        this.parents = new LinkedList<>();
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
        if (commitFile.exists()) {
            return readObject(commitFile, Commit.class);
        }
        return null;
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
            String blobID = pathToBlobID.get(path);
            Blob blob = Stage.getBlobByID(blobID);
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
        ZonedDateTime epoch = ZonedDateTime.ofInstant
                (new java.util.Date(0).toInstant(), ZoneId.systemDefault());
        return formatTimestamp(epoch);
    }

    /** @source: ChatGPT
     * problem: Mine  is  Thu Jan 01 08:00:00 1970 +08:00,
     * but need  Thu Jan 01 08:00:00 1970 +0800
     * ask GPT how to get rid of : between 08:00
     */
    private static String formatTimestamp(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        String formatted = dateTime.format(formatter);
        return formatted.replaceAll("([+-]\\d{2}):?(\\d{2})", "$1$2");
    }

    /** print as following format
     * ===
     * commit [commitID]
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     */

    public void print() {
        List<String>parents = getParents();
        System.out.println("===");
        System.out.println("commit " + commitID);
        if (parents.size() == 2) {
            System.out.println("Merge: " + parents.get(0).substring(0, 7) + " " + parents.get(1).substring(0, 7));
        }
        System.out.println("Date: " + timestamp);
        System.out.println(message);
        System.out.println();
    }



    public static String getCommitIDByAbbreb(String abbrevCommitID) {
        File[] commitFiles = COMMIT_DIR.listFiles();
        int matchCount = 0;
        String commitFullID = null;
        for (File commitFile : commitFiles) {
            if (commitFile.getName().startsWith(abbrevCommitID)) {
                commitFullID = commitFile.getName();
                matchCount += 1;
                if (matchCount > 1) {
                    System.out.println("Abbreviation is not unique.");
                    return null;
                }
            }
        }
        if (matchCount == 1) {
            return commitFullID;
        }
        return null;
    }

    public List<String> getHistoryCommit() {
        List<String> parents = this.getParents();
        List<String> reverseHistory = new LinkedList<>();
        reverseHistory.add(this.getCommitID());
        while (parents != null) {
            String parentID = parents.get(0);
            reverseHistory.add(parentID);
            parents = getCommitByID(parentID).getParents();
        }
        return reverseHistory.reversed();
    }

}
