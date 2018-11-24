package bootstrap.java.security;

import bootstrap.java.models.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailService implements UserDetailsService {
  private UserRepository userRepository;

  public AppUserDetailService( UserRepository userRepository ) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
    return new StoredUserDetails( userRepository.findByEmail( username ).orElseThrow( () -> new UsernameNotFoundException( "User could not be found by email" ) ) );
  }
}
