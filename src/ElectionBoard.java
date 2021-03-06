import paillierp.Paillier;
import paillierp.key.KeyGen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * Created by james on 11/30/16.
 */
public class ElectionBoard {
  private final Paillier keyHolders[];
  public final Paillier publicEncryption[];
  public final List<String> candidates;
  private final HashMap<String,Integer> voters=new HashMap<>();
  private final HashSet<String> votedVoters=new HashSet<>();
  public static Random random;
  
  public final BigInteger modulus;
  private final BigInteger privateExponent;
  public final BigInteger publicExponent;

  static {
    try {
      random = SecureRandom.getInstance("NativePRNGNonBlocking");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Issue creating a secure RNG, using a normal one instead.");
      random=new Random();
    }
  }

  public ElectionBoard(String voterFile, String candidatesFile) throws NoSuchAlgorithmException, IOException {
    try(BufferedReader fReader=new BufferedReader(new FileReader(voterFile))){//Read in voters and candidates from given files
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
    publicEncryption = new Paillier[candidates.size()];
    for (int i = 0; i < candidates.size(); i++) {//Generate Paillier keys for encryption
      keyHolders[i]=new Paillier(KeyGen.PaillierKey(256,random.nextLong()));
      publicEncryption[i] = new Paillier(keyHolders[i].getPublicKey());
      System.out.println("Made key for candidate #"+i);
    }
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");//Generate RSA key for blind signatures
    kpg.initialize(2048);
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
    if(!votedVoters.add(voterName)){
      throw new ElectionBoardError("This voter has attempted to vote multiple times.");
    }
    BigInteger blindSignedVote[]=new BigInteger[candidates.size()];
    for (int i = 0; i < keyHolders.length; i++) {
      blindSignedVote[i]= encryptedVote[i].modPow(privateExponent,modulus);
    }
    return blindSignedVote;
  }
  
  public BigInteger[] decrypt(BigInteger[] vote, CountingAuthority.CAToken t) throws ElectionBoardError{
    if(t == null){//Checks that the CA token is valid and came from an instance of the CA class
      throw new ElectionBoardError("Invalid attempt to call decryption function");
    }
    BigInteger[] decryption = new BigInteger[vote.length];
    for(int i = 0; i < vote.length; i++){
      decryption[i] = keyHolders[i].decrypt(vote[i]);
    }
    return decryption;
  }

  private boolean voterHasAge(String voterName, int voterAge) {
    return voterAge==voters.get(voterName);
  }

  private boolean voterOfNameExists(String voterName) {
    return voters.containsKey(voterName);
  }
}
