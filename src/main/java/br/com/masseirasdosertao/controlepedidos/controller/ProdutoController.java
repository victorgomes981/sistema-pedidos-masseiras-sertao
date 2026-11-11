
package br.com.masseirasdosertao.controlepedidos.controller;

import br.com.masseirasdosertao.controlepedidos.model.Produto;
import br.com.masseirasdosertao.controlepedidos.repository.ProdutoRepository;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/produtos/novo")
    public String formulario(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/formulario";
    }

    @GetMapping("/produtos")
    public String listar(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "produtos/lista";
    }

    @PostMapping("/produtos/salvar")
    public String salvar(
            @Valid @ModelAttribute("produto") Produto produto,
            BindingResult resultado,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            return "produtos/formulario";
        }

        produtoRepository.save(produto);

        redirectAttributes.addFlashAttribute(
            "mensagem",
            "Produto cadastrado com sucesso!"
        );

        return "redirect:/produtos";
    }
}
