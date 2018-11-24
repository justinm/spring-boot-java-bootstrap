package bootstrap.java.controllers;

import bootstrap.java.exceptions.ConflictException;
import bootstrap.java.exceptions.NotFoundException;
import bootstrap.java.models.User;
import bootstrap.java.models.UserService;
import bootstrap.java.controllers.requests.CreateUserRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController("apis/v1/users")
public class UserController {

  private UserService userService;

  public UserController( UserService userService ) {
    this.userService = userService;
  }

  @PostMapping
  public User createUser( @RequestBody CreateUserRequest createUserRequest ) throws ConflictException {
    return userService.createUser( createUserRequest );
  }
}
