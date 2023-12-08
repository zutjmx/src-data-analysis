package datan;

/**
* A class determining the asymmetric errors
* for a set of parameters determined by the least square method for the general case using class LsqGen.
* @author  Siegmund Brandt.
*/
public final class LsqAsg {
   static double EPS = 1.E-4, BIG = 1.E20;
   static int MAXSTEP = 1000;
   int[] list;
   int m;
   double mf, m1, mtarg, w;
   DatanVector y, ysav, x, x0;
   DatanMatrix cx, cy;
   DatanUserFunction uf;
   int nstep = 20;
   boolean converged;
   
/**
* @param y vector of measurements.
* @param cy covariance matrix of measurements.
* @param x0 position of minimum.
* @param cx covariance matrix at x0.
* @param mf value of minimum function.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param m number of constaint equations.
* @param uf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public LsqAsg(DatanVector y, DatanMatrix cy,  DatanVector x0, int[] list, DatanMatrix cx,
         double mf, int m, DatanUserFunction uf) {
      this.y = y;
      this.cy = cy;
      this.x0 = new DatanVector(x0);
      this.cx = new DatanMatrix(cx);
      this.mf = mf;
      this.w = w;
      this.uf = uf;
      this.m = m;
      this.list = list;
   }

/**
* @param y vector of measurements.
* @param cy covariance matrix of measurements.
* @param x0 position of minimum (all elements of list are set to 1).
* @param cx covariance matrix at x0.
* @param mf value of minimum function.
* @param m number of constaint equations.
* @param uf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public LsqAsg(DatanVector y, DatanMatrix cy,  DatanVector x0, DatanMatrix cx,
         double mf, int m, DatanUserFunction uf) {
      this.y = y;
      this.cy = cy;
      this.x0 = new DatanVector(x0);
      this.cx = new DatanMatrix(cx);
      this.mf = mf;
      this.w = w;
      this.uf = uf;
      this.m = m;
      int[] list = new int[x0.getNumberOfElements()];
      for(int i = 0; i < x0.getNumberOfElements(); i++){
         list[i] = 1;
      }
      this.list = list;
   }
 
/**
* @return  the asymmetric errors. Element [i][0] is negativer deviation, element [i][1] is positive deviation
* of the (non-fixed) parameter x[i].
* @param w probalility w for which confidence limits are computed. If w <= 0, then assymetry arrors are computed.
*/
   public double[][] getAsymmetricErrors(double w){
   DatanVector x, xr;
   int n = x0.getNumberOfElements();
   int nred = 0;
   for(int i = 0; i < n; i++){
      if(list[i] != 0) nred++;
   }
   double[] dx = new double[nred];
   double[][] dxasy = new double[nred][2];
   double dxmin = BIG;
   double del, signum, xbig, xivar, xsmall;
   boolean enclosed;
   ysav = new DatanVector(y);
// set target value of minimum function
   if(w <= 0.) mtarg = mf + 1.;
   else mtarg = mf + StatFunct.quantileChiSquared(w, nred);
   for(int i = 0; i < nred; i++){
      dx[i] = Math.sqrt(cx.getElement(i, i));
      dxmin = Math.min(dxmin, dx[i]);
      dxasy[i][0] = 0.;
      dxasy[i][1] = 0.;
   }
   dxmin = 0.1 * dxmin;
   converged = true;
   outerloop:
   for(int ivar = 0; ivar < n; ivar++){
      if(list[ivar] != 0){
// fix varaible ivar
         list[ivar] = 0;
         for(int isign = -1; isign <= 1; isign = isign +2){
            signum = (double)isign;
            del = dx[ivar];
            double edge = 0.1 * del;
            enclosed = false;
// set xsmall to x at minimum position
            xsmall = x0.getElement(ivar);
            xivar = xsmall + signum * del;
            xbig = xivar;
            innerloop:
            for(int i = 0; i < nstep; i++){            
               y = new DatanVector(ysav);
               x = new DatanVector(x0);
               x.setElement(ivar, xivar);
               LsqGen lg = new LsqGen(y, cy, x, list, m, uf);
               converged = lg.hasConverged();
//               if(! converged) break outerloop;
               xr = lg.getResult();
               m1 = lg.getChiSquare();
// test for convergence
               if(Math.abs(m1 - mtarg) < EPS){
                  if(isign < 0) dxasy[ivar][0] = x0.getElement(ivar) - xr.getElement(ivar);
                  else dxasy[ivar][1] =  - x0.getElement(ivar) + xr.getElement(ivar);
                  break innerloop;
               }
               else{
                  if(! enclosed){
// zero was not yet enclosed in last step
                     if(m1 > mtarg){
// zero is now enclosed, perform first interval halving
                        enclosed = true;
                        xbig = xr.getElement(ivar);
                        xivar = 0.5 * (xbig + xsmall);
                     }
                     else{
// zero not enclosed, widen range
                        del = 2. * del;
                        xivar = xsmall + signum * del;
                     }
                  }
                  else{
// continue interval halving
                     if(m1 > mtarg) xbig = xr.getElement(ivar);
                     else xsmall = xr.getElement(ivar);
                     xivar = 0.5 * (xbig + xsmall);
                  }
               }
            }
         }
// unfix variable ivar
         list[ivar] = 1;         
      }
      }
      return dxasy;
   }

/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }
   
}
