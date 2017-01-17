import java.lang.*;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.Socket;


public class FileSender {
  // host and port of receiver
  public static final String FILE_TYPE_F = "f";
  public static final String FILE_TYPE_DIR = "d";
  private static final int    port = 8000;
  private static final String host = "192.168.0.12";

  public static void main(String[] args) {
    try {
      Socket socket = new Socket(host, port);
      OutputStream os = socket.getOutputStream();
  
      int cnt_files = args.length;
      System.out.println("sending: " + Integer.toString(cnt_files) + " files ...");

      // How many files?
      ByteStream.toStream(os, cnt_files);

      // Sending file type
      ByteStream.toStream(os, FILE_TYPE_F);
  
      for (int cur_file=0; cur_file<cnt_files; cur_file++) {
        File file = new File(args[cur_file]);
        System.out.println("FileSize: " + file.length());
        ByteStream.toStream(os, file);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}