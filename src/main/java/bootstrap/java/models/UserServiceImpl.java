package bootstrap.java.models;

import bootstrap.java.exceptions.ConflictException;
import bootstrap.java.exceptions.NotFoundException;
import bootstrap.java.controllers.requests.CreateUserRequest;
import bootstrap.java.security.StoredUserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public UserServiceImpl( UserRepository userRepository,
                          PasswordEncoder passwordEncoder ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public User createUser( CreateUserRequest createUserRequest ) throws ConflictException {
    final Optional<User> existingUser = userRepository.findByEmail( createUserRequest.getEmail() );
    final User user = new User();

    if ( existingUser.isPresent() ) {
      throw new ConflictException( "Email address already in use" );
    }

    user.setEmail( createUserRequest.getEmail() )
        .setFirstName( createUserRequest.getFirstName() )
        .setLastName( createUserRequest.getLastName() )
        .setPassword( passwordEncoder.encode( createUserRequest.getPassword() ) );

    return user;
  }

  @Override
  public Optional<User> getUser( StoredUserDetails details ) {
    return userRepository.findByEmail( details.getUsername() );
  }
}
