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
- ✅ **Documentação Interativa (Swagger):** UI do Swagger para visualização e teste de todos os endpoints da API.

### Como Executar o Projeto Localmente

Siga este guia passo a passo para configurar e executar a API na sua máquina local.

#### 1. Pré-requisitos

Antes de começar, garanta que você tenha as seguintes ferramentas instaladas:
-   **Java JDK 21** (ou superior)
-   **Maven 3.8+**
-   **Git**
-   **PostgreSQL** (rodando como um serviço local)
-   Uma IDE de sua preferência (ex: IntelliJ IDEA, VS Code)

#### 2. Configuração do Banco de Dados

1.  Abra seu cliente de banco de dados preferido (pgAdmin, DBeaver, etc.).
2.  Execute o seguinte comando SQL para criar o banco de dados que a aplicação usará:
    ```sql
    CREATE DATABASE alvara_db;
    ```

#### 3. Clonando e Configurando o Projeto

1.  Clone o repositório para sua máquina local:
    ```bash
    git clone [https://github.com/lucxsduarte/alvara-monitor.git](https://github.com/lucxsduarte/alvara-monitor.git)
    ```
2.  Navegue até a pasta do projeto:
    ```bash
    cd alvara-monitor
    ```
3.  **Este é o passo mais importante.** Na pasta `src/main/resources/`, crie um novo arquivo chamado `application-dev.yml`. Este arquivo conterá suas informações sensíveis locais e **não será** enviado para o GitHub (graças ao nosso `.gitignore`).
4.  Copie e cole o conteúdo abaixo no seu `application-dev.yml`, **substituindo os valores de exemplo pelos seus**:

    ```yaml
    # src/main/resources/application-dev.yml
    
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/alvara_db # Verifique se a porta do seu PostgreSQL é 5432
        username: SEU_USUARIO_POSTGRES # Ex: postgres
        password: SUA_SENHA_POSTGRES   # A senha que você definiu na instalação do PostgreSQL
    
    # Credenciais para a criação do primeiro administrador na primeira vez que a aplicação rodar
    app:
      initial-admin:
        login: admin-master
        senha: SuaSenhaForteParaOAdmin123
    ```

#### 4. Verificação

Após a inicialização, a aplicação estará rodando.
* A API estará disponível em: `http://localhost:8080`
* A documentação interativa para teste dos endpoints pode ser acessada em: `http://localhost:8080/swagger-ui.html`