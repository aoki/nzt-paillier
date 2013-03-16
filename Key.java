import java.math.BigInteger;
import java.util.Random;

public class Key{
	Random rnd;
	
	int bit;		// 鍵のbit長
	BigInteger p;	// Prime number
	BigInteger q;	// Prime number
	BigInteger n;	// p*q
	BigInteger l;	// Lamda = LCM((p-1)(q-1))
	BigInteger d;	// n^(-1) mod l
	
	public static void main(String[] argv){
		if(argv.length != 1){
			System.out.println("Usage : java MkKey bit");
			System.out.println("¥tbit : RSA key bit length (q & p)");
		}else{
			int bit = Integer.parseInt(argv[0]);
			Key key = new Key(bit);
			key.makeKey();
			BigInteger pkey = key.getPublic();
			BigInteger[] skey = key.getSecret();
			BigInteger l = key.getLambda();

			System.out.println("**************************");	
			System.out.println("* Secret Key information *");
			System.out.println("**************************");
			System.out.println("p = " + skey[0].toString(16));
			System.out.println("q = " + skey[1].toString(16));
			System.out.println("d = " + skey[2].toString(16));
			System.out.println();
			System.out.println("**************************");
			System.out.println("* Public Key Information *");
			System.out.println("**************************");
			System.out.println("n = " + pkey.toString(16));
		}
	}

	Key(){
	}

	Key(int bit){
		this.bit = bit;
		rnd = new Random();
	}

	public void makeKey(){
		p = this.makePrime(rnd, bit);
		q = this.makePrime(rnd, bit);
		n = p.multiply(q);
		l = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		d = n.modInverse(l);
	}

	public static BigInteger makePrime(Random rnd, int bit){
		BigInteger prime = new BigInteger(bit, 1000, rnd);
		while(!prime.isProbablePrime(1000)){
			prime = new BigInteger(bit, 1000, rnd);
		}
		return prime;
	}

	public BigInteger getPublic(){
		return n;
	}

	public BigInteger[] getSecret(){
		BigInteger[] skey = {p, q, d};
	
		return skey;
	}

	public BigInteger getLambda(){
		return l;
	}
}
