import paillierp.Paillier;
import paillierp.key.KeyGen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyPair;
import java.util.*;

/**
 * Created by james on 11/30/16.
 */
public class ElectionBoard {
  private final Paillier keyHolders[];
  public final Pallier publicEncryption[];
  public final List<String> candidates;
  private final HashMap<String,Integer> voters=new HashMap<>();
  public static Random random;
  
  public final BigInteger modulus;
  private final BigInteger privateExponent;
  public final BigInteger publicExponent;

  static {
    try {
      random = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      random=new Random();
    }
  }

  public ElectionBoard(String voterFile, String candidatesFile) throws NoSuchAlgorithmException, IOException {
    try(BufferedReader fReader=new BufferedReader(new FileReader(voterFile))){
      String line;
      while((line=fReader.readLine())!=null&&!(line=line.trim()).isEmpty()){
        String[] split = line.split("\\s+");
        if(split.length!=2){
          throw new IOException("The voter file had line \""+line+"\" which lacked whitespace between voter name and age.");
        }
        voters.put(split[0],Integer.parseInt(split[1]));
      }
    }
    ArrayList<String> tmp=new ArrayList<>();
    try(BufferedReader fReader=new BufferedReader(new FileReader(candidatesFile))){
      String line;
      while((line=fReader.readLine())!=null&&!(line=line.trim()).isEmpty()&&!tmp.contains(line)){
        tmp.add(line);
      }
    }
    candidates=Collections.unmodifiableList(tmp);
    keyHolders=new Paillier[candidates.size()];
    for (int i = 0; i < candidates.size(); i++) {
      keyHolders[i]=new Paillier(KeyGen.PaillierKey(128,random.nextLong()));
      publicEncryption[i] = new Paillier(keyHolders[i].getPublicKey());
      System.out.println("Made key for candidate #"+i);
    }
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(128);
    KeyPair kp = kpg.genKeyPair();
    RSAPublicKey rpuk = (RSAPublicKey)kp.getPublic();
    RSAPrivateKey rprk = (RSAPrivateKey)kp.getPrivate();
    modulus = rpuk.getModulus();
    publicExponent = rpuk.getPublicExponent();
    privateExponent = rprk.getPrivateExponent();
  }
  public BigInteger[] blindSignVote(String voterName, int voterAge, BigInteger[] encryptedVote) throws ElectionBoardError{
    if(!voterOfNameExists(voterName)){
      throw new ElectionBoardError("There is no voter of name \""+voterName+"\" registered.");
    }
    if(voterAge<18){
      throw new ElectionBoardError("You must be 18 or older to vote.");
    }
    if(!voterHasAge(voterName,voterAge)){
      throw new ElectionBoardError("There is no voter of name \"" + voterName + "\" of age "+voterAge+".");
    }
    if(encryptedVote.length != candidates.size()){
      throw new ElectionBoardError("This vote is for " + encryptedVote.length + " candidates but there are really "+candidates.size()+" candidates.");
    }
    BigInteger blindSignedVote[]=new BigInteger[candidates.size()];
    for (int i = 0; i < keyHolders.length; i++) {
      blindSignedVote[i]= encryptedVote[i].modPow(privateExponent,modulus);
    }
    return blindSignedVote;
  }

  private boolean voterHasAge(String voterName, int voterAge) {
    return voterAge==voters.get(voterName);
  }

  private boolean voterOfNameExists(String voterName) {
    return voters.containsKey(voterName);
  }
}
