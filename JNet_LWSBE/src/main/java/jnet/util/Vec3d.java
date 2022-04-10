package jnet.util;

public class Vec3d implements IVector3D<Double> {
	
	public double x;
	public double y;
	public double z;
	
	@Override
	public Double getVecX() {
		return x;
	}
	
	@Override
	public Double getVecY() {
		return y;
	}

	@Override
	public Double getVecZ() {
		return z;
	}
	
	public Vec3d() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vec3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3d(IVector3D<? extends Number> v) {
		this.x = (Double) v.getVecX();
		this.y = (Double) v.getVecY();
		this.z = (Double) v.getVecZ();
	}
	
	public Vec3d mul(Vec3d v2) {
		return new Vec3d(x * v2.x, y * v2.y, z * v2.z);
	}
	
	public Vec3d mul(double scale) {
		return new Vec3d(x * scale, y * scale, z * scale);
	}
	
	public Vec3d div(Vec3d v2) {
		return new Vec3d(x / v2.x, y / v2.y, z / v2.z);
	}
	
	public Vec3d div(double scale) {
		return new Vec3d(x / scale, y / scale, z / scale);
	}
	
	public Vec3d add(Vec3d v2) {
		return new Vec3d(x + v2.x, y + v2.y, z + v2.z);
	}
	
	public Vec3d sub(Vec3d v2) {
		return new Vec3d(x - v2.x, y - v2.y, z - v2.z);
	}
	
	public double dot(Vec3d v2) {
		return x * v2.x + y * v2.y + z * v2.z;
	}

	public double distance(Vec3d v2) {
		double distanceX = Math.max(x, v2.x) - Math.min(x, v2.x);
		double distanceY = Math.max(y, v2.y) - Math.min(y, v2.y);
		double distanceZ = Math.max(z, v2.z) - Math.min(z, v2.z);
		double distXY = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
		return Math.sqrt(distXY * distXY + distanceZ * distanceZ);		
	}
	
	public Vec2d angle(Vec3d v2) {
		double distX = v2.x - x;
		double distY = v2.y - y;
		double distZ = v2.z - z;
		double angleXY = Math.atan(distX / distY);
		double distXY = Math.sqrt(distX * distX + distY * distY);
		double angleZ = Math.atan(distZ / distXY);
		return new Vec2d(angleXY, angleZ);
	}
	
	public Vec3d forceByAngle(Vec2d angle) {
		
		double fx =  Math.sin(angle.y) * Math.sin(angle.x) * x;
		double fy =  Math.sin(angle.y) * Math.cos(angle.x) * y;
		double fz = Math.sin(angle.y) * z;
		return new Vec3d(fx, fy, fz);
	}
	
	public double summ() {
		return Math.abs(x) + Math.abs(y) + Math.abs(z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec3d) {
			return ((Vec3d) obj).x == x  && ((Vec3d) obj).y == y  && ((Vec3d) obj).z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return "|" + x + "," + y + "," + z + "|";
	}

	public Vec3d normalize() {
		double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		return this.div(magnitude);
	}

	public Vec3d noramlVec(Vec3d target) {
		Vec3d velocity = target.sub(this);
		return velocity.div(velocity.summ());
	}
	
}
