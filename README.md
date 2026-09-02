# Runna

API REST para registro e acompanhamento de exercícios físicos (corrida, caminhada e bike), com autenticação JWT stateless. Cada usuário acessa apenas os próprios treinos, e o pace é calculado automaticamente a partir da duração e da distância.

## Stack

- **Java 25** / **Spring Boot 4.1**
- **Spring Security** — autenticação via filtro customizado
- **Spring Data JPA** / **Hibernate**
- **PostgreSQL**
- **jjwt 0.12** — emissão e validação de tokens
- **Bean Validation** — validação dos payloads de entrada
- **springdoc-openapi (Swagger UI)** — documentação interativa

## Como rodar

**Pré-requisitos:** JDK 25, Maven e uma instância do PostgreSQL.

**1. Crie o banco:**

```sql
CREATE DATABASE runna;
```

**2. Configure as variáveis de ambiente:**

```bash
export DB_PASSWORD=sua_senha_do_postgres
export JWT_SECRET=$(openssl rand -base64 48)
```

O `JWT_SECRET` precisa ter no mínimo 32 bytes — HS256 exige uma chave de 256 bits, e a aplicação recusa subir com uma chave mais curta.

**3. Suba a aplicação:**

```bash
./mvnw spring-boot:run
```

O Hibernate cria as tabelas automaticamente (`ddl-auto: update`). A API sobe em `http://localhost:8080`.

## Documentação interativa

Com a aplicação no ar, a interface do Swagger fica disponível em:

```
http://localhost:8080/swagger-ui.html
```

Para testar as rotas protegidas: crie um usuário, faça login, copie o token retornado e cole no botão **Authorize** no topo da página.

## Endpoints

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| `POST` | `/users` | — | Cria um usuário |
| `POST` | `/auth/login` | — | Autentica e retorna o token JWT |
| `POST` | `/exercises` | Bearer | Registra um exercício |
| `GET` | `/exercises` | Bearer | Lista os exercícios do usuário |
| `GET` | `/exercises/{id}` | Bearer | Busca um exercício específico |
| `DELETE` | `/exercises/{id}` | Bearer | Remove um exercício |

### Ordenação da listagem

`GET /exercises` aceita o parâmetro `ordem`, com quatro valores possíveis:

| Valor | Ordena por |
|---|---|
| `NEWEST` (padrão) | Mais recentes primeiro |
| `OLDEST` | Mais antigos primeiro |
| `BEST_PACE` | Melhor pace primeiro |
| `LONGEST_DISTANCE` | Maior distância primeiro |

### Exemplo

```bash
# Cria o usuário
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"username":"luiz","password":"senha123"}'

# Autentica
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"luiz","password":"senha123"}'

# Registra um treino
curl -X POST http://localhost:8080/exercises \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"type":"RUN","duration":"PT30M","distanceInKm":5.0}'
```

Os campos `duration` e `pace` usam o formato ISO-8601 de duração: `PT30M` são 30 minutos, `PT1H15M` são 1h15.

## Decisões de arquitetura

### Autenticação stateless

O servidor não guarda sessão. Todo o necessário para identificar o usuário está dentro do próprio token, assinado com HMAC-SHA256 — o `sub` carrega o UUID, e a assinatura garante que ninguém consegue alterá-lo sem invalidar o token.

A vantagem é que qualquer instância da API valida qualquer token, sem estado compartilhado. O custo é que **um token emitido não pode ser revogado**: se o usuário trocar a senha, o token antigo continua válido até expirar, porque a validação não consulta o banco. Por isso a expiração é curta (1 hora). Uma implementação de produção resolveria isso com refresh token ou blacklist por `jti`.

### Filtro de autenticação

O Spring Security não traz suporte nativo a JWT via header `Bearer`, então o `JwtAuthenticationFilter` preenche essa lacuna. Ele lê o header, valida o token pelo `JwtService`, extrai o UUID e o coloca no `SecurityContextHolder`.

O filtro **nunca bloqueia uma requisição**. Quando o token é inválido, ele apenas limpa o contexto e segue — quem barra é o `AuthorizationFilter`, mais adiante na cadeia, ao encontrar o contexto vazio. Essa separação é o que permite rotas públicas e protegidas conviverem na mesma configuração.

### DTOs em ambas as direções

As entidades JPA não atravessam a fronteira do service. Cada endpoint tem um record de entrada e outro de saída.

Na saída, isso impede vazamento: `ExerciseModel` referencia `UserModel`, que contém o hash da senha — serializar a entidade exporia esse campo. `ExerciseResponse` simplesmente não tem onde guardá-lo.

Na entrada, o DTO define o que o cliente pode enviar. `CreateExerciseRequest` não expõe `id`, `pace` nem `user`: o primeiro é gerado pelo banco, o segundo é calculado pelo service, e o terceiro vem do token. Aceitar a entidade diretamente permitiria ao cliente registrar um treino em nome de outra pessoa.

### Escopo por usuário no repository

Autenticação responde *quem é você*; ela não responde *isso é seu*. Um usuário autenticado que passe o UUID de um treino alheio está autenticado de forma legítima — nenhum filtro de segurança impediria o acesso.

A proteção está nas próprias queries, que escopam pelo dono:

```java
Optional<ExerciseModel> findByIdAndUser_Id(UUID id, UUID userId);
List<ExerciseModel> findAllByUser_IdOrderByPaceAsc(UUID userId);
```

Com o `AND` na query derivada, esquecer a checagem é impossível: ou o registro pertence àquele usuário, ou o `Optional` volta vazio. A alternativa — buscar por id e comparar o dono depois — depende de o desenvolvedor lembrar em todos os métodos.

Quando o recurso existe mas pertence a outra pessoa, a API responde `404` em vez de `403`, para não confirmar a existência daquele id.

### Tratamento centralizado de erros

Um `@RestControllerAdvice` traduz as exceptions em respostas HTTP, mantendo um formato único de erro em toda a API e evitando que detalhes internos vazem para o cliente.

Credenciais inválidas retornam a **mesma mensagem** tanto para usuário inexistente quanto para senha errada — mensagens distintas permitiriam enumerar quais usernames existem na base.

## Próximos passos

- Testes automatizados (unitários e de integração)
- Refresh token e revogação
- Migrations versionadas com Flyway, no lugar do `ddl-auto: update`
- Containerização com Docker
