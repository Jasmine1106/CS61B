package gitlet;

import static gitlet.Utils.*;
import java.io.File;

import static gitlet.Repository.DEFAULT_BRANCH_NAME;
import static gitlet.Repository.BRANCH_DIR;
public class Branch {

    public static File creat_branch(String branch_name) {
        File new_branch = join(BRANCH_DIR, branch_name);
        return new_branch;
    }

    public static void update_branch_pointer(File branch, String commit_id) {
        writeContents(branch, commit_id);
    }

}
