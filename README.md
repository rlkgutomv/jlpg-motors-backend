# JLPG Motors - Backend

Backend em microservicos com Spring Boot para o projeto JLPG Motors.

## Microservicos

| Servico | Porta | Responsabilidade |
|---|---:|---|
| `discovery-service` | 8761 | Service Registry com Eureka |
| `config-service` | 8888 | Configuracao centralizada com Spring Cloud Config |
| `gateway-service` | 8080 | API Gateway, discovery, load balancing e filtro JWT |
| `greeting-service` | 8086 | Saudacao simples via configuracao externa |
| `auth-service` | 8081 | Cadastro, login e emissao de JWT |
| `currency-service` | 8084 | Cambio, Banco Central, cache e circuit breaker |
| `product-service` | 8082 | Produtos/veiculos, Feign, cache e circuit breaker |
| `order-service` | 8085 | Pedidos e composicao de servicos |

## Padroes atendidos

- Service Registry Pattern: `discovery-service`.
- Self Registration Pattern: servicos registram no Eureka.
- Externalized Configuration Pattern: `config-service` + `config-repo`.
- API Gateway Pattern: entrada pelo `gateway-service`.
- Server-Side Discovery Pattern: gateway usa rotas `lb://`.
- Client-Side Discovery Pattern: Feign em `product-service` e `order-service`.
- Database per Service Pattern: bancos separados para auth, product, currency e order.
- Service per Container Pattern: cada servico tem `Dockerfile` e entrada no `docker-compose.yml`.
- Secure Resource Access: rotas `/ws/**` exigem JWT no gateway.
- Caching & Fault Tolerance: cache + Resilience4j em cambio/produtos/pedidos.
- Actuator: todos os servicos obrigatorios expõem `/actuator/health`.

## Endpoints principais

### Auth

- `POST /auth/signup`
- `POST /auth/signin`

Aliases mantidos:

- `POST /auth/register`
- `POST /auth/login`

### Greeting

- `GET /greeting`

### Currency

- `GET /convert?source=USD&target=BRL`
- `GET /convert?source=USD&target=BRL&amount=100`

### Products

- `GET /products?targetCurrency=BRL`
- `GET /products/{id}?targetCurrency=BRL`
- `POST /ws/product` - admin
- `PUT /ws/product/{id}` - admin
- `DELETE /ws/product/{id}` - admin

### Orders

- `POST /ws/orders`
- `GET /ws/orders/BRL`

## JWT

O `auth-service` gera tokens com:

- `userId`
- `role`

O `gateway-service` valida o JWT nas rotas `/ws/**` e injeta:

- `X-User-Id`
- `X-User-Role`

Rotas administrativas de produto exigem `role=ADMIN`.

## Config Service

Localmente, o `config-service` usa o proprio repositorio do projeto como fonte Git:

```bash
cd config-service
mvn spring-boot:run
```

No Docker Compose, o profile `native` monta a pasta `config-repo` para simplificar o uso local. Para usar um Git remoto real, execute o `config-service` com:

```bash
SPRING_PROFILES_ACTIVE=git
CONFIG_REPO_URI=https://github.com/seu-usuario/seu-config-repo.git
```

## Rodando com Docker

```bash
docker compose up --build
```

Acessos:

- Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- Config Service: `http://localhost:8888`

## Rodando manualmente

Suba primeiro o PostgreSQL:

```bash
docker compose up -d postgres-db db-initializer
```

Depois rode os servicos nesta ordem:

```bash
cd discovery-service && mvn spring-boot:run
cd config-service && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd greeting-service && mvn spring-boot:run
cd currency-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd gateway-service && mvn spring-boot:run
```

## Exemplo de login

```json
{
  "username": "admin",
  "email": "admin@jlpg.com",
  "password": "senha123",
  "role": "ADMIN"
}
```

Use `POST /auth/signup` para criar e `POST /auth/signin` para obter o token.
