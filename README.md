# ♨️ API - Monitoramento de Alvarás

![Status do Projeto](https://img.shields.io/badge/status-em--desenvolvimento-yellow)

Backend da aplicação de monitoramento de alvarás, desenvolvido com Spring Boot, Java 21 e JPA/Hibernate. Esta API é responsável por toda a lógica de negócio, gerenciamento de dados e comunicação com o banco de dados PostgreSQL.

### Funcionalidades Implementadas
- ✅ **API REST Completa:** CRUD completo e funcional para a entidade `Empresa`, com filtros e busca.
- ✅ **Endpoint de Dashboard:** Um endpoint de resumo (`/api/dashboard/summary`) com dados pré-calculados para otimizar a performance do frontend.
- ✅ **Lógica de Importação:** Serviço para importação de dados iniciais a partir de uma planilha CSV via URL.
- ✅ **Tratamento de Exceções:** Sistema de exceções customizadas com um handler global (`@RestControllerAdvice`).
- ✅ **Segurança com JWT:** Fluxo de autenticação via API com endpoint `/login` e proteção de rotas baseada em JSON Web Token.
- ✅ **Gerenciamento de Usuários (Admin):** Endpoints seguros para listar, criar, editar e deletar usuários.
- ✅ **Qualidade Assegurada por Testes:** Cobertura de testes de unidade (Mockito) para a camada de serviço e testes de integração (MockMvc) para a camada de controllers.
- ✅ **Gerenciamento de Configuração:** Uso de perfis (`profiles`) do Spring para separar configurações sensíveis (senhas) do código-fonte.
- ✅ **Configuração de CORS:** Permite a comunicação segura com o frontend da aplicação.

### Como Executar Localmente
1.  Clone este repositório.
2.  Para rodar em ambiente de desenvolvimento, crie um arquivo `src/main/resources/application-dev.yml` com as suas credenciais do banco de dados PostgreSQL.
3.  Execute a aplicação usando sua IDE ou via Maven (`mvn spring-boot:run`).
4.  A API estará disponível em `http://localhost:8080`.