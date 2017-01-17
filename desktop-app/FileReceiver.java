import java.lang.*;

import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.ServerSocket;

public class FileReceiver implements Runnable {
  private static final int port = 8000;

  private Socket socket;

  public static void main(String[] _) {
    try {
      ServerSocket listener = new ServerSocket(port);

      while (true) {
        FileReceiver file_rec = new FileReceiver();
        file_rec.socket = listener.accept();  

        new Thread(file_rec).start();
      }
    }
    catch (java.lang.Exception ex) {
      ex.printStackTrace(System.out);
    }
  }

  public void run() {
    try {
      InputStream in = socket.getInputStream();
      String dirName = null;
      long dirSize = 0;

      int nof_files = ByteStream.toInt(in);
      System.out.println("receiving: " + nof_files + " files ...");

      String fileType = ByteStream.toString(in);
      System.out.println("fileType: " + fileType);      

      if(fileType.equals("d")){
        dirName = ByteStream.toString(in);
        System.out.println("dirName: " + dirName);

        dirSize = ByteStream.toLong(in);
        System.out.println("dirSize: " + dirSize);
      }

      String filePath = null;
      for (int cur_file=0;cur_file < nof_files; cur_file++) {
        String file_name = ByteStream.toString(in);

        try{
          if(file_name == null || file_name.length() == 0 || file_name == "")
            continue;
            //System.out.println("Filename vuoto!!!");
          else if(file_name.contains("/")){ // it means it's a filePath, we may need to create some directories
            System.out.println("Pathed filename: " + file_name);
            if(file_name.startsWith("/")){
              filePath = file_name.substring(1, file_name.length());
            }else{
              filePath = file_name;
            }

            if(fileType.equals("d")){
              int index = filePath.indexOf(dirName);
              filePath = filePath.substring(index, filePath.length());
              System.out.println("filePath DIR-CASE: " + filePath);
            }
            System.out.println("filePath modified: " + filePath);

            File file = new File(filePath);
            file.getParentFile().mkdirs();
            file.createNewFile();

            ByteStream.toFile(in, file);
          }else{
            System.out.println("Unpathed filename: " + file_name);
            File file=new File(file_name);
            ByteStream.toFile(in, file);
          }          
        }catch(FileNotFoundException e){
          e.printStackTrace();
        }
      }
    }
    catch (java.lang.Exception ex) {
      ex.printStackTrace(System.out);
    }
  }

/*  public void updateModel(String filepath, int percentage, long sentData){
    Intent localIntent = new Intent(INTENT_NAME)
        // Puts the status into the Intent
        .putExtra(INTENT_ACTION, INTENT_ACTION_VALUE)
        .putExtra(EXTENDED_DATA_FILENAME, filepath)
        .putExtra(EXTENDED_DATA_RECEIVED, sentData)
        .putExtra(EXTENDED_DATA_PERCENTAGE, Integer.toString(percentage));
    // Broadcasts the Intent to receivers in this app.
    Log.d("TEST10", "sending broadcast message");
    mLocalBroadcastManager.sendBroadcast(localIntent);
  }*/
}