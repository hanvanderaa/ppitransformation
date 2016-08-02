package ppitransformation.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ppitransformation.markovmodels.HMM;

public class HMMSerialization {

	public static void serializeHMM(HMM hmm, String filepath) {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(filepath);
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(fout);
			oos.writeObject(hmm);
			oos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HMM deserializeHMM(String filepath) {
		Object o = null;
	      try
	      {
	         FileInputStream fileIn = new FileInputStream(filepath);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         o = in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	      }catch(ClassNotFoundException c)
	      {
	         c.printStackTrace();
	      }
	      return (HMM) o;
	}
}
