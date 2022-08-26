package br.com.carmonia.br.com.carmonia.repository;

import br.com.carmonia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByNameContaining(String name);
}
