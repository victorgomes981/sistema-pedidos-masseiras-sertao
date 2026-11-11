
package br.com.masseirasdosertao.controlepedidos.controller;

import br.com.masseirasdosertao.controlepedidos.model.Pedido;
import br.com.masseirasdosertao.controlepedidos.model.ItemPedido;
import br.com.masseirasdosertao.controlepedidos.model.Cliente;
import br.com.masseirasdosertao.controlepedidos.model.Produto;

import br.com.masseirasdosertao.controlepedidos.repository.ClienteRepository;
import br.com.masseirasdosertao.controlepedidos.repository.ProdutoRepository;
import br.com.masseirasdosertao.controlepedidos.repository.PedidoRepository;
import br.com.masseirasdosertao.controlepedidos.repository.ItemPedidoRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoController(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            ItemPedidoRepository itemPedidoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    // FORMULÁRIO DE NOVO PEDIDO
    @GetMapping("/pedidos/novo")
    public String formulario(Model model) {

        model.addAttribute("pedido", new Pedido());
        model.addAttribute("itensPedido", List.of(new ItemPedido()));
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());

        return "pedidos/formulario";
    }

    // LISTAR PEDIDOS
    @GetMapping("/pedidos")
    public String listar(Model model) {

        model.addAttribute(
            "pedidos",
            pedidoRepository.findAllByOrderByDataEntregaAsc()
        );

        model.addAttribute(
            "itens",
            itemPedidoRepository.findAll()
        );

        return "pedidos/lista";
    }

    // SALVAR NOVO PEDIDO COM VÁRIOS PRODUTOS
    @Transactional
    @PostMapping("/pedidos/salvar")
    public String salvar(
            @RequestParam Long clienteId,
            @RequestParam LocalDate dataEntrega,
            @RequestParam(required = false) String observacoes,
            @RequestParam List<Long> produtoIds,
            @RequestParam List<Integer> quantidades,
            RedirectAttributes redirectAttributes) {

        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cliente não encontrado."
            ));

        Pedido pedido = new Pedido();

        pedido.setCliente(cliente);
        pedido.setDataEntrega(dataEntrega);
        pedido.setStatus("Pendente");
        pedido.setObservacoes(observacoes);

        pedidoRepository.save(pedido);

        List<ItemPedido> itens = montarItensPedido(
            pedido, produtoIds, quantidades
        );

        itemPedidoRepository.saveAll(itens);

        redirectAttributes.addFlashAttribute(
            "mensagem",
            "Pedido cadastrado com sucesso!"
        );

        return "redirect:/pedidos";
    }

    // ATUALIZAR STATUS DO PEDIDO
    @PostMapping("/pedidos/atualizar-status")
    public String atualizarStatus(
            @RequestParam Long pedidoId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        if (!status.equals("Pendente")
                && !status.equals("Em produção")
                && !status.equals("Pronto")
                && !status.equals("Entregue")
                && !status.equals("Cancelado")) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Status inválido."
            );
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado."
            ));

        pedido.setStatus(status);

        pedidoRepository.save(pedido);

        redirectAttributes.addFlashAttribute(
            "mensagem",
            "Status do pedido atualizado com sucesso!"
        );

        return "redirect:/pedidos";
    }

    // ABRIR FORMULÁRIO PARA EDITAR PEDIDO
    @GetMapping("/pedidos/editar")
    public String editar(
            @RequestParam Long id,
            Model model) {

        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado."
            ));

        List<ItemPedido> itensPedido =
            itemPedidoRepository.findByPedidoId(id);

        model.addAttribute("pedido", pedido);
        model.addAttribute("itensPedido", itensPedido);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());

        return "pedidos/formulario";
    }

    // ATUALIZAR PEDIDO COM VÁRIOS PRODUTOS
    @Transactional
    @PostMapping("/pedidos/atualizar")
    public String atualizar(
            @RequestParam Long pedidoId,
            @RequestParam Long clienteId,
            @RequestParam LocalDate dataEntrega,
            @RequestParam(required = false) String observacoes,
            @RequestParam List<Long> produtoIds,
            @RequestParam List<Integer> quantidades,
            RedirectAttributes redirectAttributes) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado."
            ));

        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cliente não encontrado."
            ));

        pedido.setCliente(cliente);
        pedido.setDataEntrega(dataEntrega);
        pedido.setObservacoes(observacoes);

        pedidoRepository.save(pedido);

        itemPedidoRepository.deleteAll(
            itemPedidoRepository.findByPedidoId(pedidoId)
        );

        List<ItemPedido> itens = montarItensPedido(
            pedido, produtoIds, quantidades
        );

        itemPedidoRepository.saveAll(itens);

        redirectAttributes.addFlashAttribute(
            "mensagem",
            "Pedido atualizado com sucesso!"
        );

        return "redirect:/pedidos";
    }

    // VISUALIZAR DETALHES DO PEDIDO
    @GetMapping("/pedidos/detalhes")
    public String detalhes(
            @RequestParam Long id,
            Model model) {

        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado."
            ));

        List<ItemPedido> itens =
            itemPedidoRepository.findByPedidoId(id);

        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", itens);

        return "pedidos/detalhes";
    }

    // EXCLUIR PEDIDO
    @Transactional
    @PostMapping("/pedidos/excluir")
    public String excluir(
            @RequestParam Long pedidoId,
            RedirectAttributes redirectAttributes) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado."
            ));

        itemPedidoRepository.deleteAll(
            itemPedidoRepository.findByPedidoId(pedidoId)
        );

        pedidoRepository.delete(pedido);

        redirectAttributes.addFlashAttribute(
            "mensagem",
            "Pedido excluído com sucesso!"
        );

        return "redirect:/pedidos";
    }

    // MÉTODO AUXILIAR PARA MONTAR OS ITENS DO PEDIDO
    private List<ItemPedido> montarItensPedido(
            Pedido pedido,
            List<Long> produtoIds,
            List<Integer> quantidades) {

        if (produtoIds == null
                || quantidades == null
                || produtoIds.isEmpty()
                || produtoIds.size() != quantidades.size()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Informe pelo menos um produto."
            );
        }

        List<ItemPedido> itens = new ArrayList<>();

        for (int i = 0; i < produtoIds.size(); i++) {

            if (produtoIds.get(i) == null
                    || quantidades.get(i) == null
                    || quantidades.get(i) < 1) {

                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Verifique os produtos e as quantidades."
                );
            }

            Produto produto = produtoRepository
                .findById(produtoIds.get(i))
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Produto não encontrado."
                ));

            ItemPedido item = new ItemPedido();

            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidades.get(i));

            itens.add(item);
        }

        return itens;
    }
}
