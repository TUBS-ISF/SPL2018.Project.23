package de.kaemmelot.youmdb.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ActorRolePair implements Comparable<ActorRolePair> {
	private String actor;
	private String role;
	@Column(name="o")
	private int order;
	@Id
	@GeneratedValue
	private Long id;
	
	public ActorRolePair() { }
	
	public ActorRolePair(String actor, String role, int order) {
		this.actor = actor;
		this.role = role;
		this.order = order;
	}
	
	public String getActor() {
		return actor;
	}
	
	public void setActor(String actor) {
		this.actor = actor;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public void switchOrder(ActorRolePair arp) {
		if (arp == null)
			return;
		final int tmp = arp.order;
		arp.order = order;
		order = tmp;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public int compareTo(ActorRolePair arp) {
		if (arp == null)
			return -1;
		return Integer.compare(order, arp.order);
	}
}
