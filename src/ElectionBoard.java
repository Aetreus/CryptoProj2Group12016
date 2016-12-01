import paillierp.Paillier;
import paillierp.key.KeyGen;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by james on 11/30/16.
 */
public class ElectionBoard {
  public final String candidates[]=new String[]{"Bernie","Hillary","Deez Nuts","Trump (The Hearthstone streamer)"};
  private final Paillier keyHolders[]=new Paillier[candidates.length];
  public static Random random;

  static {
    try {
      random = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      random=new Random();
    }
  }

  public ElectionBoard() throws NoSuchAlgorithmException {
    for (int i = 0; i < candidates.length; i++) {
      System.out.println("Make key for candidate "+i);
      keyHolders[i]=new Paillier(KeyGen.PaillierKey(128,random.nextLong()));
      System.out.println("Make key for candidate "+i);
    }
    System.out.println("Donezo");
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
    if(encryptedVote.length != candidates.length){
      throw new ElectionBoardError("This vote is for " + encryptedVote.length + " candidates but there are really "+candidates.length+" candidates.");
    }
    BigInteger blindSignedVote[]=new BigInteger[candidates.length];
    for (int i = 0; i < keyHolders.length; i++) {
      blindSignedVote[0]= keyHolders[i].encrypt(encryptedVote[i]);
    }
    return blindSignedVote;
  }

  private boolean voterHasAge(String voterName, int voterAge) {
    return true;
  }

  private boolean voterOfNameExists(String voterName) {
    return true;
  }
}
