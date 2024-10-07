package gitlet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import java.io.File;

/** some helper method of branches
 * */
public class Branch {

    // creat a new branch, add into BRANCH_DIR.
    public static File creat_branch(String branch_name) {
        File new_branch = join(BRANCH_DIR, branch_name);
        return new_branch;
    }

    // change commit_id in that branch file
    public static void update_branch_pointer(File branch, String commit_id) {
        writeContents(branch, commit_id);
    }

    public static void updateCurBranch(String commit_id) {
        writeContents(BRANCH, commit_id);
    }


}
