# 📦 Order Service — Microsserviço de Pedidos

Microsserviço responsável pela **criação, gerenciamento do ciclo de vida dos pedidos e publicação resiliente de eventos**, utilizando o padrão arquitetural **Transactional Outbox Pattern** para garantir a consistência entre o banco de dados e a comunicação assíncrona via RabbitMQ.

## 🏛️ Arquitetura e Padrões

### Transactional Outbox Pattern

Para garantir que os eventos sejam publicados de forma confiável, o serviço utiliza o padrão Transactional Outbox.

Ao criar um pedido, duas operações são realizadas dentro da mesma transação SQL (`@Transactional`):

1. Persistência do pedido na tabela `tb_pedidos`.
2. Persistência do evento correspondente na tabela `tb_outbox`.

Dessa forma, caso ocorra uma falha durante a operação, ambas as alterações são revertidas, evitando inconsistências entre o banco de dados e os eventos que precisam ser publicados.

### Outbox Worker (Polling)

Uma tarefa agendada utilizando `@Scheduled` atua como um worker responsável por:

- Consultar periodicamente os eventos com status `PENDENTE` na tabela `tb_outbox`.
- Publicar os eventos na exchange `orders.exchange` do RabbitMQ.
- Atualizar o status dos eventos após a publicação bem-sucedida.

Esse mecanismo permite desacoplar a criação dos pedidos da publicação dos eventos, aumentando a confiabilidade da comunicação entre microsserviços.

### Consumo de Respostas de Pagamento

O serviço também atua como consumidor de eventos, escutando a fila `order.payment-response.queue`, vinculada à exchange `payments.exchange`.

Após receber uma resposta do serviço de pagamentos, o pedido tem seu status atualizado conforme o resultado:

| Evento recebido | Status do pedido |
|---|---|
| Pagamento aprovado | `PAGO` |
| Pagamento recusado | `CANCELADO` |

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 21 | Linguagem de programação |
| Spring Boot 3 | Framework principal |
| Spring Data JPA | Persistência de dados |
| PostgreSQL | Banco de dados relacional |
| Spring AMQP | Integração com RabbitMQ |
| RabbitMQ | Mensageria e comunicação assíncrona |
| Flyway | Versionamento e gerenciamento do schema |

---

## 🚀 API REST

### Criar Pedido

`POST /pedidos`

Cria um novo pedido e registra o evento correspondente na Outbox para publicação assíncrona.

**Request Body**

```json
{
  "customerId": "usr-123",
  "totalAmount": 250.00
}
```

**Response — `202 Accepted` ou `201 Created`**

```json
{
  "id": "e4a91b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c",
  "customerId": "usr-123",
  "totalAmount": 250.00,
  "status": "PENDENTE"
}
```

> O pedido é criado inicialmente com o status `PENDENTE`. Sua confirmação depende do processamento assíncrono do pagamento.

---

## 🐇 Configuração do RabbitMQ

### Mensageria de saída

Responsável pela publicação dos eventos de criação de pedidos.

| Configuração | Valor |
|---|---|
| Exchange | `orders.exchange` |
| Tipo | `Topic` |
| Routing Key | `order.created` |

### Mensageria de entrada

Responsável pelo recebimento das respostas do serviço de pagamentos.

| Configuração | Valor |
|---|---|
| Exchange | `payments.exchange` |
| Queue | `order.payment-response.queue` |

---

## 🔄 Fluxo de Processamento

1. O cliente realiza uma requisição `POST /pedidos`.
2. O Order Service cria o pedido e registra o evento na tabela `tb_outbox`, dentro da mesma transação.
3. O Outbox Worker identifica os eventos pendentes.
4. O evento `order.created` é publicado no RabbitMQ.
5. O serviço de pagamentos consome o evento e processa o pagamento.
6. O serviço de pagamentos publica uma resposta na exchange `payments.exchange`.
7. O Order Service consome a resposta e atualiza o status do pedido para `PAGO` ou `CANCELADO`.

---

## 🎯 Objetivos Arquiteturais

- Garantir consistência entre persistência e publicação de eventos.
- Evitar perda de mensagens em cenários de falha.
- Promover baixo acoplamento entre microsserviços.
- Implementar comunicação assíncrona baseada em eventos.
- Garantir maior resiliência e confiabilidade no processamento de pedidos.

---

**Projeto desenvolvido com foco em arquitetura de microsserviços, mensageria assíncrona e padrões de consistência distribuída.**
