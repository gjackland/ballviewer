package uk.ac.ed.ph.ballviewer;

import java.io.*;
import java.lang.*;
import java.awt.*;
import java.util.*;

// converts text input to binary //
class Converter {

	static int i;		
	static TextField tf = new TextField();

	public static void main(String args[]) {
		final String dir = "/scratch/crystals/";
		final String indir = dir + "2/";
		BufferedInputStream inputx;
		BufferedInputStream inputy;
		BufferedInputStream inputz;
		DataOutputStream output;
		
		Frame fr = new Frame("Progress");
		fr.add(tf); fr.pack(); fr.setVisible(true);
	
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() { public void run() {update();} },5000,5000);
		
		try {
			inputx = new BufferedInputStream(new FileInputStream(indir+"rrelx.def"));
			inputy = new BufferedInputStream(new FileInputStream(indir+"rrely.def"));
			inputz = new BufferedInputStream(new FileInputStream(indir+"rrelz.def"));
			output = new DataOutputStream(new FileOutputStream(dir+"rrel2.dat"));
		
			int total = readInt(inputx);
			System.out.println(total);
			output.writeInt(total);                     //writes 4 bytes
			for (i=0;i<6;i++) { readInt(inputx); }
		
			for (i=0;i<total;i++) {
				float x = readFloat(inputx);
				float y = readFloat(inputy);
				float z = readFloat(inputz);
				output.writeFloat(x);           // each float is 4 bytes
				output.writeFloat(y);
				output.writeFloat(z);	
			}
			inputx.close();
			inputy.close();
			inputz.close();
			output.close();
		}
		catch (Exception e) {
			System.out.println(e.toString());
			System.exit(1);
		}
		fr.dispose();
	}
	static void update () {
		tf.setText(""+i);
	}
	static int readInt(BufferedInputStream inp) {  // there are 10 characters for every int
		byte[] b = new byte[10];
		try { inp.read(b,0,b.length); } catch (Exception e) {}
		return Integer.parseInt(new String(b).trim() );
	}
	static float readFloat(BufferedInputStream inp) {  // and 12 for every Float (including 
		byte[] b = new byte[12];
		try { inp.read(b,0,b.length); } catch (Exception e) {}
		return Float.parseFloat(new String(b).trim() );   // spaces which are removed by trim() )
	}
}		