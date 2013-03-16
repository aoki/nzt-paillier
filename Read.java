/*
  argv[0] : Input file name
*/
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.math.BigInteger;

public class Read{

  FileInputStream fi;
  BufferedReader bf;

  public static void main(String[] argv){

    Read rd = new Read(argv[0]);
    String s;
    /*
    while((s=rd.readLine()) != null){
      System.out.println(s);
    }
    */
    String[] ss = rd.readAll();

    for(int i=0 ; i<ss.length ; ++i){
      System.out.println(ss[i]);
    }

    Read read2 = new Read(argv[0]);
    String[] sss = read2.readSec(argv[1]);
    read2.close();

    for(int j=0 ; j<sss.length ; ++j){
        System.out.println(sss[j]);
    }

    rd.close();

  }


  public Read(){
  }


  public Read(String file){

    try{
      fi = new FileInputStream(file);
      InputStreamReader is = new InputStreamReader(fi);
      bf = new BufferedReader(is);
    }catch(FileNotFoundException e){
      System.out.println("Error! File not found");
      System.out.println(e);
    }

  }


  public String readLine(){

    String s = null;

    try{
        if(bf != null){
      s = bf.readLine();
        }else{
      return null;
        }
    }catch(IOException e){
      System.out.println("Error!");
      System.out.println(e);
    }

    return s;

  }


  public String[] readAll(){

    byte[] data = null;

    try{
      int file_len = fi.available();
      data = new byte[file_len];
      fi.read(data);
    }catch(IOException e){
      System.out.println("Error! IO Error!!");
      System.out.println(e);
    }

    return this.disp(data, "\n\r");

  }


  public String[] readSec(String delim){

    String s = this.readLine();

    if(s == null){
      return null;
    }else{
      return disp(s.getBytes(), delim);
    }

  }


  public static String[] disp(byte[] data, String delim){
    String d = new String(data);
    StringTokenizer st = new StringTokenizer(d, delim);
    int line = st.countTokens();

    String[] s = new String[line];
    int i = 0;

    while(st.hasMoreTokens()){
      try{
        s[i++] = st.nextToken();
      }catch(NoSuchElementException e){
        System.out.println("Error! Can't next token!!");
        System.out.println(e);
      }
    }

    return s;

  }


  public int readByte(byte[] data){

    int a = 0;

    try{
      a = fi.read(data);
    }catch(IOException e){
      System.out.println(e);
    }

    return a;

  }


  public void close(){

    try{
      fi.close();
      bf.close();
    }catch(IOException e){
      System.out.println("Error! Can't close file.");
      System.out.println(e);
    }

  }


  public static BigInteger[] toBigInteger(String[] m, int radix){

    BigInteger[] a = new BigInteger[m.length];

    for(int i=0 ; i<a.length ; i++){
      a[i] = new BigInteger(m[i], radix);
    }

    return a;

  }


  public static BigInteger[] toBigInteger(String[] m){
    return toBigInteger(m, 10);
  }

}
