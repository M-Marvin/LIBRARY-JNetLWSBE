package jnet.util;

public class Vec2d implements IVector<Double> {
	
	public double x;
	public double y;
	
	@Override
	public Double getVecX() {
		return x;
	}
	
	@Override
	public Double getVecY() {
		return y;
	}
	
	public Vec2d() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2d(IVector<? extends Number> v) {
		this.x = (double) v.getVecX();
		this.y = (double) v.getVecY();	
	}
	
	public Vec2d mul(Vec2d v2) {
		return new Vec2d(x * v2.x, y * v2.y);
	}
	
	public Vec2d mul(double scale) {
		return new Vec2d(x * scale, y * scale);
	}
	
	public Vec2d div(Vec2d v2) {
		return new Vec2d(x / v2.x, y / v2.y);
	}
	
	public Vec2d div(double scale) {
		return new Vec2d(x / scale, y / scale);
	}
	
	public Vec2d add(Vec2d v2) {
		return new Vec2d(x + v2.x, y + v2.y);
	}
	
	public Vec2d sub(Vec2d v2) {
		return new Vec2d(x - v2.x, y - v2.y);
	}
	
	public double dot(Vec2d v2) {
		return x * v2.x + y * v2.y;
	}

	public double distance(Vec2d v2) {
		double distanceX = Math.max(x, v2.x) - Math.min(x, v2.x);
		double distanceY = Math.max(y, v2.y) - Math.min(y, v2.y);
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY);		
	}
	
	public double angle(Vec2d v2) {
		double distX = v2.x - x;
		double distY = v2.y - y;
		return Math.atan(distX / distY);
	}
	
	public Vec2d forceByAngle(double angle) {
		double fx = Math.sin(angle) * x;
		double fy = Math.cos(angle) * y;
		return new Vec2d(fx, fy);
		
	}
	
	public double summ() {
		return Math.abs(x) + Math.abs(y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec2d) {
			return ((Vec2d) obj).x == x  && ((Vec2d) obj).y == y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "|" + x + "," + y + "|";
	}

	public Vec2d normalize() {
		double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		return this.div(magnitude);
	}

	public Vec2d noramlVec(Vec2d target) {
		Vec2d velocity = target.sub(this);
		return velocity.div(velocity.summ());
	}
	
}
