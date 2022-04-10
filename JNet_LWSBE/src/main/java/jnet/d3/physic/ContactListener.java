package jnet.d3.physic;

/**
 * An abstract class to extend from that recives all collisions of the object that is bound to it (can be a SoftBody or the PhysicWorld).
 * It also determines if a collision is solved or if the objects pass each other.
 * @author M_Marvin
 *
 */
public abstract class ContactListener {
	
	/**
	 * Called before the collision is solved, returning false, prevents the PhysicSolver from solving this contact.
	 * The PhysicWorld ContactListener is asked before the SoftBody ContactListeners are called, if it returns false, this method is not called and the contact is solved.
	 * The ConstactListeners of the SoftBodys are called both in a random order and only if both return false, the solving is skipped.
	 * @param contact The Contact representing all informations about the collision
	 * @return true if the contact must be solved, false otherwise
	 */
	public abstract boolean beginContact(Contact contact);
	
	/**
	 * Called after the the collision is solved, and only if the previous method has returned true.
	 * @param contact The Contact representing all informations about the collision
	 */
	public abstract void endContact(Contact contact);
	
	public static class DummyListener extends ContactListener {
		public boolean beginContact(Contact contact) {
			return true;
		}
		public void endContact(Contact contact) {}
	}
	
}
