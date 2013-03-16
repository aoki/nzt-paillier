import java.math.BigInteger;
import java.util.Random;
import java.util.Date;

public class Paillier{
    BigInteger n, c, r;
    Random rnd;
    public static void main(String[] argv){
  int round = 1000;
  Read pkf = new Read("key.pk");
  Read skf = new Read("key.sk");
  String[] pk = pkf.readAll();
  String[] sk = skf.readAll();
  pkf.close();
  skf.close();

  BigInteger n = new BigInteger(pk[0], 16);

  BigInteger p = new BigInteger(sk[0], 16);
  BigInteger q = new BigInteger(sk[1], 16);
  BigInteger d = new BigInteger(sk[2], 16);
  BigInteger l = new BigInteger(sk[3], 16);
  /*
  BigInteger n = new BigInteger("35");
  BigInteger p = new BigInteger("7");
  BigInteger q = new BigInteger("5");
  BigInteger d = new BigInteger("11");
  BigInteger l = new BigInteger("24");
  */

  Paillier pai = new Paillier(n);
  Random rnd = new Random();
  Date d1, d2, d3;
  double enc = 0, dec = 0;
  for(int i=0 ; i<round ; ++i){
      BigInteger m = new BigInteger(d.bitLength(), rnd);
      //BigInteger m = new BigInteger(argv[0], 16);;
      d1 = new Date();
      BigInteger c = pai.enc(m);
      d2 = new Date();
      BigInteger m2 = pai.dec(c, d, n, l);
      d3 = new Date();
      enc += d2.getTime() - d1.getTime();
      dec += d3.getTime() - d2.getTime();
      System.out.print("enc = " + (d2.getTime() - d1.getTime()) + " [msec]");
      System.out.println("\t\tdec = " + (d3.getTime() - d2.getTime()) + " [msec]");
  }
  System.out.println("========================================================");
  System.out.println("total : enc = " + enc + " [msec]\t\tdec = " + dec + " [msec]");
  System.out.println("average : enc = " + enc/round + " [msec]\t\tdec = " + dec/round + " [msec]");
  //System.out.println(m.toString(16));
  //System.out.println(m2.toString(16));
    }

    Paillier(){
    }

    Paillier(BigInteger n){
  this.n = n;
  this.rnd = new Random();
    }

    public BigInteger enc(String mess){
  BigInteger m = new BigInteger(mess.getBytes());
  return this.enc(m);
    }

    public BigInteger enc(BigInteger mess){
  //c = mess.modPow(e, n);
  r = new BigInteger(n.bitLength(), rnd);
  r = r.mod(n);

  c = (BigInteger.ONE.add(n)).modPow(mess, n.pow(2)).multiply(r.modPow(n, n.pow(2))).mod(n.pow(2));
  return c;
    }

    public static BigInteger dec(BigInteger c, BigInteger d, BigInteger n, BigInteger l){
  BigInteger mA = c.mod(n);
  BigInteger r = mA.modPow(d, n);
  BigInteger mB = (c.multiply((r.modPow(n, n.pow(2))).modInverse(n.pow(2)))).mod(n.pow(2));
  BigInteger m = ((mB.subtract(BigInteger.ONE)).divide(n)).mod(n);
  return m;
    }
}
