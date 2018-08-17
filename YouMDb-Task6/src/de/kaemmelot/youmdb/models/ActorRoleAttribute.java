package de.kaemmelot.youmdb.models;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.SortNatural;

import de.kaemmelot.youmdb.Database;

@Entity
public class ActorRoleAttribute extends MovieAttribute {
	@SortNatural
	@OneToMany
	private SortedSet<ActorRolePair> actorRoles = new TreeSet<ActorRolePair>();
	
	public SortedSet<ActorRolePair> getActorRoles() {
		return actorRoles;
	}
	
	public void addActorRole(String actor, String role) {
		ActorRolePair arp = new ActorRolePair(actor, role, actorRoles.size());
		actorRoles.add(arp);
		Database.getInstance().add(arp);
	}
	
	public void addActorRole(ActorRolePair arp) {
		actorRoles.add(arp);
		Database.getInstance().add(arp);
	}
	
	public ActorRolePair switchActorRoles(int i1, int i2) {
		if (i1 > actorRoles.size() || i2 > actorRoles.size())
			return null;
		ActorRolePair arp1 = null;
		ActorRolePair arp2 = null;
		for (ActorRolePair arp : actorRoles) {
			if (arp.getOrder() == i1)
				arp1 = arp;
			else if (arp.getOrder() == i2)
				arp2 = arp;
			if (arp1 != null && arp2 != null)
				break;
		}
		arp1.switchOrder(arp2);
		// readd them to reorder the list
		actorRoles.remove(arp1);
		actorRoles.remove(arp2);
		actorRoles.add(arp1);
		actorRoles.add(arp2);
		return arp1;
	}
	
	public ActorRolePair removeActorRole(int order) {
		ActorRolePair arp = null;
		for (ActorRolePair a : actorRoles) {
			if (a.getOrder() > order)
				a.setOrder(a.getOrder() - 1);
			else if (a.getOrder() == order)
				arp = a;
		}
		if (arp != null) {
			actorRoles.remove(arp);
			return arp;
		}
		return null;
	}
}
