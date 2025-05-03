# [Link do backend da nuvem](https://inarisys.koyeb.app/)

# Projeto Univesp PI 2025 S1 - Backend

Este projeto é o backend da aplicação desenvolvida para o Projeto Integrador da Univesp (Turma 2025 S1).

---

## ✅ Pré-requisitos

- Java 17 ou superior
- Maven (ou usar o `mvnw.cmd` que já vem no projeto)
- PostgreSQL
- Make (em ambientes Windows, recomenda-se o uso via Git Bash, WSL ou Make para Windows)

---

## ⚙️ Configuração do banco de dados

1. Suba uma instância do PostgreSQL localmente. Exemplo com Docker:

   ```bash
   docker run --name postgres-inarisys \
     -e POSTGRES_DB=inarisys \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5432:5432 \
     -d postgres:15
   ```

2. No projeto, edite o arquivo `src/main/resources/application-local.properties` para apontar para sua instância do banco. Exemplo:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   spring.datasource.driver-class-name=org.postgresql.Driver
   ```

---

## 🗃️ Carga do banco (schema e dados iniciais)

1. Conecte-se ao banco `inarisys` criado no passo anterior.

2. Execute os arquivos localizados em `src/main/resources`:

    - `schema.sql` → cria as tabelas do sistema
    - `data.sql` → insere os perfis e o usuário admin

Você pode usar pgAdmin, DBeaver, psql ou qualquer outro cliente de banco de dados.

---

## ▶️ Rodando o projeto localmente

Para rodar o projeto com o profile `local`, execute no terminal:

```bash
make run
```

Esse comando faz:
- Build do projeto com Maven
- Geração do `.jar`
- Execução com o profile `local`

---

## 🔐 Login padrão

- **Email:** `admin`
- **Senha:** `admininarisys2025`

---

## 🛠️ Comandos úteis

```bash
make            # build + run com profile local
make build      # apenas compila o projeto
make clean      # limpa os arquivos gerados pelo Maven
```

---

## 📂 Estrutura do projeto

- `src/main/java`: código fonte da aplicação
- `src/main/resources/schema.sql`: DDL com as tabelas do banco
- `src/main/resources/data.sql`: DML com dados iniciais
- `Makefile`: comandos automatizados para build e execução

---

## 🧪 Testes com curl

### 🔑 Login

```bash
curl --location 'http://localhost:8080/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{"email":"admin","senha":"admininarisys2025"}'
```

Isso retorna um token JWT. Copie ele para testar a autenticação nos endpoints protegidos.

### ✅ Teste de endpoint autenticado

```bash
curl --location 'http://localhost:8080/hello' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer <SEU_TOKEN_JWT_AQUI>'
```

* Substitua `<SEU_TOKEN_JWT_AQUI>` pelo token retornado no login.

Se tudo funcionar, deve ser retornada a mensagem `Univesp - PI 2025 S1 - T4 S3`
