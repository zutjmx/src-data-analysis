package datan;

/**
* A class determining the asymmetric errors
* for a set of parameters determined by minimization.
* @author  Siegmund Brandt.
*/
public final class MinAsy {
   static double EPS = 1.E-4, BIG = 1.E20;
   static int MAXSTEP = 1000;
   int[] list;
   double fcont;
   DatanVector x, x0;
   DatanMatrix cx;
   DatanUserFunction muf;
   int nstep = 20;
   boolean converged;
   
/**
* @param x0 position of minimum.
* @param cx covariance matrix at x0.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinAsy(DatanVector x0, int[] list, DatanMatrix cx, DatanUserFunction muf) {
      this.x0 = new DatanVector(x0);
      this.cx = new DatanMatrix(cx);
      this.muf = muf;
      this.list = list;
   }

/**
* @param x0 position of minimum (all elements of list are set to 1).
* @param cx covariance matrix at x0.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinAsy(DatanVector x0, DatanMatrix cx, DatanUserFunction muf) {
      this.x0 = new DatanVector(x0);
      this.cx = new DatanMatrix(cx);
      this.muf = muf;
      int[] list = new int[x0.getNumberOfElements()];
      for(int i = 0; i < x0.getNumberOfElements(); i++){
         list[i] = 1;
      }
      this.list = list;
   }
 
/**
* @return  the asymmetric errors. Element [i][0] is negativer deviation, element [i][1] is positive deviation
* of the (non-fixed) parameter x[i].
* @param fcont value of minimum function at postion of errors (right/hand side of eq. (10.17.9).
*/
   public double[][] getAsymmetricErrors(double fcont){
   DatanVector x, xr;
   int n = x0.getNumberOfElements();
   int nred = list.length;
   double[] dx = new double[nred];
   double[][] dxasy = new double[nred][2];
   double dxmin = BIG;
   double del, signum, xbig, xivar, xsmall;
   boolean enclosed;
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
               x = new DatanVector(x0);
               x.setElement(ivar, xivar);
               MinSim ms = new MinSim(x, list, 1000, 0., edge, muf);
               double r1 = ms.getMinimum();
               converged = ms.hasConverged();
//               if(! converged) break outerloop;
               xr = ms.getMinPosition();
// test for convergence
               if(Math.abs(r1 - fcont) < EPS){
                  if(isign < 0) dxasy[ivar][0] = x0.getElement(ivar) - xr.getElement(ivar);
                  else dxasy[ivar][1] =  - x0.getElement(ivar) + xr.getElement(ivar);
                  break innerloop;
               }
               else{
                  if(! enclosed){
// zero was not yet enclosed in last step
                     if(r1 > fcont){
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
                     if(r1 > fcont) xbig = xr.getElement(ivar);
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
