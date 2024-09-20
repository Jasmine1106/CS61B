package gitlet;

// TODO: any imports you need here
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Repository.BLOB_DIR;
import static gitlet.Utils.join;
import static gitlet.Utils.sha1;
import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Add.checkFileExist;
import static gitlet.Repository.ADDITION;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
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
    private String timestamp = makeTimestamp();
    /** the sha1 code of this commit*/
    private String uid = sha1();
    /** the parent commit*/
    private String parent;
    /** a treemap for storing the commit tree,ket is commit_id,value is references for bolbs*/
    TreeMap<String, String> commit_tree = new TreeMap<>();

    BlobsId = 


    /* TODO: fill in the rest of this class. */

    /** creat a commit object */
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        if (parent == null) {
            this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        }
        this.timestamp = makeTimestamp();
    }

    // a helper method to make a timestamp
    private String makeTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z").withZone(ZoneId.systemDefault());
        String timestamp = now.format(formatter);
        return timestamp;
    }

    // clean the staging area
    public void clean_staging() {

    }

    // if STAGE_DIR's ADDITION folder havn't ant files, return false.
    private boolean checkCanCommit() {
        if (ADDITION.listFiles().length == 0) {
            return false;
        }
        return true;
    }

    /*
    search wheter file in staging file is already in BLOB_DIR,
    if so, update it and delete the old version; if not, creat that file
    **/
    private void update_file(String file_name){
        boolean ifExist = Add.checkFileExist(BLOB_DIR, file_name);
    }

    /** using TreeMap to store the information of commit history, the key is the file_name ,value if the */


    public void commit(String message) {
        if (checkCanCommit()){
             Commit new_commit = new Commit(message,commit_tree.lastKey());
             new_commit.uid = sha1(new_commit);
            commit_tree.put(new_commit.uid, BlobsId);
        }
    }
}
