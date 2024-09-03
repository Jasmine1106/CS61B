package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import static gitlet.Utils.sha1;

import java.util.Date;

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
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** use java.time and java.time.DatetimeFormatter class rather than spec recommending.
     * The timestamps  */
    private String timestamp = makeTimestamp();
    /** the sha1 code of this commit*/
    String uid = sha1();
    /** the parent commit*/
    String parent;


    /* TODO: fill in the rest of this class. */

    /** creat a commit object */
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        if (parent == null) {
            this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        }
    }

    // make a timestamp
    private String makeTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z").withZone(ZoneId.systemDefault());
        String timestamp = now.format(formatter);
        return timestamp;
    }

    // clean the staging area
    public void clean_staging() {

    }

    // if staging area havn't ant files to commit, return false.
    private boolean checkCanCommit(File staging_area) {
        if (staging_area.listFiles().length == 0) {
            return false;
        }
        return true;
    }

    // search wheter file in staging file is already in CWD,if so, update it and delete the old version; if not, creat that file
    private void update_file(){
        boolean ifExist = Add.searchfile(CWD, )
    }


    public void commit(String message) {

    }
}
