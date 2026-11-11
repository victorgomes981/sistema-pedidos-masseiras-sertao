# Auto-revisão Técnica — Sistema de Controle de Pedidos

## 1. Arquitetura
O sistema foi desenvolvido utilizando Spring Boot, com separação em camadas: controllers, models e repositories. Essa organização facilita a manutenção e a compreensão do código.

## 2. Refatoração
Durante a revisão, foi identificado que algumas regras de negócio estão concentradas nos controllers. Como melhoria futura, essas responsabilidades podem ser transferidas para classes Service, tornando o código mais organizado.

## 3. Legibilidade
Foram utilizados nomes claros para classes, métodos e variáveis. A separação das funcionalidades de clientes, produtos e pedidos também facilita a leitura e manutenção do projeto.

## 4. Segurança
A conexão com o PostgreSQL utiliza uma variável de ambiente para armazenar a senha, evitando deixá-la diretamente no arquivo de configuração. Como melhoria futura, recomenda-se implementar níveis de acesso específicos para os usuários e ampliar as validações das operações.

## 5. Conclusão
A auto-revisão permitiu verificar que o MVP possui as principais funcionalidades propostas. Também foram identificadas oportunidades de melhoria na organização das regras de negócio e na segurança da aplicação.
