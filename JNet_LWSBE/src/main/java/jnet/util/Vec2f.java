package jnet.util;

public class Vec2f implements IVector2D<Float> {
	
	public float x;
	public float y;
	
	@Override
	public Float getVecX() {
		return x;
	}
	
	@Override
	public Float getVecY() {
		return y;
	}
	
	public Vec2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2f(IVector2D<? extends Number> v) {
		this.x = (Float) v.getVecX();
		this.y = (Float) v.getVecY();	
	}
	
	public Vec2f mul(Vec2f v2) {
		return new Vec2f(x * v2.x, y * v2.y);
	}
	
	public Vec2f mul(float scale) {
		return new Vec2f(x * scale, y * scale);
	}
	
	public Vec2f div(Vec2f v2) {
		return new Vec2f(x / v2.x, y / v2.y);
	}
	
	public Vec2f div(float scale) {
		return new Vec2f(x / scale, y / scale);
	}
	
	public Vec2f add(Vec2f v2) {
		return new Vec2f(x + v2.x, y + v2.y);
	}
	
	public Vec2f sub(Vec2f v2) {
		return new Vec2f(x - v2.x, y - v2.y);
	}
	
	public float dot(Vec2f v2) {
		return x * v2.x + y * v2.y;
	}

	public float distance(Vec2f v2) {
		float distanceX = Math.max(x, v2.x) - Math.min(x, v2.x);
		float distanceY = Math.max(y, v2.y) - Math.min(y, v2.y);
		return (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);		
	}
	
	public float angle(Vec2f v2) {
		float distX = v2.x - x;
		float distY = v2.y - y;
		return (float) Math.atan(distX / distY);
	}
	
	public Vec2f forceByAngle(float angle) {
		float fx = (float) (Math.sin(angle) * x);
		float fy = (float) (Math.cos(angle) * y);
		return new Vec2f(fx, fy);
		
	}
	
	public float summ() {
		return Math.abs(x) + Math.abs(y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec2f) {
			return ((Vec2f) obj).x == x  && ((Vec2f) obj).y == y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "|" + x + "," + y + "|";
	}

	public Vec2f normalize() {
		float magnitude = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		return this.div(magnitude);
	}

	public Vec2f noramlVec(Vec2f target) {
		Vec2f velocity = target.sub(this);
		return velocity.div(velocity.summ());
	}
	
}
