package datan;

import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class performing time series analysis
* @author  Siegmund Brandt.
*/

public class TimeSeries{
   double[] coneta, eta, tt, y;
   DatanMatrix a, ata1, ata1at, etai, etatmp, seta2, sy2, tmat, x, ymat, ytmp;
   double p, pprime, talpha;
   int iadd, is, j, k, k21, l, l1, n, nf;
   
/**
* @param y data
* @param k parameter giving the length 2 * k + 1 of the averaging interval
* @param l oder of the averaging polynomial
* @param p probability defining confidence limits
*/
   public TimeSeries(double[] y, int k, int l, double p){
      this.y = y;
      this.k = k;
      this.l = l;
      this.p = p;
      n = y.length;
      eta = new double[n + 2 * k];
      coneta = new double[n + 2 * k];
// quantile of Student's distribution
      pprime = 0.5 * (p + 1.);
      nf = 2 * k - l;
      talpha = StatFunct.quantileStudent(pprime, nf);
// compute matrices depending on k and l
      k21 = 2 * k + 1;
      l1 = l + 1;
      a = new DatanMatrix(k21, l1);
      for(int i = 0; i < k21; i++){
         for(int j = 0; j < l1; j++){
            if(j == 0) a.setElement(i, j, -1.);
            else a.setElement(i, j, a.getElement(i, j - 1) * (double)(i - k));
         }
      }
      ata1 = a.multiplyTransposedWith(a);
      ata1.choleskyInversion();
      ata1at = ata1.multiplyWithTransposed(a);
      ata1at = ata1at.multiply(-1.);
// moving averages and confidence limits for inner part
      ymat = new DatanMatrix(y);
      for(int i = 2 * k; i < n; i ++){
         ytmp = ymat.getSubmatrix(k21, 1, i - 2 * k, 0);
         x = ata1at.multiply(ytmp);
         eta[i] = x.getElement(0, 0);
         etatmp = a.multiply(x);
         etatmp = etatmp.add(ytmp);
         sy2 = etatmp.multiplyTransposedWith(etatmp);
         double s = sy2.getElement(0, 0) / (double) nf;
         double a0 = Math.sqrt(Math.abs(ata1.getElement(0, 0)));
         coneta[i] = a0 * Math.sqrt(s) * talpha;
// moving averages and confidence limits for end sections
         if(i == 2 * k || i == n - 1){
            tt = new double[l + 1];
            if(i == 2 * k){
               iadd = 2 * k;
               is = -1;
            }
            else{
               iadd = n - 1;
               is = 1;
            }
            for(int i1 = 1; i1 <= 2 * k; i1++){
               j = is * i1;
               for(int i2 = 0; i2 < l + 1; i2++){
                  for(int i3 = 0; i3 <= i2; i3++){
                     if(i3 == 0) tt[i2] = 1.;
                     else tt[i2] = tt[i2] * (double)j;
                  }
               }
               tmat = new DatanMatrix(tt);
               seta2 = tmat.multiplyTransposedWith(ata1.multiply(tmat));
               double se2 = s * seta2.getElement(0, 0);
               etai = tmat.multiplyTransposedWith(x);
               eta[iadd + j] = etai.getElement(0,0);
               coneta[iadd + j] = Math.sqrt(Math.abs(se2)) * talpha;
            }
         }
      }
   }

   public double[] getMovingAverages(){
      return eta;
   }

   public double[] getConfidenceLimits(){
      return coneta;
   }



}
