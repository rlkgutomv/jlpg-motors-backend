# Deploy no Render — JLPG Motors Backend

## Ajustes feitos nesta versão
1. Login aceita email OU username
2. Login retorna token + id + username + email + role
3. CORS liberado para o app mobile
4. GET /vehicles é público (sem token)
5. docker-compose com customer_db incluído

## Ordem de deploy no Render

### 1. Banco de dados PostgreSQL
Crie 3 bancos no Render (PostgreSQL):
- `auth_db`
- `vehicle_db`  
- `customer_db`

### 2. Variáveis de ambiente para cada serviço

**auth-service:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/auth_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://<url_discovery>:8761/eureka/
```

**vehicle-service:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/vehicle_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://<url_discovery>:8761/eureka/
```

**customer-service:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host_render>/customer_db
SPRING_DATASOURCE_USERNAME=<usuario_render>
SPRING_DATASOURCE_PASSWORD=<senha_render>
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://<url_discovery>:8761/eureka/
```

### 3. Ordem de inicialização
1. discovery-server (porta 8761)
2. auth-service (porta 8081)
3. vehicle-service (porta 8082)
4. customer-service (porta 8083)
5. gateway-server (porta 8080) — este é o que o app mobile usa!

## Credenciais de teste
- Admin: `adm@jlpg.com` / `senha123`
- User: `comum@jlpg.com` / (senha)

## Endpoints principais (via gateway na porta 8080)
- POST /auth-service/auth/register
- POST /auth-service/auth/login
- GET  /vehicle-service/vehicles (público)
- POST /vehicle-service/vehicles (ADMIN)
- POST /customer-service/negotiations/{vehicleId} (USER)
- GET  /customer-service/negotiations/my-chats (USER)
- POST /customer-service/favorites/{vehicleId} (USER)
- GET  /customer-service/favorites (USER)
