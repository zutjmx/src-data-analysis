package datan;

/**
* A class representing a sample.
* @author  Siegmund Brandt.
*/
public class Sample{
   double x[];
	double q;
	double xm, delxm, s2, dels2, s, dels;
   int n;
/**
* Creates a sample from array data.
*/
public Sample(double[] data){
      n = data.length;
		x = new double[n];
		for(int i = 0; i < n; i++){
		  x[i] = data[i];
		}
		computeMeanAndVariance();
   }

   private void computeMeanAndVariance(){ 
	   xm = 0.;
		for(int i = 0; i < n; i++){
		  xm = xm + x[i];
		}
		xm = xm / (double)n;
		q = 0.;
		for(int i = 0; i < n; i++){
		  q = q + (xm - x[i])*(xm - x[i]);
		}
		s2 = q / ((double)n - 1.);

   }
/**
* Returns trhe sample mean.
*/ 
	public double getMean(){
	   return xm;
	}
/**
* Returns the error of the sample mean.
*/
	public double getErrorOfMean(){
	   delxm = Math.sqrt(s2 / (double)n);
	   return delxm;
	}
/**
* Returns the sample variance.
*/
	public double getVariance(){
	   return s2;
	}
/**
* Returns the error of the sample variance.
*/
	public double getErrorOfVariance(){
	   dels2 = s2 * Math.sqrt(2. / ((double)n - 1.));
	   return dels2;
	}
/**
* Returns the standard deviation of the sample.
*/
	public double getStandardDeviation(){
	   s = Math.sqrt(s2);
	   return s;
	}
/**
* Returns the error of the standard deviation of the sample.
*/
	public double getErrorOfStandardDeviation(){
	   dels = Math.sqrt(s2 / (2. * ((double)n - 1.)));
	   return dels;
	}



}
