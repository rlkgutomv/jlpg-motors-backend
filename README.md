# JLPG Motors — Backend

API REST desenvolvida em arquitetura de microsserviços com Spring Boot para suporte ao aplicativo mobile JLPG Motors, uma plataforma de e-commerce de veículos.

## Tecnologias

- **Java 21**
- **Spring Boot 3.3+**
- **Spring Cloud Gateway**
- **Spring Cloud Netflix Eureka**
- **Spring Security + JWT**
- **PostgreSQL**
- **Flyway Migration**
- **Docker / Docker Compose**

## Arquitetura

O sistema é composto por 5 microsserviços independentes:

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `discovery-server` | 8761 | Registro e descoberta de serviços (Eureka) |
| `gateway-server` | 8080 | Porta de entrada única, autenticação JWT e roteamento |
| `auth-service` | 8081 | Cadastro e autenticação de usuários |
| `vehicle-service` | 8082 | Gerenciamento do catálogo de veículos |
| `customer-service` | 8083 | Favoritos e sistema de negociação |

## Segurança

A autenticação é centralizada no `gateway-server` através do `JwtAuthenticationFilter`. Todas as requisições protegidas passam pelo gateway, que valida o token JWT e injeta o header `X-User-Id` automaticamente para os serviços internos.

### Matriz de Permissões

| Funcionalidade | USER | ADMIN |
|---|---|---|
| Listar veículos | ✅ | ✅ |
| Favoritar veículos | ✅ | ✅ |
| Iniciar negociação | ✅ | ✅ |
| Cadastrar veículos | ❌ | ✅ |
| Editar veículos | ❌ | ✅ |
| Excluir veículos | ❌ | ✅ |
| Painel de negociações | ❌ | ✅ |

## Endpoints

### Auth Service
| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/auth-service/auth/register` | ❌ | Cadastro de usuário |
| POST | `/auth-service/auth/login` | ❌ | Login (retorna JWT + dados) |

### Vehicle Service
| Método | Rota | Auth | Descrição |
|---|---|---|---|
| GET | `/vehicle-service/vehicles` | ❌ | Listar veículos |
| POST | `/vehicle-service/vehicles` | ADMIN | Cadastrar veículo |
| PUT | `/vehicle-service/vehicles/{id}` | ADMIN | Atualizar veículo |
| DELETE | `/vehicle-service/vehicles/{id}` | ADMIN | Excluir veículo |

### Customer Service
| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/customer-service/favorites/{vehicleId}` | USER | Toggle favorito |
| GET | `/customer-service/favorites` | USER | Listar favoritos |
| POST | `/customer-service/negotiations/{vehicleId}` | USER | Iniciar negociação |
| GET | `/customer-service/negotiations/my-chats` | USER | Histórico de negociações |
| PUT | `/customer-service/negotiations/{chatId}/close` | ADMIN | Encerrar negociação |
| GET | `/customer-service/negotiations/admin/all` | ADMIN | Todas as negociações |

## Como rodar localmente

### Pré-requisitos
- Java 21
- Maven
- Docker Desktop

### 1. Subir o banco de dados
```bash
docker-compose up -d
```

### 2. Iniciar os serviços (nessa ordem)
```bash
cd discovery-server && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd vehicle-service && mvn spring-boot:run
cd customer-service && mvn spring-boot:run
cd gateway-server && mvn spring-boot:run
```

### 3. Acessar
- Gateway (API): `http://localhost:8080`
- Eureka Dashboard: `http://localhost:8761`

## Usuários de teste

| Usuário | Email | Senha | Role |
|---|---|---|---|
| Admin | adm@jlpg.com | senha123 | ADMIN |
| Cliente | comum@jlpg.com | senha123 | USER |

## Disciplina

Projeto desenvolvido para a disciplina **Projeto, Design e Engenharia de Processos** — ATITUS Educação, Passo Fundo/RS.

**Professor:** Augusto Kruger Ortolan / Luciano Rodrigo Ferretto

**Integrantes:**
- João Paulo Pasolini
- Luis Eduardo Moroso
- Gustavo Marcante Vazzoler
- Pedro Henrique Renosto
