package jnet.util;

public class Vec2i implements IVector<Integer> {
	
	public int x;
	public int y;
	
	@Override
	public Integer getVecX() {
		return x;
	}
	
	@Override
	public Integer getVecY() {
		return y;
	}
	
	public Vec2i() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2i(IVector<? extends Number> v) {
		this.x = (int) v.getVecX();
		this.y = (int) v.getVecY();	
	}
	
	public Vec2i mul(Vec2i v2) {
		return new Vec2i(x * v2.x, y * v2.y);
	}
	
	public Vec2i mul(int scale) {
		return new Vec2i(x * scale, y * scale);
	}
	
	public Vec2i div(Vec2i v2) {
		return new Vec2i(x / v2.x, y / v2.y);
	}
	
	public Vec2i div(int scale) {
		return new Vec2i(x / scale, y / scale);
	}
	
	public Vec2i add(Vec2i v2) {
		return new Vec2i(x + v2.x, y + v2.y);
	}
	
	public Vec2i sub(Vec2i v2) {
		return new Vec2i(x - v2.x, y - v2.y);
	}
	
	public int dot(Vec2i v2) {
		return x * v2.x + y * v2.y;
	}

	public int distance(Vec2i v2) {
		int distanceX = Math.max(x, v2.x) - Math.min(x, v2.x);
		int distanceY = Math.max(y, v2.y) - Math.min(y, v2.y);
		return (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);		
	}
	
	public int angle(Vec2i v2) {
		int distX = v2.x - x;
		int distY = v2.y - y;
		return (int) Math.atan(distX / distY);
	}
	
	public Vec2i forceByAngle(int angle) {
		int fx = (int) (Math.sin(angle) * x);
		int fy = (int) (Math.cos(angle) * y);
		return new Vec2i(fx, fy);
		
	}
	
	public int summ() {
		return Math.abs(x) + Math.abs(y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec2i) {
			return ((Vec2i) obj).x == x  && ((Vec2i) obj).y == y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "|" + x + "," + y + "|";
	}

	public Vec2i normalize() {
		int magnitude = (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		return this.div(magnitude);
	}

	public Vec2i noramlVec(Vec2i target) {
		Vec2i velocity = target.sub(this);
		return velocity.div(velocity.summ());
	}
	
}
