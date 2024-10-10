package gitlet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import java.io.File;

/** some helper method of branches
 * */
public class Branch {

    public static File getBranchFileByName(String branchName) {
        File[] branchesList = BRANCH_DIR.listFiles();
        if (branchesList != null) {
            for (File branch : branchesList) {
                if (branch.getName().equals(branchName)) {
                    return branch;
                }
            }
        }
        return null;
    }

    // creat a new branch, add into BRANCH_DIR.
    public static File creat_branch(String branch_name) {
        return join(BRANCH_DIR, branch_name);
    }

    // change commit_id in that branch file
    public static void update_branch_pointer(File branch, String commit_id) {
        writeContents(branch, commit_id);
    }

    public static void updateCurBranch(String commit_id) {
        writeContents(BRANCH, commit_id);
    }


}
