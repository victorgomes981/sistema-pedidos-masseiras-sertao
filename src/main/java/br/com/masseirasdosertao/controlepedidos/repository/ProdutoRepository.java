
package br.com.masseirasdosertao.controlepedidos.repository;

import br.com.masseirasdosertao.controlepedidos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}

