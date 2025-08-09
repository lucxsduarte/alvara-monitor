package com.empresa.contabilidade.alvara_monitor.repositories;

import com.empresa.contabilidade.alvara_monitor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByLogin(String login);

}
