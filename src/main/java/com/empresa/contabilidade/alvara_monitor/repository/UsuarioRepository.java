package com.empresa.contabilidade.alvara_monitor.repository;

import com.empresa.contabilidade.alvara_monitor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Usuario findByLogin(String login);

}
