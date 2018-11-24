package bootstrap.java.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class User {
  @Id
  private UUID id = UUID.randomUUID();
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  
  public UUID getId() {
    return id;
  }

  public User setId( UUID id ) {
    this.id = id;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public User setFirstName( String firstName ) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public User setLastName( String lastName ) {
    this.lastName = lastName;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public User setEmail( String email ) {
    this.email = email;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public User setPassword( String password ) {
    this.password = password;
    return this;
  }
}
