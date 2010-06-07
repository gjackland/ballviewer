package uk.ac.ed.ph.ballviewer;

public class LightHistogram {  // has no graphics component

	int freq[];         //array of frequencies
	long total;         //total no. of entries 
	double min;         //min x value
	double max;         //max x value
	double range;       //=(max-min)
	double step;        //=(range/freq.length) ie the width of each bar: same
	
	public LightHistogram ( double mn, double mx, double stp) {	
		min = mn;
		max = mx;
		step = stp;
		range = max-min;
		int nbars = (int) Math.ceil(range/step);
		freq = new int[nbars];
		resetFreqs();
	}
	public LightHistogram ( double mn, double mx, int nbars) {
		min = mn;
		max = mx;
		freq = new int[nbars];
		range = max-min;
		step = range/nbars;
		resetFreqs();
	}
	
	public void resetFreqs() {
		for (int i=0;i<freq.length;i++) freq[i]=0;
		total=0;
	}
	public void addData(Histogram h) {
		for (int i=0;i<freq.length;i++) { freq[i]+=h.freq[i]; total += h.freq[i]; }
	}	
		
	
	public void add(double x) {
		int i = (int)Math.floor(((x-min)/step) +0.5);
		freq[i]++;
		total++;
	}
	public int sum(double x1,double x2) {
		int i1 = (int)Math.floor(((x1-min)/step) +0.5);
		int i2 = (int)Math.floor(((x2-min)/step) +0.5);
		int s=0;
		for (int i=i1;i<=i2;i++) s+=freq[i];
		return s;
	}
	public double weightedSum(double x1, double peak, double x2) { 
		int i1 = (int)Math.floor(((x1-min)/step) +0.5);
		int ip = (int)Math.floor(((peak-min)/step) +0.5); // weighted by a triangle
		int i2 = (int)Math.floor(((x2-min)/step) +0.5);
		double ws=0;
		for (int i=i1;i<ip;i++) ws+=freq[i]*(i-i1)/(double)(ip-i1);
		for (int i=ip;i<=i2;i++) ws+=freq[i]*(i2-i)/(double)(i2-ip);
		return ws;
	}	
	public double mean(double x1,double x2) {   // ie the mean x position of the data in this range
		int i1 = (int)Math.floor(((x1-min)/step) +0.5);
		int i2 = (int)Math.floor(((x2-min)/step) +0.5);
		double m=0;
		for (int i=i1;i<=i2;i++) m+=freq[i]*(min+i*step);
		m/=sum(x1,x2);
		return m;
	}
}