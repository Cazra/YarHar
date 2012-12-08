package yarhar;

import pwnee.fileio.*;
import java.io.Serializable;
import java.util.HashMap;

/** A saveable/loadable HashMap of persistent variables used by YarHar. */
public class YarharConfig implements Serializable {
  public static String path = "yarhar.dat";
  
  public HashMap<String, String> vars = new HashMap<String,String>();
  
  public YarharConfig() {
    
  }
  
  public static YarharConfig load() {
    ObjectFileIO ofio = new ObjectFileIO();
    Object obj = ofio.loadObject(YarharConfig.path);
    if(obj != null && obj instanceof YarharConfig)
      return (YarharConfig) obj;
    else {
      YarharConfig config = new YarharConfig();
      config.setDefaults();
      return config;
    }
  }
  
  
  public void save() {
    ObjectFileIO ofio = new ObjectFileIO();
    ofio.saveObject(this,YarharConfig.path);
  }
  
  
  public void setDefaults() {
    vars.put("lastOpen", ".");
  }
  
}
