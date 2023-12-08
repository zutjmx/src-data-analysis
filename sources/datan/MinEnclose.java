package datan;

/**
* A class trying to find an interval along a straight line in n-dimensional space which contains 
* the minimum on that line of a function in n-space.
* @author  Siegmund Brandt.
*/
public final class MinEnclose {
   static double ZERO = 0., GSPAR = 1.618034, FACMAG = 10., EPSILON = 1.E-10;
   static int MAXSTP = 1000;
   DatanUserFunction muf;
   double xa, xb, xc, xend, xm, ya, yb, yc, ym, buf;
   int nstep, nst, istep;
   DatanVector x0, xdir;
   FunctionOnLine fol;
   boolean converged;   
   double[] XValues, YValues;

/**
* @param x0 point in n-space
* @param xdir direction in n-space. Straight line is given by x = x0 + a * xdir, where a is a running scalar variable.
* @param nstep maximal step number; if a value <= 0 is used, then 1000 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinEnclose(double a, double b, DatanVector x0, DatanVector xdir, int nstep, DatanUserFunction muf) {
      this.xa = a;
      this.xb = b;
      this.x0 = x0;
      this.xdir = xdir;
      this.nstep = nstep;
      this.muf = muf;
      XValues = new double[3];
      YValues = new double[3];
      fol = new FunctionOnLine(x0, xdir, this.muf);
      ya = fol.getFunctionOnLine(xa);
      yb = fol.getFunctionOnLine(xb);
      nst = nstep;
      if(nst <= 0) nst = MAXSTP;
      if(yb > ya){
// exchange a and b
         buf = xa;
         xa = xb;
         xb = buf;
         buf = ya;
         ya = yb;
         yb = buf;
      }
      xc = xb + GSPAR * (xb - xa);
      yc = fol.getFunctionOnLine(xc);
      istep = 0;
      while(yb >= yc && istep < nst){
         istep++;
// step was still downwards
         xend = xb + FACMAG * (xc - xb);
         xm = MinParab.getPositionOfExtremum(xa, ya, xb, yb, xc, yc);
         ym = fol.getFunctionOnLine(xm);
         if((xm - xb) * (xc - xm) > ZERO){
// case (a): xm is between xb and xc
            if(ym < yc){
// case (a1): minimum is between xb and xc
               xa = xb; ya = yb; xb = xm; yb = ym;
            }
            else if(ym > yb){
// case (a2): minimum is between xa and xm
               xc = xm; yc = ym;
            }
            else{
// case (a3): there was no minimum, go on
               xm = xc + GSPAR * (xc - xb);       
               extend(xm);                
            }
         }
         else if((xc - xm) * (xm - xend) > ZERO){
// case (b): xm is between xc and xend
            if(ym < yc){
// case (b2): there was no minimum, go on
               xm = xc + GSPAR * (xc - xb);       
               extend(xm); 
            }
            else{
// case (b1): minimum is between xb and xm
               xa = xb; ya = yb; xb = xc; yb = yc; xc = xm; yc = ym;
            }
         }
         else if((xm - xend) * (xend - xc) >= ZERO){
// case (c): xm is beyond xend
            xm = xend;       
            extend(xm); 
         }
         else{
// case (d): normally should not happen, go on
            xm = xc + GSPAR * (xc - xb);       
            extend(xm);
         }
//         System.out.println("istep = " + istep + ", xa = " + xa + ", xb = " + xb + ", xc = " + xc + ", xm = " + xm);
      }
      if(istep == nst){
         this.nstep = -1; converged = false;         
      }
      else{
         this.nstep = istep; converged = true;
      }    
   }

   private void extend(double xt){
      xa = xb; ya = yb; xb = xc; yb = yc; xc = xt; yc = fol.getFunctionOnLine(xc);     
   }

/**
* @return 3 values (xa, xb, xc) of the variable a determining the interval containing the minimum.
*/
   public double[] getXValues(){
      XValues[0] = xa; XValues[1] = xb; XValues[2] = xc;     
      return XValues;
   }

/**
* @return the  function values (ya, yb, yc) of at the points given by (xa, xb, xc).
*/
   public double[] getYValues(){
      YValues[0] = ya; YValues[1] = yb; YValues[2] = yc;      
      return YValues;
   }

/**
* @return the number of iteration steps needed, -1 if iteration failed.
*/
   public int getSteps(){
      return this.nstep;
   }

/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }   
   
}
