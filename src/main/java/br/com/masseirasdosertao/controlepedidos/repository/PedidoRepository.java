
package br.com.masseirasdosertao.controlepedidos.repository;

import br.com.masseirasdosertao.controlepedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByDataEntregaAsc();

}
