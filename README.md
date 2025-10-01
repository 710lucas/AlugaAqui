# AlugaAqui: Plataforma de Aluguel de Imóveis

## 🏠 Visão Geral do Projeto

O **AlugaAqui** é uma API RESTful desenvolvida em Java com Spring Boot, projetada para gerenciar o fluxo de aluguel de imóveis, desde o cadastro de propriedades e manifestação de interesse até a formalização dos contratos. O sistema é estruturado em torno de dois perfis de usuários principais:

* **Locador (`LOCADOR`):** Responsável por listar e gerenciar imóveis (`Casa`) e contratos de aluguel (`Aluguel`). Possui total controle sobre suas propriedades e transações.
* **Locatário (`LOCATARIO`):** Pode visualizar imóveis, manifestar interesse (`Interesse`) em propriedades e acompanhar seus contratos ativos.

A aplicação segue o padrão arquitetural em camadas, separando responsabilidades entre Controllers, Services e Repositories, e utiliza DTOs para garantir a segurança e integridade dos dados durante as transações.

## ✨ Tecnologias e Arquitetura Principal

| Categoria | Tecnologia | Detalhe |
| :--- | :--- | :--- |
| **Linguagem & Framework** | Java 21, Spring Boot 3.5.5 | Backend robusto focado em alta performance e escalabilidade. |
| **Persistência** | Spring Data JPA, PostgreSQL | Utilização de um banco relacional com JPARepository para acesso eficiente a dados. |
| **Segurança** | Spring Security, JWT | Autenticação *Stateless* com Tokens JWT, e Autorização baseada em Roles (`LOCADOR`, `LOCATARIO`) e regras de propriedade (`@PreAuthorize`). |
| **Documentação** | Springdoc OpenAPI / Swagger UI | Documentação automática e interativa disponível em ambiente de desenvolvimento. |
| **Padrões** | DTO, Service Layer, Exception Handling | Uso de DTOs para mapeamento (via ModelMapper), lógica de negócio isolada em Services e tratamento global de erros. |

---

## 🔒 Fluxo de Segurança (JWT)

A API utiliza o Spring Security para proteger todas as rotas, exceto `/api/login` e `POST /usuarios/`.

1. **Login:** O usuário envia credenciais para `POST /api/login`.
2. **Token:** O sistema retorna um **Token JWT** em caso de sucesso.
3. **Acesso Protegido:** Para acessar qualquer outra rota, o usuário deve incluir o token no cabeçalho `Authorization` no formato `Bearer <token>`.
4. **Autorização:** A validação da permissão para uma ação específica é realizada em tempo de execução via anotações `@PreAuthorize` nos métodos de Serviço, verificando se o usuário é o proprietário do recurso ou possui a Role necessária (ex: `@PreAuthorize("hasRole('LOCADOR')")`).

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

* Java Development Kit (JDK) 21.
* Apache Maven.
* Docker e Docker Compose.

### 1. Configuração do Banco de Dados

O projeto utiliza o Docker Compose para iniciar uma instância do PostgreSQL.

1.  Navegue até o diretório raiz.
2.  Execute o seguinte comando para iniciar o banco:

    ```bash
    docker-compose up -d
    ```

    As configurações de acesso estão definidas no `application.properties`:
    * **URL:** `jdbc:postgresql://localhost:5432/alugaqui`
    * **Usuário/Senha:** `postgres`/`postgres`

### 2. Execução da Aplicação

1.  No diretório raiz, execute a aplicação Spring Boot:

    ```bash
    ./mvnw spring-boot:run
    ```

A API estará disponível em `http://localhost:8080`.

### Usuário Inicial (Default)

Um usuário administrador é criado automaticamente na inicialização, caso não exista:

| Email | Senha | Role |
| :--- | :--- | :--- |
| `admin@admin.com` | `admin` | `LOCADOR` |

## 📚 Documentação da API (Swagger UI)

Acesse a documentação completa dos endpoints, DTOs e regras de validação em:

```
http://localhost:8080/swagger-ui/index.html
```

> **Atenção:** Utilize a função **Authorize** no Swagger para inserir o token JWT obtido após o login (ex: `Bearer <token>`).

## 🔄 Exemplo de Fluxo de Uso

Este fluxo demonstra a interação entre Locador e Locatário para a concretização de um aluguel.

### Passo 1: Cadastro e Autenticação

| Ação | Endpoint | Dados (Exemplo) | Resultado |
| :--- | :--- | :--- | :--- |
| **1.1 Login Admin** | `POST /api/login` | `username: "admin@admin.com", password: "admin"` | **Token JWT Admin** |
| **1.2 Criar Locador** | `POST /usuarios/` | `role: "LOCADOR"` | **ID do Locador** |
| **1.3 Criar Locatário**| `POST /usuarios/` | `role: "LOCATARIO"` | **ID do Locatário** |
| **1.4 Login Locador** | `POST /api/login` | Credenciais do Locador | **Token JWT Locador** |
| **1.5 Login Locatário**| `POST /api/login` | Credenciais do Locatário | **Token JWT Locatário** |

### Passo 2: Publicação e Interesse na Casa

| Ação | Endpoint | Usuário | Observações |
| :--- | :--- | :--- | :--- |
| **2.1 Criar Casa** | `POST /casas` | **Locador** | Cria um imóvel. **Guarde o `casaId`**. |
| **2.2 Buscar Casas** | `GET /casas` | Locatário | Lista os imóveis disponíveis (opcionalmente filtrando por `tipo`, `minQuartos`, etc.). |
| **2.3 Criar Interesse**| `POST /interesses/` | **Locatário** | O Locatário demonstra interesse na casa (requer `casaId`). |
| **2.4 Listar Interesses**| `GET /interesses/` | **Locador** | O Locador visualiza o interesse criado pelo Locatário. |

### Passo 3: Criação do Contrato de Aluguel

| Ação | Endpoint | Usuário | Resultado |
| :--- | :--- | :--- | :--- |
| **3.1 Criar Aluguel** | `POST /alugueis` | **Locador** | O Locador formaliza o contrato, vinculando `casaId` e `locatarioId`. |
| **3.2 Buscar Aluguel**| `GET /alugueis/{id}` | Locador **ou** Locatário | Ambos podem ver os detalhes do contrato. |
| **3.3 Atualizar Aluguel**| `PATCH /alugueis/{id}` | **Locador** | Altera o status ou valor do aluguel. |

### Regras de Autorização (Resumo)

As permissões são controladas por `@PreAuthorize` e baseadas na função (`Role`) e na propriedade do recurso.

| Recurso | `POST` (Criar) | `GET` (Listar/Buscar) | `PATCH` (Atualizar) |
| :--- | :--- | :--- | :--- |
| **`Casa`** | Somente `LOCADOR` | Todos Autenticados | Somente o `LOCADOR` da casa |
| **`Interesse`** | Somente `LOCATARIO` | `LOCATARIO` do interesse ou `LOCADOR` da casa | Somente o `LOCATARIO` do interesse |
| **`Aluguel`** | Somente `LOCADOR` (dono da casa) | `LOCADOR` ou `LOCATARIO` participante | Somente o `LOCADOR` do contrato |
| **`Usuario`** | Qualquer um (para cadastro) | Todos Autenticados | Todos Autenticados |
