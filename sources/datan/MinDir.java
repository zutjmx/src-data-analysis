package datan;

/**
* A class determining the minimum of a function along a straight line in n-dimensional space.
* The minimum is first enclosed and then found
* according to combination the method of the golden section and of quadratic interpolation.
* @author  Siegmund Brandt.
*/
public final class MinDir {
   static double DELTAX = 1.E-3, CUT = 0.1,EPSDEF = 1.E-18;
   static int MAXSTP = 1000;
   DatanUserFunction muf;
   double xa, xb, c, fc, dum, eps;
   int nst, nstin;
   DatanVector x0, xdir, xmin;
   FunctionOnLine fol;
   boolean converged;
   double[] xValues;
/**
* @param x0 point in n-space
* @param xdir direction in n-space. Straight line is given by x = x0 + a * xdir, where a is a running scalar variable.
* @param nstep maximal step number; if a value <= 0 is used, then 1000 is taken instead.
* @param epsilon accuracy; if a value <= 0. is used, then 1,E-8 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinDir(DatanVector x0, DatanVector xdir, int nstep, double epsilon, DatanUserFunction muf) {
      eps = epsilon;
      this.x0 = x0;
      this.xdir = xdir;
      nst = nstep;
      this.muf = muf;
      if(eps <= 0.)eps = EPSDEF;
      if(nst <= 0) nst = MAXSTP;
      nstin = nst;
      fol = new FunctionOnLine(x0, xdir, this.muf);
      xa = 0.;
      xb = CUT * DELTAX;
      MinEnclose me = new MinEnclose(xa, xb, x0, xdir, nst, muf);
      if (me.hasConverged()){
         xValues = me.getXValues();
         if(xValues[2] < xValues[0]){
            dum = xValues[2]; xValues[2] = xValues[0]; xValues[0] = dum;
         }
         MinCombined mc = new MinCombined(xValues[0], xValues[2], x0, xdir, nst, epsilon, muf);
         if(mc.hasConverged()){
            c = mc.getX();
            fc = mc.getY();
            xmin = x0.add(xdir.multiply(c));
            converged = true;
         }
         else{
            converged = false;
            xmin = new DatanVector(x0.getNumberOfElements());
            fc = 0.;
         }         
      }
      else{
         converged=false;
      }
   }

/**
* @return the postion of the minimum.
*/
   public DatanVector getMinPosition(){
      return xmin;
   }
 
/**
* @return the function value at the minimum.
*/
   public double getMinimum(){  
      return fc;
   }


/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }   
   
}
