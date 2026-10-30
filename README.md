# Industrial Maintenance API

API REST para gerenciamento de manutenção industrial, desenvolvida com Java e Spring Boot. O sistema permite organizar equipamentos, controlar ordens de serviço, acompanhar manutenções preventivas e consultar informações sobre custos de manutenção.

O projeto foi desenvolvido com foco em boas práticas de programação, organização do código, validação de dados, tratamento de exceções e testes automatizados.

## Funcionalidades

- **Gerenciamento de equipamentos:** cadastro, consulta, atualização e exclusão de equipamentos industriais.
- **Ordens de serviço:** abertura, acompanhamento e conclusão de ordens de serviço, com controle de status.
- **Manutenção preventiva:** organização e acompanhamento das manutenções programadas.
- **Histórico de manutenção:** registro de informações relacionadas às intervenções realizadas.
- **Controle de peças:** registro das peças utilizadas nas ordens de serviço.
- **Relatórios de custos:** consulta dos custos de peças por ordem de serviço e dos custos totais de manutenção.
- **Validação de dados:** verificação dos dados recebidos pela API.
- **Tratamento de exceções:** respostas de erro padronizadas para diferentes situações.
- **Documentação interativa:** exploração e teste dos endpoints por meio do Swagger UI.
- **Testes automatizados:** testes para verificar o comportamento dos componentes da aplicação.

## Tecnologias utilizadas

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- MockMvc
- Springdoc OpenAPI / Swagger UI

## Arquitetura

A aplicação segue uma organização modular, separando responsabilidades entre entidades, repositórios, serviços, controladores e objetos de transferência de dados (DTOs).

- **Controllers:** recebem as requisições HTTP e disponibilizam os endpoints.
- **Services:** implementam as regras de negócio.
- **Repositories:** realizam o acesso e a persistência dos dados.
- **Entities:** representam os dados persistidos no banco de dados.
- **DTOs:** definem os dados recebidos e devolvidos pela API.
- **Exception handling:** centraliza o tratamento de erros e as respostas de exceção.

## Pré-requisitos

Antes de executar o projeto, instale:

- Java Development Kit (JDK) 21.
- PostgreSQL.
- Git, para clonar o repositório.

O projeto inclui o Maven Wrapper, utilizado para executar os comandos Maven sem precisar instalar o Maven separadamente.

## Configuração do banco de dados

A aplicação utiliza PostgreSQL para persistência dos dados.

1. Instale e inicie o PostgreSQL.
2. Crie um banco de dados chamado `maintenance_system`.
3. Configure a variável de ambiente `DB_PASSWORD` com a senha do usuário PostgreSQL.
4. Verifique se o usuário configurado em `spring.datasource.username` possui permissão de acesso ao banco.

A conexão é configurada no arquivo `src/main/resources/application.properties`.

Por padrão, a aplicação utiliza:

- **Host:** localhost
- **Porta:** 5432
- **Banco de dados:** maintenance_system
- **Usuário:** postgres
- **Senha:** fornecida pela variável de ambiente `DB_PASSWORD`

O Hibernate está configurado para atualizar automaticamente
o esquema do banco de dados durante a execução da aplicação.

## Como executar

### 1. Clonar o repositório

```bash
git clone https://github.com/Copxyz/industrial-maintenance-api.git
cd industrial-maintenance-api
```

### 2. Configurar o banco de dados

Certifique-se de que o PostgreSQL esteja em execução e que o banco `maintenance_system` tenha sido criado.

Configure a variável de ambiente `DB_PASSWORD` com a senha do usuário PostgreSQL.

No Windows PowerShell, No Windows PowerShell, configure a variável de ambiente com a senha do PostgreSQL:

```powershell
$env:DB_PASSWORD="sua_senha_do_postgresql"
```

### 3. Executar a aplicação

No Windows PowerShell, execute a aplicação com o Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternativamente, importe o projeto em uma IDE de sua preferência e execute a classe principal `IndustrialMaintenanceApiApplication`, certificando-se de que a variável de ambiente `DB_PASSWORD` esteja configurada no ambiente de execução.

Após a inicialização, a API estará disponível em:

```text
http://localhost:8080
```

A aplicação utiliza a porta `8080` por padrão, salvo se houver outra configuração.

## Documentação da API

Com a aplicação em execução, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Especificação OpenAPI:** http://localhost:8080/v3/api-docs

A interface Swagger permite consultar os endpoints disponíveis, visualizar os parâmetros e modelos de dados e realizar requisições HTTP para experimentar a API.

## Endpoints da API

A API disponibiliza endpoints REST organizados por módulos funcionais.

### Equipamentos (Equipments)

| Método | Endpoint | Descrição |
|:---:|---|---|
| POST | `/api/equipments` | Cadastrar equipamento |
| GET | `/api/equipments` | Listar equipamentos |
| GET | `/api/equipments/{id}` | Consultar equipamento por ID |
| PUT | `/api/equipments/{id}` | Atualizar equipamento |
| DELETE | `/api/equipments/{id}` | Excluir equipamento |

### Ordens de serviço (Work orders)

| Método | Endpoint | Descrição |
|:---:|---|---|
| POST | `/api/work-orders` | Abrir ordem de serviço |
| GET | `/api/work-orders` | Listar ordens de serviço |
| GET | `/api/work-orders/{id}` | Consultar ordem por ID |
| GET | `/api/work-orders/status/{status}` | Listar ordens por status |
| GET | `/api/work-orders/equipment/{equipmentId}` | Listar ordens por equipamento |
| PATCH | `/api/work-orders/{id}/start` | Iniciar ordem de serviço |
| PATCH | `/api/work-orders/{id}/complete` | Concluir ordem de serviço |

### Manutenção preventiva (Preventive maintenances)

| Método | Endpoint | Descrição |
|:---:|---|---|
| POST | `/api/preventive-maintenances` | Cadastrar manutenção preventiva |
| GET | `/api/preventive-maintenances` | Listar manutenções preventivas |
| GET | `/api/preventive-maintenances/{id}` | Consultar manutenção por ID |
| GET | `/api/preventive-maintenances/overdue` | Listar manutenções atrasadas |
| PATCH | `/api/preventive-maintenances/{id}/complete` | Concluir manutenção preventiva |
| PATCH | `/api/preventive-maintenances/{id}/cancel` | Cancelar manutenção preventiva |

### Peças utilizadas (Part usages)

| Método | Endpoint | Descrição |
|:---:|---|---|
| POST | `/api/part-usages` | Registrar peça utilizada |
| GET | `/api/part-usages/work-order/{workOrderId}` | Consultar peças utilizadas em uma ordem de serviço |

### Histórico de manutenção (Maintenance-records)

| Método | Endpoint | Descrição |
|:---:|---|---|
| POST | `/api/maintenance-records` | Registrar intervenção de manutenção |
| GET | `/api/maintenance-records/work-order/{workOrderId}` | Consultar histórico de manutenção de uma ordem de serviço |

### Custos de manutenção (Maintenance-costs)
    
| Método | Endpoint | Descrição |
|:---:|---|---|
| GET | `/api/maintenance-costs` | Consultar relatório geral de custos |
| GET | `/api/maintenance-costs/work-order/{workOrderId}` | Consultar custos de uma ordem de serviço |

O cálculo dos custos considera o valor unitário multiplicado pela quantidade de peças utilizadas. Custos de mão de obra não estão incluídos.

Consulte a documentação interativa para obter detalhes sobre parâmetros, modelos de dados, códigos HTTP e exemplos de requisições e respostas.

## Testes automatizados

O projeto possui testes automatizados desenvolvidos com JUnit 5, Mockito e as ferramentas de teste do Spring Boot.

Os testes abrangem diferentes camadas da aplicação:

- **Testes unitários:** verificam as regras de negócio e o comportamento dos serviços, utilizando Mockito para simular dependências.
- **Testes de controladores:** verificam endpoints HTTP, códigos de resposta, validações e tratamento de requisições utilizando MockMvc.
- **Teste de contexto:** verifica a inicialização do contexto da aplicação.

Para executar a suíte de testes e verificar a compilação do projeto, utilize:

```powershell
.\mvnw.cmd clean verify
```

O comando executa as etapas de compilação, testes e verificações configuradas no Maven.

**Status dos testes:** 80 testes automatizados aprovados na última execução registrada.

## Objetivos do projeto

Este projeto tem como objetivos praticar e demonstrar conhecimentos em:

- Desenvolvimento de APIs REST com Java e Spring Boot.
- Persistência de dados relacionais com PostgreSQL.
- Aplicação de regras de negócio.
- Validação de entradas e tratamento de exceções.
- Testes automatizados.
- Documentação de APIs com OpenAPI.
- Organização e manutenção de código.

## Licença

Este projeto ainda não possui uma licença de distribuição definida.