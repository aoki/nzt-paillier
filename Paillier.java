/*
 * This is Paillier algorithm.
 * version 1.1
 * 2009.12.01 by nakazato
 *
 *
 *Update  2010/11/16  Yoshiki Aoki
*/

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import java.util.TreeMap;

public class Paillier{
  BigInteger n;
  BigInteger n2;
  BigInteger p;
  BigInteger q;
  BigInteger d;
  BigInteger l;
  SecureRandom rnd; // 2010/11/17 Yoshiki Aoki このクラスは暗号用に強化された乱数ジェネレータ．なんだそれ！すご．

  Paillier(){
  }
  // 公開鍵のみでPaillierオブジェクトを作成
  Paillier(BigInteger n){
    this.n = n;
    this.n2 = n.pow(2);
    this.rnd = new SecureRandom();
  }

  // KeyオブジェクトからでPaillierオブジェクトを作成
  Paillier(Key inputKey){
    // set PublicKey
    this.n = inputKey.getPublic();
    this.n2 = n.pow(2);
    this.rnd = new SecureRandom();

    // set SecretKey
    BigInteger[] secretKey = inputKey.getSecret();
    this.p = secretKey[0];
    this.q = secretKey[1];
    this.d = secretKey[2];
    this.l = inputKey.getLambda();
  }

  public void displayKey(){
    System.out.println("[Key] n      : " + this.n );
    System.out.println("[Key] n^2    : " + this.n2);
    System.out.println("[Key] p      : " + this.p );
    System.out.println("[Key] q      : " + this.q );
    System.out.println("[Key] d      : " + this.d );
    System.out.println("[Key] labmda : " + this.l );
  }

  // 文字列を暗号化
  public BigInteger enc(String mess){
    BigInteger m = new BigInteger(mess.getBytes());
//    System.out.println("Message : " + m);
    return this.enc(m);
  }

  // Intを暗号化
  public BigInteger enc(int mess){
    BigInteger m = new BigInteger(String.valueOf(mess));
//    System.out.println("Message : " + m);
    return this.enc(m);
  }

  // 数値を暗号化
  public BigInteger enc(BigInteger mess){
    //c = mess.modPow(e, n);
    BigInteger r = new BigInteger(n.bitLength(), rnd);
    r = r.mod(n);

    BigInteger c = (BigInteger.ONE.add(n)).modPow(mess, n2).multiply(r.modPow(n, n2)).mod(n2);
    return c;
  }


  public TreeMap<String, BigInteger> enc(TreeMap<String, BigInteger> messTree){
    //c = mess.modPow(e, n);
    BigInteger r = new BigInteger(n.bitLength(), rnd);
    r = r.mod(n);

    BigInteger c;
    BigInteger mess;
    for(Object userKey: messTree.keySet()){
      mess = new BigInteger((messTree.get(userKey)).toString());
      c = (BigInteger.ONE.add(n)).modPow(mess, n2).multiply(r.modPow(n, n2)).mod(n2);
      messTree.put(userKey.toString(), c);
    }

    return messTree;
  }

  // 数値に復号 引数暗号文のみ
  public BigInteger dec(BigInteger c){
    BigInteger mA = c.mod(this.n);
    BigInteger r = mA.modPow(this.d, this.n);
    BigInteger mB = (c.multiply((r.modPow(this.n, this.n2)).modInverse(this.n2))).mod(this.n2);
    BigInteger m = ((mB.subtract(BigInteger.ONE)).divide(this.n)).mod(this.n);
    return m;
  }

  // 数値に復号
  public static BigInteger dec(BigInteger c, BigInteger d, BigInteger n, BigInteger n2){
    BigInteger mA = c.mod(n);
    BigInteger r = mA.modPow(d, n);
    BigInteger mB = (c.multiply((r.modPow(n, n2)).modInverse(n2))).mod(n2);
    BigInteger m = ((mB.subtract(BigInteger.ONE)).divide(n)).mod(n);
    return m;
  }



  // args[0]:bit of key length, args[1]:Message
  public static void main(final String[] args){
    if(args.length != 1) {
      System.out.println("Usage: java Paillier <Key bit length>");
      System.out.println("\tExample: java Paillier 1024");
      System.exit(1);
    }

    int round = 30;
    int keylength = Integer.parseInt(args[0]);

    Key key = new Key(keylength);
    key.makeKey();

    // set PublicKey
    Paillier paillier = new Paillier(key.getPublic());

    // set SecretKey
    BigInteger[] secretKey = key.getSecret();
    paillier.p = secretKey[0];
    paillier.q = secretKey[1];
    paillier.d = secretKey[2];
    paillier.l = key.getLambda();

    System.out.println("--------------------------------------------------------------");
    System.out.println("|  Supler Paillier                                           |");
    System.out.println("--------------------------------------------------------------");

    System.out.println("Number of Round : " + round);
    System.out.println("Key length : " + keylength);


    System.out.println("--[Test1]-----------------------------------------------------");
    System.out.println("|  Create Cipher Text                                        |");
    System.out.println("--------------------------------------------------------------");


    SecureRandom rnd = new SecureRandom();
    Date d1, d2, d3,d4,d5;
    double enc = 0, dec = 0, times = 0;
    BigInteger mBef, mAft, c, c2;
    for(int i=0 ; i<round ; ++i){
      mBef = new BigInteger(paillier.d.bitLength(), rnd);

      d1 = new Date();
        c = paillier.enc(mBef);
      d2 = new Date();
//        c.multiply(c).mod(paillier.n2);
        c.modPow(c,paillier.n2);
      d3 = new Date();
        mAft = paillier.dec(c, paillier.d, paillier.n, paillier.n2);
      d4 = new Date();

      enc += d2.getTime() - d1.getTime();
      times += d3.getTime() - d2.getTime();
      dec += d4.getTime() - d3.getTime();
      System.out.print("Loop" + i + "\tenc = " + (d2.getTime() - d1.getTime()) + " [msec]");
      System.out.print("\t\ttimes = " + (d3.getTime() - d2.getTime()) + " [msec]");
      System.out.println("\t\tdec = " + (d4.getTime() - d3.getTime()) + " [msec]");
    }
    System.out.println("========================================================");
    System.out.println("total : enc = " + enc + " [msec]\t\ttimes = " + times + " [msec]\t\tdec = " + dec + " [msec]");
    System.out.println("average : enc = " + enc/round + " [msec]\t\ttimes = " + times/round + " [msec]\t\tdec = " + dec/round + " [msec]");

/*
    System.out.println("--[Test1]-----------------------------------------------------");
    String plainText1 = "Aoki Yoshiki";
    BigInteger plainNum1 = new BigInteger("1985");
    BigInteger plainNum2 = new BigInteger("25");

    String cipherText1 = paillier.enc("Aoki Yoshiki").toString();
    String cipherNum1 = paillier.enc(plainNum1).toString();
    String cipherNum2 = paillier.enc(plainNum2).toString();

    System.out.println("Decrypt cipherText1 : " + new String(paillier.dec(new BigInteger(cipherText1), paillier.d, paillier.n, paillier.n2).toByteArray()));
    System.out.println("Decrypt cipherNum1  : " + paillier.dec(new BigInteger(cipherNum1), paillier.d, paillier.n, paillier.n2));
    System.out.println("Decrypt cipherNum1*cipherNum2 : " + paillier.dec((new BigInteger(cipherNum1)).multiply(new BigInteger(cipherNum2)), paillier.d, paillier.n, paillier.n2));
    System.out.println("Decrypt cipherNum1^2 : " + paillier.dec((new BigInteger(cipherNum1)).pow(2), paillier.d, paillier.n, paillier.n2));
    System.out.println("--------------------------------------------------------------");






    System.out.println("--[Test2 - Cipher*Cipher Time test]-----------------------------------------------------");
    double time = 0.0;

    BigInteger m1 = new BigInteger(paillier.d.bitLength(), rnd);
    BigInteger m2 = new BigInteger(paillier.d.bitLength(), rnd);
    BigInteger c1 = paillier.enc(m1);
    BigInteger c2 = paillier.enc(m2);

    d1 = new Date();
    for(int i=0 ; i<(round*10) ; ++i){
      c1.multiply(c2);
    }
    d2 = new Date();

    time += d2.getTime() - d1.getTime();
    System.out.println("c*c = " + (d2.getTime() - d1.getTime()) + " [msec]");

    System.out.println("========================================================");
    System.out.println("total : c*c = " + time + " [msec]");
    System.out.println("average : c*c = " + time/(round*10) + " [msec]");



/*    System.out.println("--[Test3]-----------------------------------------------------");
    System.out.println("Plain Text   : " + args[1]);
    String cipherText = paillier.enc(args[1]).toString();
    System.out.println("Cipher Text  : " + cipherText);
    System.out.println("Decrypt Text : " + new String(paillier.dec(new BigInteger(cipherText)).toByteArray()));
    System.out.println("--------------------------------------------------------------");



    System.out.println("--[Test4]-----------------------------------------------------");
    Paillier paillier2 = new Paillier(paillier.n);
    System.out.println("Plain Text   : " + "550");
    cipherText = paillier.enc(new BigInteger("550")).toString();
    String cipherText2 = paillier2.enc(new BigInteger("550")).toString();
    System.out.println("Cipher Text  : " + cipherText);
    System.out.println("Cipher Text2 : " + cipherText2);
    System.out.println("Decrypt Text : " + paillier.dec(new BigInteger(cipherText)));
    System.out.println("Decrypt Text2: " + paillier.dec(new BigInteger(cipherText2)));
    System.out.println("--------------------------------------------------------------");
    //paillier.displayKey();
*/
  }
}
