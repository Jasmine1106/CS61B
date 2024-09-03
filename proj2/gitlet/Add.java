package gitlet;

import java.io.File;
import static gitlet.Utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * add [file name]
 *  Adds a copy of the file as it currently exists to the staging area.The staging area should be somewhere in .gitlet.
 *  If the current working version of the file is identical to the version in the current commit, do not stage it to be added,
 *  and remove it from the staging area if it is already there.
 */
public class Add {
    static File CWD = Repository.CWD;
    static File staging_area = Repository.Staging_area;


    // check that if adding file exists
    public static void ValidCheck(String file_name) {
        if (!searchfile(CWD, file_name)) {
            throw new IllegalArgumentException("File does not exist.");
        }
    }

    // a private method to search file
    public static boolean searchfile(File directory, String file_name) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(file_name)) {
                return true;
            }
            if (file.isDirectory()){
                searchfile(file, file_name);
            }
        }
        return false;
    }

    // copy file into staging area
    public static void copy_file(String file_name) throws IOException {
        Path sourcePath = Paths.get(file_name); // 源文件路径
        File copy_version = join(staging_area, file_name);
        Files.copy(sourcePath, copy_version.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void add(String file_name) throws IOException {
        ValidCheck(file_name);
        copy_file(file_name);
    }

}
