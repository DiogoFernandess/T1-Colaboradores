# T1-Colaboradores

Descrição
---------
"T1-Colaboradores" é um projeto Java que visa facilitar o gerenciamento de colaboradores (funcionários/usuários) em aplicações empresariais. Ele fornece modelos, serviços e utilitários para criação, leitura, atualização e remoção (CRUD) de colaboradores, além de exemplos de integração com persistência e APIs REST.

Recursos
--------
- Entidades para colaboradores
- DTOs para folhas de pagamento
- Serviços com validação e regras de negócio
- Repositório JPA
- Endpoints REST 
- Suporte para testes unitários e de integração
- Arquitetura em camadas, modular e fácil de estender

Tecnologias
-----------
- Java 21
- Gradle
- Spring Boot, JPA/Hibernate, JUnit, Mockito e AssertJ
- H2/Postgres
- SpringDoc (Swagger)

Pré-requisitos
--------------
- JDK 21
- Gradle
- Executar o Docker com banco de dados PostgreSQL

Como rodar a Aplicação
------------------
1. Subir o Docker:
```bash
docker compose up -d
```

2. Rodar a aplicação:
```bash
./gradlew bootRun
```
Configuração
-----------
- Arquivo: `src/main/resources/application.properties`
- Propriedades da Aplicação:
  - server.port=8081
  - spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
  - spring.datasource.username=postgres
  - spring.datasource.password=123456
  
- Arquivo: `docker-compose.yaml`
- Postgres:
  - image: postgres:17
  - container_name: postgres
  - environment:
   - POSTGRES_USER: postgres
   - POSTGRES_PASSWORD: 123456
   - POSTGRES_DB: postgres
  - ports:
    - "5432:5432" 
  
- PgAdmin 
  - image: dpage/pgadmin4:latest
  - container_name: pgadmin4
  - environment:
    - PGADMIN_DEFAULT_EMAIL: admin@admin.com
    - PGADMIN_DEFAULT_PASSWORD: 123456
  - ports:
    - "8080:80"
  - depends_on:
    - postgres
      
Documentação dos Endpoints (Swagger)
----------------------
Para facilitar os Testes de Aplicação e fácil usabilidade dos Endpoints mesmo sem a Interface final do projeto, foi utilizamos o Swagger
```bash
localhost:8081/swagger-ui/index.html
```
Visualização do Banco de Dados (PgAdmin)
----------------------
Para facilitar a utilização do Banco de Dados PostgreSQL, utilizamos o PgAdmin, uma Interface que permite criar, editar e excluir bancos de dados, tabelas, entre outras funções
- Login:
  - email: admin@admin.com
  - password: 123456
```bash
localhost:8080
```


Testes
------
- Executar testes com Maven:
```bash
./gradlew test
```
