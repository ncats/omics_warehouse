package gov.nih.ncats.omics.warehouse.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Person class holds basic information on people involved with an omics project, experiment or data analysis.
 * This is captured so that Omics Entities (experiments, projects etc.) can be associated with a key person or people.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name="omics_adm.investigator", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Person implements Comparable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "invest_id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	public Person() {		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof Person) {
			Person other = (Person)o;
			return getLastFirstJoin().compareTo(other.getLastFirstJoin());
		}
		return 0;
	}
	
	@JsonIgnore
	public String getLastFirstJoin() {
		return lastName + firstName;
	}
}
