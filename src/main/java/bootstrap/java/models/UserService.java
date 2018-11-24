package bootstrap.java.models;

import bootstrap.java.exceptions.ConflictException;
import bootstrap.java.exceptions.NotFoundException;
import bootstrap.java.controllers.requests.CreateUserRequest;
import bootstrap.java.security.StoredUserDetails;

import java.util.Optional;

public interface UserService {
  User createUser( CreateUserRequest createUserRequest ) throws ConflictException;
  Optional<User> getUser( StoredUserDetails details );
}
