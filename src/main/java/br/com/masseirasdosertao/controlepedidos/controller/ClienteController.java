
package br.com.masseirasdosertao.controlepedidos.controller;


import br.com.masseirasdosertao.controlepedidos.model.Cliente;
import br.com.masseirasdosertao.controlepedidos.repository.ClienteRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


@Controller
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/clientes/novo")
    public String formulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }


@GetMapping("/clientes")
public String listar(Model model) {
    model.addAttribute("clientes", clienteRepository.findAll());
    return "clientes/lista";
}




@PostMapping("/clientes/salvar")
public String salvar(
        @Valid @ModelAttribute("cliente") Cliente cliente,
        BindingResult resultado,
        RedirectAttributes redirectAttributes) {

    if (resultado.hasErrors()) {
        return "clientes/formulario";
    }

    clienteRepository.save(cliente);

    redirectAttributes.addFlashAttribute(
        "mensagem",
        "Cliente cadastrado com sucesso!"
    );

    return "redirect:/clientes";
}


}
