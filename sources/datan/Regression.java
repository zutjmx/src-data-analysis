package datan;

/**
* A class performing polynomial regression.
* @author  Siegmund Brandt.
*/
public class Regression{
   double alpha, beta, gamma1, gamma2, s, sg, tbar;
   double[] chi2, deltay, g, t,  x, y;
	double[][] a, b;
   int n, nr;
   boolean errorsknown;

/**
* Polynomial regression with known errors.
* @param t values of the controlled variable
* @param y measurements
* @param deltay measurement errors
* @param nr number of parameters in the polynomial 
*/
public Regression(double[] t, double[] y, double[] deltay, int nr){
      n = t.length;
      this.nr = nr;
      this.t = t;
      this.y = y;
      this.deltay = deltay;
      errorsknown = true;
      compute();
}

/**
* Polynomial regression with unknown errors, assumed all equal.
* @param t values of the controlled variable
* @param y measurements
* @param nr number of parameters in the polynomial 
*/
public Regression(double[] t, double[] y, int nr){
      n = t.length;
      this.nr = nr;
      this.t = t;
      this.y = y;
      this.deltay = deltay;
      errorsknown = false;
      deltay = new double[n];
      for(int i = 0; i < n ; i++){
         deltay[i] = 1.;
      }
      compute();
}

private void compute(){
// compute weights g and weighted mean tbar
      sg = 0.;
      tbar = 0.;
      g = new double[n];
      for(int i = 0; i < n; i++){
         g[i] = 1. / Math.pow(deltay[i], 2.);
         sg = sg + g[i];
         tbar = tbar + g[i] * t[i];
      }
      tbar = tbar / sg;
// compute b and a for nr = 1
      b = new double[nr][nr];
      a = new double[n][nr];
      b[0][0] = 1. / Math.sqrt(sg);
      for(int i = 0; i < n; i++){
         a[i][0] = b[0][0];
      }
// compute b and a for nr >= 2
      if(nr >= 2){
         s = 0.;
         for(int i = 0; i < n; i++){
            s = s + g[i] * Math.pow(t[i] - tbar, 2.);
         }
         b[1][1] = 1. / Math.sqrt(s);
         b[1][0] = - b[1][1] * tbar;
         for(int i = 0; i < n; i++){
            a[i][1] = b[1][0] + b[1][1] * t[i];
         }
      }      
// compute b and a for nr > 2
      if(nr > 2){
         for(int j = 2; j < nr; j++){
            alpha = 0.;
            beta = 0.;
            gamma2 = 0.;
            for(int i = 0; i < n; i++){
               alpha = alpha + g[i] * t[i] * Math.pow(a[i][j-1],2.);
               beta = beta + g[i] * t[i] * a[i][j-1] * a[i][j-2];
            }
            for(int i = 0; i < n; i++){
               gamma2 = gamma2 + g[i] * Math.pow((t[i] - alpha) * a[i][j-1] - beta * a[i][j-2], 2.);
            }
            gamma1 = 1. / Math.sqrt(gamma2);
            b[j][0] = gamma1 *( - alpha * b[j-1][0] - beta * b[j-2][0]);
            if(j >= 3){
               for(int k = 1; k <= j - 2; k++){
                  b[j][k] = gamma1 *(b[j-1][k-1] - alpha * b[j-1][k] - beta * b[j-2][k]);
               }
            }
            b[j][j-1] = gamma1 * (b[j-1][j-2] - alpha * b[j-1][j-1]);
            b[j][j] = gamma1 * b[j-1][j-1];
            for(int i = 0; i < n; i++){
               a[i][j] = b[j][0];
               for(int k = 1; k <= j; k++){
               a[i][j] = a[i][j] + b[j][k] * Math.pow(t[i], (double)(k));
               }
            }
         }
      }
// compute x and chi2
      x = new double[nr];
      chi2 = new double[nr];
      for(int j = 0; j < nr; j++){
         for(int i = 0; i < n; i++){
            x[j] = x[j] + g[i] * a[i][j] * y[i];
         }
         for(int i = 0; i < n; i++){
            s = 0.;
            for(int k =0; k <= j; k++){
               s = s + a[i][k] * x[k];
            }
            chi2[j] = chi2[j] + g[i] * Math.pow(y[i] - s, 2.);
         }
      }
   }

/**
* @return vector of nr chisquare values
*/
   public DatanVector getChiSquare(){
      return new DatanVector(chi2);
   }

/**
* @return vector of nr parameters
*/   
   public DatanVector getParameters(){
      return new DatanVector(x);
   }

/**
* @return ordinate value eta on regression line for abscissa value tt of controlled variable
*/
   public double regressionLine(double tt){
      double eta = 0.;
      for(int j = 0; j < nr; j++){
         double d = b[j][0];
         if(j > 0){
            for(int k = 1; k <= j; k++){
               d = d + b[j][k] * Math.pow(tt, (double)k);
            }
         }
         eta = eta + x[j] * d;
      }
      return eta;
   }

/**
* @return confidence limit for a given value tt of the controlled variable and a given probability p 
*/
   public double confidenceLimit(double tt, double p){
      double cl, s, fact, pprime;
      int ndf = n - nr;  // number of degrees of freedom
      if(p <=0. || p >=1. || ndf <= 0){
// wrong input, should not happen!
         cl = 0.;
      }
      else{
         pprime = 0.5 * (p + 1.);
         if(errorsknown){
            s = 1.;
            fact = StatFunct.quantileStandardNormal(pprime);
         }
         else{
            s = Math.sqrt(chi2[nr - 1] / (double)ndf);
            fact = StatFunct.quantileStudent(pprime, ndf);
         }
         cl = 0.;
         for(int j = 0; j < nr; j++){
            double d = b[j][0];
            if(j > 0){
               for(int k = 1; k <= j; k++){
                  d = d + b[j][k] * Math.pow(tt, (double)k);
               }
            }
            cl = cl + d * d;
         }
         cl = fact * s * Math.sqrt(cl);
      }
      return cl;
   }



}
