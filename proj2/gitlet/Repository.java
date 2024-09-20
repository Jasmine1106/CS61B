package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jasmine1106
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** the first floor of the directory*/
    public static final File OBJECT_DIR = join(GITLET_DIR, "OBJECT_DIR");
    public static final File STAGE_DIR = join(GITLET_DIR, "STAGE_DIR");
    /** the second floor of the directory*/
    public static File BLOB_DIR = join(OBJECT_DIR, "BLOB_DIR");
    public static File COMMIT_DIR = join(OBJECT_DIR, "COMMIT_DIR");
    public static File ADDITION = join(STAGE_DIR, "ADDTION");
    public static File REMOVAL = join(STAGE_DIR, "REMOVAL");


    /* TODO: fill in the rest of this class. */
    /**
     * Does require filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * .GITLET_DIR/ -- hidden gitlet directory
     *    - OBJECT_DIR -- folder containing all Serializable object
     *         - BLOB_DIR -- all the reference of file object
     *         - COMMIT_DIR -- the commit tree
     *    - STAGE_DIR -- the staging area
     *         - ADDITION
     *         - REMOVAL
     */
    private static void setupPersistence() {
        // TODO
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            // create the basic structrue of gitlet directory
            GITLET_DIR.mkdir();
            OBJECT_DIR.mkdir();
            STAGE_DIR.mkdir();
            BLOB_DIR.mkdir();
            COMMIT_DIR.mkdir();
            ADDITION.mkdir();
            REMOVAL.mkdir();

        }
    }

    /** Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that contains no files and has the commit message
     * initial commit. It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC,*Thursday, 1 January 1970
     * in whatever format you choose for dates (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     * It will have a single branch: master, which initially points to this initial commit */
    public static void init(){
        setupPersistence();
        Commit inital = new Commit("initial commit", null);
        // Set up branch as master
    }

}
