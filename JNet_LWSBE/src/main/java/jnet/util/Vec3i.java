package jnet.util;

public class Vec3i implements IVector3D<Integer> {
	
	public int x;
	public int y;
	public int z;
	
	@Override
	public Integer getVecX() {
		return x;
	}
	
	@Override
	public Integer getVecY() {
		return y;
	}

	@Override
	public Integer getVecZ() {
		return z;
	}
	
	public Vec3i() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3i(IVector3D<? extends Number> v) {
		this.x = (Integer) v.getVecX();
		this.y = (Integer) v.getVecY();
		this.z = (Integer) v.getVecZ();
	}

	public Vec3f mul(Vec3i v2) {
		return new Vec3f(x * v2.x, y * v2.y, z * v2.z);
	}
	
	public Vec3f mul(int scale) {
		return new Vec3f(x * scale, y * scale, z * scale);
	}
	
	public Vec3f div(Vec3f v2) {
		return new Vec3f(x / v2.x, y / v2.y, z / v2.z);
	}
	
	public Vec3f div(float scale) {
		return new Vec3f(x / scale, y / scale, z / scale);
	}
	
	public Vec3i add(Vec3i v2) {
		return new Vec3i(x + v2.x, y + v2.y, z + v2.z);
	}
	
	public Vec3i sub(Vec3i v2) {
		return new Vec3i(x - v2.x, y - v2.y, z - v2.z);
	}
	
	public double dot(Vec3i v2) {
		return x * v2.x + y * v2.y + z * v2.z;
	}

	public double distance(Vec3i v2) {
		double distanceX = Math.max(x, v2.x) - Math.min(x, v2.x);
		double distanceY = Math.max(y, v2.y) - Math.min(y, v2.y);
		double distanceZ = Math.max(z, v2.z) - Math.min(z, v2.z);
		double distXY = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
		return Math.sqrt(distXY * distXY + distanceZ * distanceZ);		
	}
//	
//	public double angle(Vec3i v2) {
//		double distX = v2.x - x;
//		double distY = v2.y - y;
//		// TODO
//		return Math.atan(distX / distY);
//	}
//	
//	public Vec3i forceByAngle(float angle) {
//		double fx = Math.sin(angle) * x;
//		double fy = Math.cos(angle) * y;
//		// TODO
//		return new Vec3i(fx, fy, 0);
//		
//	}
	
	public float summ() {
		return Math.abs(x) + Math.abs(y) + Math.abs(z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec3i) {
			return ((Vec3i) obj).x == x  && ((Vec3i) obj).y == y  && ((Vec3i) obj).z == z;
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
	
	public Vec3f noramlVec(Vec3i target) {
		Vec3i velocity = target.sub(this);
		return velocity.div((float) velocity.summ());
	}
	
}
