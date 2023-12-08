package datan;

/**
* A class determining the minimum of a function along a straight line in n-dimensional space
* according to combination the method of the golden section and of quadratic interpolation
* @author  Siegmund Brandt.
*/
public final class MinCombined {
   static double C = 0.381966, EPSDEF = 1.E-12, TT = 1.E-15;
   static int MAXSTP = 1000;
   DatanUserFunction muf;
   double a, b, d, e, fu, fv,fx,fw, p, q, r, u, v, w, x, xm, eps, tol, t2;
   int nst, istep;
   DatanVector x0, xdir;
   FunctionOnLine fol;
   boolean parstep, converged;
/**
* @param x0 point in n-space
* @param xdir direction in n-space. Straight line is given by x = x0 + a * xdir, where a is a running scalar variable.
* @param xa value corresponding to a point defining one end the interval comprising the minimum.
* @param xb value corresponding to a point defining the other end the interval comprising the minimum.
* @param nstep maximal step number; if a value <= 0 is used, then 1000 is taken instead.
* @param epsilon accuracy; if a value <= 0. is used, then 1,E-8 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinCombined(double xa, double xb, DatanVector x0, DatanVector xdir, int nstep, double epsilon, DatanUserFunction muf) {
      a = xa;
      b = xb;
      eps = epsilon;
      this.x0 = x0;
      this.xdir = xdir;
      nst = nstep;
      this.muf = muf;
      if(eps <= 0.)eps = EPSDEF;
      if(nst <= 0) nst = MAXSTP;
      fol = new FunctionOnLine(x0, xdir, this.muf);
// initialize x at goldensection position between a and b
      x = a + C * (b - a);
// initialize e, v, w, fx, fv, fw
      e = 0.;
      v = x;
      w = x;
      fx = fol.getFunctionOnLine(x);
      fv = fx;
      fw = fx;
      xm = 0.5 *(a + b);
      tol = eps * Math.abs(x) + TT;
      t2 = 2. * tol;
      istep = 0;
         while(Math.abs(x - xm) > t2 - 0.5 * (b -a) && istep < nst){
            istep++;
            p = 0.; q = 0.; r = 0.; parstep = false;
            if(Math.abs(e) > tol){
// fit parabola
               r = (x - w) * (fx - fv);
               q = (x - v) * (fx - fw);
               p = (x - v) * q - (x - w) * r;
               q = 2. * (q - r);
               if(q > 0.){p = -p;}
               else{q = -q;}
               r = e;
               e = d;
               if(Math.abs(p) < Math.abs(0.5 * q * r) && p > q * (a - x) && p < q * (b - x)){
// use result of paraqbolic fit
                  d = p / q;
                  u = x + d;
                  if((u - a) < t2 || (b - u) < t2){
// make sure u is not too near a and b
                     d = -tol;
                     if(x < xm) d = tol;
                  }
                  parstep = true;
               }              
            }
            if(! parstep){
// perform golden section step
               if(x < xm){e = b - x;}
               else{e = a - x;}
               d = C * e;   
            }
// determine point u where function is to be computed,
// making sure it is not too close to x
            if(Math.abs(d) >= tol){
               u = x + d;
            }
            else if(d > 0.){
               u = x + tol;
            }
            else{
               u = x - tol;
            }
            fu = fol.getFunctionOnLine(u);
// update a, b, v, w, x
            if(fu <= fx){
               if(u < x){b = x;}
               else{a = x;}
               v = w; fv = fw; w = x; fw = fx; x = u; fx = fu; 
            }
            else{
               if(u < x){a = u;}
               else{b = u;}
               if(fu <= fw || w == x){
                  v = w; fv = fw; w = u; fw = fu;
               }
               else if(fu <= fv || v == x || v == w){
                  v = u; fv = fu;
               }               
            }
            xm = 0.5 *(a + b);
            tol = eps * Math.abs(x) + TT;
            t2 = 2. * tol;
         }  
      if(istep == nst){
         nst = -1; converged = false;         
      }
      else{
         nst = istep; converged = true;
      }
   }

/**
* @return the postion of the minimum.
*/
   public double getX(){
      return x;
   }
 
/**
* @return the function value at the minimum.
*/
   public double getY(){  
      return fx;
   }

/**
* @return the number of iteration steps needed, -1 if iteration failed.
*/
   public int getSteps(){
      return nst;
   }

/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }   
   
}
