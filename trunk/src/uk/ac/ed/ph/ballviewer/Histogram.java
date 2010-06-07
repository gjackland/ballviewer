package uk.ac.ed.ph.ballviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;


/** 
 * A histogram that can be built by the user and displayed if necessary. <p>
 * <code>LightHistogram</code> is a class which has the same functionality, except for 
 * the graphical component; it can't be displayed. The data in a <code>LightHistogram</code>
 * can be displayed by adding it using {@link #addData(LightHistogram)}. <p>
 * <code>RadialHistogram</code> and <code>AngularHistogram</code> are subclasses of 
 * <code>Histogram</code> that each set an appropriate title, size and marks for the type
 * of histogram.
 */
public class Histogram extends Frame implements ActionListener {  
	String name;
	BufferedCanvas canv;
	Graphics gr;
	int W,H=320;

	int freq[];         //array of frequencies
	long total;         //total no. of entries 
	double min;         //min x value
	double max;         //max x value
	double range;       //=(max-min)
	double step;        //=(range/freq.length) ie the width of each bar: same
	
	/**
	 * Creates a new histogram, with bars of width <code>stp</code>. 
	 * It will cover the range <code>mn</code> to <code>mx</code> and have the name <code>n</code>.
	 */
	public Histogram (String n, double mn, double mx, double stp) {
		name = n;		
		min = mn;
		max = mx;
		step = stp;
		range = max-min;
		int nbars = (int) Math.ceil(range/step);
		freq = new int[nbars];
		resetFreqs();
	}
	/** Creates a new histogram, with <code>nbars</code> bars. */
	public Histogram (String n, double mn, double mx, int nbars) {
		name = n;
		min = mn;
		max = mx;
		freq = new int[nbars];
		range = max-min;
		step = range/nbars;
		resetFreqs();
	}
   public void actionPerformed(ActionEvent e) {}    // belongs to Actionlistener Interface 

	/** Clears the histogram by zeroing the frequency on every bar. */
	public void resetFreqs() {
		for (int i=0;i<freq.length;i++) freq[i]=0;
		total=0;
	}
	/** Accumulates the data from <code>h</code> into this histogram. */
	public void addData(Histogram h) {
		for (int i=0;i<freq.length;i++) { freq[i]+=h.freq[i]; total += h.freq[i]; }
	}	
	/** Accumulates the data from <code>h</code> into this histogram. */
	public void addData(LightHistogram h) {
		for (int i=0;i<freq.length;i++) { freq[i]+=h.freq[i]; total += h.freq[i]; }
	}	
		
	/** Increments the frequency for the bar into which <code>x</code> falls. */
	public void add(double x) {
		int i = (int)Math.floor(((x-min)/step) +0.5);
		freq[i]++;
		total++;
	}
	/** 
	 * Returns the sum of all the bars within the range <code>x1</code> through 
	 * <code>x2</code> inclusive.
	 */
	public int sum(double x1,double x2) {
		int i1 = (int)Math.floor(((x1-min)/step) +0.5);
		int i2 = (int)Math.floor(((x2-min)/step) +0.5);
		int s=0;
		for (int i=i1;i<=i2;i++) s+=freq[i];
		return s;
	}
	/** Returns the weighted sum of the bars over the given range, focussed on <code>peak</code>. */
	public double weightedSum(double x1, double peak, double x2) { 
		int i1 = (int)Math.floor(((x1-min)/step) +0.5);
		int ip = (int)Math.floor(((peak-min)/step) +0.5); // weighted by a triangle
		int i2 = (int)Math.floor(((x2-min)/step) +0.5);
		double ws=0;
		for (int i=i1;i<ip;i++) ws+=freq[i]*(i-i1)/(double)(ip-i1);
		for (int i=ip;i<=i2;i++) ws+=freq[i]*(i2-i)/(double)(i2-ip);
		return ws;
	}	
	/** Returns an approximate mean value of x over the given range. */
	public double mean(double x1,double x2) {   // ie the mean x position of the data in this range
		int i1 = (int)Math.floor(((x1-min)/step) +0.5);
		int i2 = (int)Math.floor(((x2-min)/step) +0.5);
		double m=0;
		for (int i=i1;i<=i2;i++) m+=freq[i]*(min+i*step);
		m/=sum(x1,x2);
		return m;
	}

	/** Displays the histogram. */
	public void draw() {
		this.setTitle("Histogram: "+name);
		this.setBackground(Color.blue);
		this.addWindowListener(new WindowAdapter () { // this code allows window to be closed //
			public void windowClosing(WindowEvent e) { e.getWindow().dispose(); System.exit(0); }
		});
		 		
		canv = new BufferedCanvas();
		canv.setBackground(Color.white);
		canv.setSize(W,H);
		canv.setVisible(true);
		this.add(canv);
		this.pack();
		this.setVisible(true);
		canv.buffer=canv.createImage(W,H);
		gr = canv.buffer.getGraphics();
		
		int i,j;
		final int w =2;
		double h = (total>1000)? (6000.0/total):10.0;
		for (i=0;i<freq.length;i++) {
			gr.setColor(Color.black);
			gr.fillRect(i*w,0,w,(int)(freq[i]*h));    // this draws the bar
			gr.setColor(Color.gray);
			for (j=10;j<freq[i]*h;j+=10) gr.drawLine(i*w,j,i*w+w,j);  // puts ticks on bar
		}
		for (i=0;i<W;i+=10) gr.drawLine(i,0,i,2);         // the x axis tick
		canv.update(canv.getGraphics());
	}		

}

class BufferedCanvas extends Canvas {
	Image buffer;
	public void update(Graphics g) { paint(g); }
   public void paint(Graphics g) {                    // this won't work until frame is displayed
   	if (buffer!=null) g.drawImage(buffer,0,0,this);             // so need to check it is possible
   }
}
	