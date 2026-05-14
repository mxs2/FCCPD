# QUESTÃO 01 | Docker & Aplicação Monolítica

## Objetivo

Padronizar o ambiente da aplicação para garantir deploy consistente entre desenvolvimento e produção.

---

## Dockerfile — Serviço de Pagamentos

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --only=production

COPY . .

EXPOSE 3000

CMD ["node", "server.js"]
```

---

## Arquitetura da Aplicação

```mermaid
%%{
  init: {
    "theme": "base",
    "themeVariables": {
      "background": "#ffffff",
      "primaryColor": "#f8fafc",
      "primaryBorderColor": "#cbd5e1",
      "primaryTextColor": "#0f172a",
      "lineColor": "#475569",
      "fontSize": "14px",
      "fontFamily": "Inter, sans-serif"
    }
  }
}%%

flowchart LR

    USER["Usuário"]

    subgraph PLATFORM["Infraestrutura Docker"]
        WEB["Frontend Web"]
        PAY["Serviço de Pagamentos"]
        REP["Serviço de Relatórios"]
        DB["Banco de Dados"]
    end

    GATEWAY["Gateway de Pagamento"]

    USER --> WEB

    WEB --> PAY
    WEB --> REP

    PAY --> DB
    PAY --> GATEWAY



    classDef default fill:#ffffff,color:#0f172a,stroke:#cbd5e1,stroke-width:1px
    classDef external fill:#111827,color:#ffffff,stroke:#111827

    class GATEWAY external
```

---

## Principais Pontos

* Docker garante ambientes consistentes.
* Deploy mais rápido e previsível.
* Dados persistentes devem utilizar volumes.
* Credenciais não devem ficar na imagem.

---

# QUESTÃO 02 | Persistência Local

## Objetivo

Conectar a aplicação Python ao PostgreSQL com persistência segura dos dados.

---

## Comandos Docker

```bash
docker network create rede_dados

docker volume create pg_persist

docker run -d \
  --name db \
  --network rede_dados \
  -v pg_persist:/var/lib/postgresql/data \
  postgres

docker run -d \
  --name app_python \
  --network rede_dados \
  minha_analise_python
```

---

## Estrutura da Solução

```mermaid
%%{
  init: {
    "theme": "base",
    "themeVariables": {
      "background": "#ffffff",
      "primaryColor": "#f8fafc",
      "primaryBorderColor": "#cbd5e1",
      "primaryTextColor": "#0f172a",
      "lineColor": "#475569",
      "fontSize": "14px",
      "fontFamily": "Inter, sans-serif"
    }
  }
}%%

flowchart LR

    subgraph HOST["Docker Host"]

        subgraph NETWORK["Rede Interna"]
            PY["Aplicação Python"]
            PG["PostgreSQL"]
        end

        VOL["Volume Persistente"]
    end

    PY --> PG
    PG --> VOL
```

---

## Benefícios

* Comunicação simples entre containers.
* Persistência segura dos dados.
* Estrutura desacoplada e reutilizável.

---

# QUESTÃO 03 | Arquitetura Cloud AWS

## Objetivo

Distribuir vídeos globalmente com alta disponibilidade e baixa latência.

---

## Arquitetura Proposta

```mermaid
%%{
  init: {
    "theme": "base",
    "themeVariables": {
      "background": "#ffffff",
      "primaryColor": "#f8fafc",
      "primaryBorderColor": "#cbd5e1",
      "primaryTextColor": "#0f172a",
      "lineColor": "#475569",
      "fontSize": "14px",
      "fontFamily": "Inter, sans-serif"
    }
  }
}%%

flowchart LR

    USER["Usuário"]

    CDN["CloudFront CDN"]
    S3["S3 Storage"]
    LAMBDA["Lambda Processing"]
    DDB["DynamoDB"]
    AUTH["Cognito"]

    USER --> CDN
    CDN --> S3

    S3 --> LAMBDA
    LAMBDA --> DDB

    USER --> AUTH
```

---

## Benefícios da Solução

| Serviço    | Benefício                 |
| ---------- | ------------------------- |
| S3         | Armazenamento escalável   |
| CloudFront | Baixa latência global     |
| Lambda     | Escalabilidade automática |
| Cognito    | Gestão segura de usuários |

---

# QUESTÃO 04 | Parecer Técnico — Saúde

## Recomendação

Adotar **PaaS (Platform as a Service)**.

---

## Justificativa

### Redução Operacional

Menor esforço com manutenção de infraestrutura.

### Segurança

Responsabilidade compartilhada:

* Provedor → infraestrutura;
* Empresa → acessos e dados.

### Alta Disponibilidade

Uso de múltiplas zonas redundantes.

### Estratégia Recomendada

Modelo híbrido:

* dados sensíveis localmente;
* aplicações e processamento na nuvem.

---

# QUESTÃO 05 | Microsserviços & Mensageria

## Objetivo

Separar responsabilidades para melhorar escalabilidade e resiliência.

---

## Arquitetura de Microsserviços

```mermaid
%%{
  init: {
    "theme": "base",
    "themeVariables": {
      "background": "#ffffff",
      "primaryColor": "#f8fafc",
      "primaryBorderColor": "#cbd5e1",
      "primaryTextColor": "#0f172a",
      "lineColor": "#475569",
      "secondaryColor": "#eef2ff",
      "tertiaryColor": "#ffffff",
      "fontSize": "14px",
      "fontFamily": "Inter, sans-serif"
    }
  }
}%%

flowchart LR

    CLIENT["Cliente"]

    subgraph EDGE["Entry Layer"]
        API["API Gateway"]
    end

    subgraph SERVICES["Core Services"]
        CAT["Catalog Service"]
        ORD["Order Service"]
        PAY["Payment Service"]
        NOT["Notification Service"]
    end

    subgraph MESSAGING["Async Communication"]
        SQS["SQS Queue"]
        DLQ["Dead Letter Queue"]
    end

    CLIENT --> API

    API --> CAT
    API --> ORD

    ORD --> PAY

    ORD -- Events --> SQS

    SQS --> NOT

    SQS -. Failed Messages .-> DLQ



    classDef gateway fill:#111827,color:#ffffff,stroke:#111827,stroke-width:1px
    classDef service fill:#ffffff,color:#0f172a,stroke:#cbd5e1,stroke-width:1px
    classDef queue fill:#f8fafc,color:#334155,stroke:#94a3b8,stroke-width:1px
    classDef error fill:#fef2f2,color:#991b1b,stroke:#dc2626,stroke-width:1px

    class API gateway
    class CAT,ORD,PAY,NOT service
    class SQS queue
    class DLQ error
```

---

## Benefícios

### Escalabilidade

Cada serviço pode crescer independentemente.

### Resiliência

Falhas em notificações não afetam pedidos.

### Baixo Acoplamento

Serviços mais independentes e fáceis de evoluir.

### Continuidade

Mensagens críticas permanecem armazenadas até reprocessamento.

---

# Conclusão

A solução proposta entrega:

* padronização com Docker;
* escalabilidade em cloud;
* arquitetura resiliente;
* foco em disponibilidade e segurança.

Resultado:
uma plataforma preparada para crescimento sustentável e manutenção simplificada.
