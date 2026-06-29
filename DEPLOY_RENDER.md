# Deploy no Render - JLPG Motors Backend

Este arquivo esta atualizado para a arquitetura nova de microservicos.

## Servicos

Ordem recomendada:

1. `discovery-service` - porta 8761
2. `config-service` - porta 8888
3. `auth-service` - porta 8081
4. `greeting-service` - porta 8086
5. `currency-service` - porta 8084
6. `product-service` - porta 8082
7. `order-service` - porta 8085
8. `gateway-service` - porta 8080

O app/frontend deve consumir o `gateway-service`.

## Bancos PostgreSQL

Crie bases separadas:

- `auth_db`
- `product_db`
- `currency_db`
- `order_db`

`customer_db` so e necessario se o servico legado `customer-service` tambem for publicado.

## Variaveis comuns

Use em todos os servicos que registram no Eureka:

```bash
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://<url_discovery>:8761/eureka/
CONFIG_SERVER_URL=http://<url_config>:8888
```

Use em `auth-service` e `gateway-service`:

```bash
JWT_SECRET=minha_chave_secreta_muito_segura_e_longa_para_o_jwt_jlpg_motors
JWT_EXPIRATION=86400000
```

## Config Service com Git remoto

Para cumprir a configuracao externalizada via Git remoto:

```bash
SPRING_PROFILES_ACTIVE=git
CONFIG_REPO_URI=https://github.com/seu-usuario/seu-config-repo.git
CONFIG_REPO_SEARCH_PATHS=config-repo
CONFIG_REPO_LABEL=main
```

No Docker local, o projeto usa `SPRING_PROFILES_ACTIVE=native` e monta a pasta `config-repo`.

## Variaveis de banco

### auth-service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/auth_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
```

### product-service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/product_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
```

### currency-service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/currency_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
```

### order-service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/order_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
```

## Endpoints via gateway

- `POST /auth/signup`
- `POST /auth/signin`
- `GET /greeting`
- `GET /convert?source=USD&target=BRL`
- `GET /products?targetCurrency=BRL`
- `GET /products/{id}?targetCurrency=BRL`
- `POST /ws/product` - ADMIN
- `PUT /ws/product/{id}` - ADMIN
- `DELETE /ws/product/{id}` - ADMIN
- `POST /ws/orders` - JWT valido
- `GET /ws/orders/BRL` - JWT valido
