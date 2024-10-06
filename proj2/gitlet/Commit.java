package gitlet;

// TODO: any imports you need here

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import gitlet.Repository.*;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


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
    private String timestamp;
    /** the sha1 code of this commit*/
    private String commit_id;
    /** a treemap for storing blobs, file paths is key, and blob_id is value */
    private Map<String, String> pathToBlobID;
    /** a Linked list for sorting all parents' commit_id*/
    private List<String> parents;
    // File storing the commit object
    private transient File commit_file;



    /* TODO: fill in the rest of this class. */
    /** creat a commit object */
    public Commit(String message, List<String> parents, Map<String, String> pathToBlobID) {
        this.message = message;
        this.timestamp = makeTimestamp();
        this.commit_id = generateID();
        this.commit_file = generate_commit_file();
        this.parents = parents;
        this.pathToBlobID = pathToBlobID;
    }
    // initial commit
    public Commit() {
        this.message = "initial commit";
        this.parents = new LinkedList<>();
        this.pathToBlobID = new TreeMap<>();
        this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        this.commit_id = generateID();
        this.commit_file = generate_commit_file();
        save_HEAD(this.commit_id);      // write this commit_id into HEAD
    }

    // generate a commit_id
    private String generateID() {
        return Utils.sha1(timestamp, message, parents.toString(), pathToBlobID.toString());
    }

    private File generate_commit_file() {
        return join(COMMIT_DIR, commit_id);
    }

    // save current commit_id into HEAD file
    public static void save_HEAD(String commitId){
        writeContents(HEAD, commitId);
    }

    public void save() {
        writeObject(commit_file, this);
    }

    public String from_Head() {
        return readContentsAsString(HEAD);
    }

    // get a commit object from commit_id
    public static Commit fromFile(String commitId) {
        File commitFile = join(COMMIT_DIR, commitId);
        return readObject(commitFile, Commit.class);
    }


    /** some get method */
    public List<String> getParents() {
        return parents;
    }



    public Map<String, String> getPathToBlobID() {
        return pathToBlobID;
    }

    public String getCommit_id() {
        return commit_id;
    }
    public  List<String> update_parents() {
        String update_id =  from_Head();
        parents.add(update_id);
        return parents;
    }



    // a helper method to make a timestamp
    public static String makeTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z").withZone(ZoneId.systemDefault());
        String timestamp = now.format(formatter);
        return timestamp;
    }




}
