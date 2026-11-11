
package br.com.masseirasdosertao.controlepedidos.repository;

import br.com.masseirasdosertao.controlepedidos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
