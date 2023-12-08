package datan;

/**
* A class determining the minimum of a function along a straight line in n-dimensional space
* according to the method of the golden section
* @author  Siegmund Brandt.
*/
public final class MinGold {
   static double Q = 1.618034, EPSDEF = 1.E-8, TT = 1.E-15;
   static int MAXSTP = 1000;
   DatanUserFunction muf;
   double a, b, c, x, y, s, eps;
   int nstep, nst, istep;
   DatanVector x0, xdir;
   FunctionOnLine fol;
   boolean converged;

/**
* @param x0 point in n-space
* @param xdir direction in n-space. Straight line is given by x = x0 + a * xdir, where a is a running scalar variable.
* @param xa value corresponding to a point defining one end the interval comprising the minimum.
* @param xb value corresponding to a point defining the other end the interval comprising the minimum.
* @param xc value corresponding to a point somewhere n the interval.
* @param nstep maximal step number; if a value <= 0 is used, then 1000 is taken instead.
* @param epsilon accuracy; if a value <= 0. is used, then 1,E-8 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinGold(double xa, double xb, double xc, DatanVector x0, DatanVector xdir, int nstep, double epsilon, DatanUserFunction muf) {
      a = xa;
      b = xb;
      c = xc;
      eps = epsilon;
      this.x0 = x0;
      this.xdir = xdir;
      this.nstep = nstep;
      this.muf = muf;
      if(eps <= 0.)eps = EPSDEF;
      fol = new FunctionOnLine(x0, xdir, this.muf);
      s = fol.getFunctionOnLine(b);
      istep = 0;
      if(nst <= 0) nst = MAXSTP;
      while(Math.abs(b - a) > eps * Math.abs(a) + TT && istep < nst){
         istep++;
         if(Math.abs(b - a) < Math.abs(c - b)){
            x = a + Q * (b - a);
            y = fol.getFunctionOnLine(x);
            if(y < s){
               c = b; b = x; s = y;
            }
            else{
               a = x;
            }
         }
         else{
            x = b + Q * (c - b);
            y = fol.getFunctionOnLine(x);
            if(y < s){
               a = b; b = x; s = y;
            }
            else{
               c = x;
            }
         }
      }   
      if(istep == nst){
         this.nstep = -1; converged = false;         
      }
      else{
         this.nstep = istep; converged = true;
      }
   }

/**
* @return the postion of the minimum.
*/
   public double getX(){
      return b;
   }
 
/**
* @return the function value at the minimum.
*/
   public double getY(){  
      return s;
   }

/**
* @return the number of iteration steps needed, -1 if iteration failed.
*/
   public int getSteps(){
      return nstep;
   }

/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }   
   
}
