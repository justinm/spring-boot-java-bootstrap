package bootstrap.java.models;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
  Optional<User> findByEmail( String email );
}
