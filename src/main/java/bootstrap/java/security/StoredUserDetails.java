package bootstrap.java.security;

import bootstrap.java.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Stored User Details
 */
public class StoredUserDetails implements UserDetails {

  private String password;
  private String username;
  private boolean accountExpired = false;
  private boolean accountLocked = false;
  private boolean credentialsExpired = false;
  private boolean enabled = true;

  public StoredUserDetails( User user ) {
    password = user.getPassword();
    username = user.getEmail();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return !accountExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !credentialsExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
