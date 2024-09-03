package gitlet;

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
     *
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File Staging_area = join(GITLET_DIR, "Staging_area");

    /* TODO: fill in the rest of this class. */
    /**
     * Does require filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     * .gitlet/ -- top level folder for all persistent data in your lab12 folder
     *    - staging_area/ -- folder containing all  the persistent data for dogs
     *    - commit -- file containing the current story
     */
    private static void setupPersistence() {
        // TODO
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            Staging_area.mkdir();
            // branch = master, time = 0, commit message = initial commit
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
