package yarhar.utils;

import java.io.File;

public class FileUtils {
  /** 
   * Returns the canonical path of Yarhar's working directory 
   * (where it's running from). 
   */
  public static String getWorkingPath() {
    try {
      File f = new File("");
      return f.getCanonicalPath();
    } 
    catch(Exception e) {
      return "";
    }
  }
  
  /** 
   * Returns the path of file relative to home. 
   * file is the canonical path of the file we want to find the relative path
   * relative to the canonical path of the directory, home.
   */
  public static String getRelativePath(String file, String home) {
    String result = "";
    if(file.startsWith(home)) {
      result = file.substring(home.length()+1);
    }
    else {
      while(file.charAt(0) == home.charAt(0)) {
        file = file.substring(1);
        home = home.substring(1);
      }
      
      int goBacks = StringUtils.countChars(home,'/') + StringUtils.countChars(home,'\\') + 1;
      for(int i = 0; i < goBacks; i++) {
        result += "../";
      }
      result += file;
    }
    return result;
  }
  
  
  
  
  public static void main(String[] args) {
    String home = "a/b/c";
    String path1 = "a/b/c/d/e.txt";
    String path2 = "a/d/f/g.txt";
    
    System.out.println("Rel path of " + path1 + " to " + home + " : " + getRelativePath(path1, home));
    System.out.println("Rel path of " + path2 + " to " + home + " : " + getRelativePath(path2, home));
  }
}
