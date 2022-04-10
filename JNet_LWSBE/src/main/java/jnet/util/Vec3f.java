package jnet.util;

public class Vec3f implements IVector3<Float> {
	
	public float x;
	public float y;
	public float z;
	
	@Override
	public Float getVecX() {
		return x;
	}
	
	@Override
	public Float getVecY() {
		return y;
	}

	@Override
	public Float getVecZ() {
		return z;
	}
	
	public Vec3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3f(IVector3<? extends Number> v) {
		this.x = (Float) v.getVecX();
		this.y = (Float) v.getVecY();
		this.z = (Float) v.getVecZ();
	}
	
	public Vec3f mul(Vec3f v2) {
		return new Vec3f(x * v2.x, y * v2.y, z * v2.z);
	}
	
	public Vec3f mul(float scale) {
		return new Vec3f(x * scale, y * scale, z * scale);
	}
	
	public Vec3f div(Vec3f v2) {
		return new Vec3f(x / v2.x, y / v2.y, z / v2.z);
	}
	
	public Vec3f div(float scale) {
		return new Vec3f(x / scale, y / scale, z / scale);
	}
	
	public Vec3f add(Vec3f v2) {
		return new Vec3f(x + v2.x, y + v2.y, z + v2.z);
	}
	
	public Vec3f sub(Vec3f v2) {
		return new Vec3f(x - v2.x, y - v2.y, z - v2.z);
	}
	
	public double dot(Vec3f v2) {
		return x * v2.x + y * v2.y + z * v2.z;
	}

	public double distance(Vec3f v2) {
		double distanceX = Math.max(x, v2.x) - Math.min(x, v2.x);
		double distanceY = Math.max(y, v2.y) - Math.min(y, v2.y);
		double distanceZ = Math.max(z, v2.z) - Math.min(z, v2.z);
		double distXY = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
		return Math.sqrt(distXY * distXY + distanceZ * distanceZ);		
	}
	
//	public double angle(Vec3f v2) {
//		double distX = v2.x - x;
//		double distY = v2.y - y;
//		// TODO
//		return Math.atan(distX / distY);
//	}
//	
//	public Vec3f forceByAngle(float angle) {
//		float fx = Math.sin(angle) * x;
//		float fy = Math.cos(angle) * y;
//		// TODO
//		return new Vec3f(fx, fy, 0);
//		
//	}
	
	public float summ() {
		return Math.abs(x) + Math.abs(y) + Math.abs(z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec3f) {
			return ((Vec3f) obj).x == x  && ((Vec3f) obj).y == y  && ((Vec3f) obj).z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return "|" + x + "," + y + "," + z + "|";
	}

	public Vec3f normalize() {
		float magnitude = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		return this.div(magnitude);
	}

	public Vec3f noramlVec(Vec3f target) {
		Vec3f velocity = target.sub(this);
		return velocity.div(velocity.summ());
	}
	
}
