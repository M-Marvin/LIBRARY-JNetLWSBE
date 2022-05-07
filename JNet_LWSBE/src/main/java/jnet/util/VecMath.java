package jnet.util;

public class VecMath {
	
	public static Vec2d nearestPointOnLine(Vec2d orig, Vec2d lineA, Vec2d lineB) {
		Vec2d v = lineB.sub(lineA);
		Vec2d w = orig.sub(lineA);
		double b = w.dot(v) / v.dot(v);
		return lineA.add(v.mul(b));
	}
	
	public static double rayLineDistance(Vec2d rayOrigin, Vec2d rayNormal, Vec2d lineA, Vec2d lineB) {
		
		Vec2d v1 = rayOrigin.sub(lineA);
		Vec2d v2 = lineB.sub(lineA);
		Vec2d v3 = new Vec2d(-rayNormal.y, -rayNormal.x);
		
		System.out.println("TESTs " + rayNormal);
		System.out.println("TESTs " + v2);
		
		double dot = v2.dot(v3);
//		if (Math.abs(dot) < 0.000001F) {
//			System.out.println("TEST1 " + d);
//			return -1D;
//		}
		System.out.println("TESTs " + v2 + " " + v3);
		
		
	    double t1 = v2.cross(v1) / dot;
	    double t2 = v1.dot(v3) / dot;
	    
//	    if (t1 >= 0.0 && (t2 >= 0.0 && t2 <= 1.0))
//	        return t1;
	    
	    return -1.0f;
		
	}
	
	public static Vec2d lineIntersection(Vec2d A, Vec2d B, Vec2d C, Vec2d D) {
		
		 // Line AB represented as a1x + b1y = c1
        double a1 = B.y - A.y;
        double b1 = A.x - B.x;
        double c1 = a1*(A.x) + b1*(A.y);
       
        // Line CD represented as a2x + b2y = c2
        double a2 = D.y - C.y;
        double b2 = C.x - D.x;
        double c2 = a2*(C.x)+ b2*(C.y);
       
        double determinant = a1*b2 - a2*b1;
       
        if (determinant == 0)
        {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return new Vec2d(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        else
        {
            double x = (b2*c1 - b1*c2)/determinant;
            double y = (a1*c2 - a2*c1)/determinant;
            return new Vec2d(x, y);
        }
		
//		Vec2d lineVec = line1B.sub(line1A).normalize();
//		double dist = rayLineDistance(line1A, lineVec, line2A, line2B);
//		return line1A.add(lineVec.mul(dist));
		
	}
	
	public static boolean pointOnLine(Vec2d point, Vec2d lineA, Vec2d lineB) {
		return point.distance(lineA) + point.distance(lineB) - lineA.distance(lineB) <= 0.00000001F;

	}
	
	public static boolean pointBetweenPoints(Vec2d l11, Vec2d l12, Vec2d p) {
		   if(p.x <= Math.max(l11.x, l12.x) && p.x <= Math.min(l11.x, l12.x) &&
				      (p.y <= Math.max(l11.y, l12.y) && p.y <= Math.min(l11.y, l12.y)))
				         return true;

				   return false;
	}
	
	public static boolean pointInPolygon(Vec2d a, Vec2d b, Vec2d c, Vec2d d, Vec2d point) {
		return checkInside(new Vec2d[] {a, b, c, d}, 4, point);
	}
	
	public static int direction(Vec2d a, Vec2d b, Vec2d c) {
		   double val = (b.y-a.y)*(c.x-b.x)-(b.x-a.x)*(c.y-b.y);
		   if (val == 0)
		      return 0;           //colinear
		   else if(val < 0)
		      return 2;          //anti-clockwise direction
		      return 1;          //clockwise direction
		}

	public static boolean isIntersect(Vec2d l11, Vec2d l12, Vec2d l21, Vec2d l22) {
		   //four direction for two lines and Vec2ds of other line
		   int dir1 = direction(l11, l12, l21);
		   int dir2 = direction(l11, l12, l22);
		   int dir3 = direction(l21, l22, l11);
		   int dir4 = direction(l21, l22, l12);

		   if(dir1 != dir2 && dir3 != dir4)
		      return true;           //they are intersecting
		   if(dir1==0 && pointBetweenPoints(l11, l12, l21))        //when p2 of line2 are on the line1
		      return true;
		   if(dir2==0 && pointBetweenPoints(l11, l12, l22))         //when p1 of line2 are on the line1
		      return true;
		   if(dir3==0 && pointBetweenPoints(l21, l22, l11))       //when p2 of line1 are on the line2
		      return true;
		   if(dir4==0 && pointBetweenPoints(l21, l22, l12)) //when p1 of line1 are on the line2
		      return true;
		   return false;
		}

		public static boolean checkInside(Vec2d poly[], int n, Vec2d p) {
		   if(n < 3)
		      return false;                  //when polygon has less than 3 edge, it is not polygon
		   Vec2d exline1 = p;
		   Vec2d exline2 = new Vec2d(Double.MAX_VALUE, p.y);//create a Vec2d at infinity, y is same as Vec2d p
		   //line exline = {p, {9999, p.y}};   
		   
		   int count = 0;
		   int i = 0;
		   do {
			  Vec2d side1 = poly[i];
			  Vec2d side2 = poly[(i + 1) % n];
		      //line side = {poly[i], poly[(i+1)%n]};     //forming a line from two consecutive Vec2ds of poly
		      if(isIntersect(side1, side2, exline1, exline2)) {          //if side is intersects exline
		         if(direction(side1, p, side2) == 0)
		            return pointBetweenPoints(side1, side2, p);
		         count++;
		      }
		      i = (i+1)%n;
		   } while(i != 0);
		      return count % 2 != 0;             //when count is odd
		}
		
	
}
