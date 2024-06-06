package com.cdc.linkage.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
  private List<Patient> patients;

  @OneToOne(mappedBy = "person")
  private ExternalPerson externalPerson;

  public Person(List<Patient> patients) {
    this.patients = patients;
    for (Patient patient : patients) {
      patient.setPerson(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Person person = (Person) o;
    return Objects.equals(id, person.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
