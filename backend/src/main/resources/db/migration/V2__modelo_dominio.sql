-- Modelo de domínio do Lav.Cametá (PDF seções 5–7) com ajustes documentados em
-- docs/banco-de-dados/modelagem.md: etapas de ciclo (combo), idempotência de eventos,
-- rastreio de comandos de máquina e recuperação de senha.
-- Datas gravadas em UTC (DATETIME(6)); a aplicação converte para America/Belem.

CREATE TABLE usuarios (
    id               CHAR(36)     NOT NULL,
    nome             VARCHAR(120) NOT NULL,
    email            VARCHAR(150) NULL,
    telefone         VARCHAR(20)  NULL,
    senha_hash       VARCHAR(255) NULL,
    google_id        VARCHAR(100) NULL,
    tipo             ENUM('CLIENTE','ADMIN') NOT NULL,
    tentativas_login INT          NOT NULL DEFAULT 0,
    bloqueado_ate    DATETIME(6)  NULL,
    criado_em        DATETIME(6)  NOT NULL,
    atualizado_em    DATETIME(6)  NOT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT uk_usuarios_telefone UNIQUE (telefone),
    CONSTRAINT uk_usuarios_google UNIQUE (google_id),
    CONSTRAINT ck_usuarios_identificacao CHECK (email IS NOT NULL OR telefone IS NOT NULL),
    CONSTRAINT ck_usuarios_credencial CHECK (senha_hash IS NOT NULL OR google_id IS NOT NULL)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE codigos_recuperacao_senha (
    id          CHAR(36)    NOT NULL,
    usuario_id  CHAR(36)    NOT NULL,
    codigo_hash CHAR(64)    NOT NULL,
    tentativas  INT         NOT NULL DEFAULT 0,
    expira_em   DATETIME(6) NOT NULL,
    usado_em    DATETIME(6) NULL,
    criado_em   DATETIME(6) NOT NULL,
    CONSTRAINT pk_codigos_recuperacao PRIMARY KEY (id),
    CONSTRAINT fk_codigos_recuperacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    INDEX ix_codigos_recuperacao_usuario (usuario_id, criado_em)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE maquinas (
    id                INT          NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(50)  NOT NULL,
    tipo              ENUM('LAVADORA','SECADORA') NOT NULL,
    capacidade_kg     DECIMAL(4,1) NOT NULL,
    status            ENUM('DISPONIVEL','EM_USO','MANUTENCAO') NOT NULL,
    minutos_restantes INT          NULL,
    recursos          VARCHAR(200) NULL,
    criado_em         DATETIME(6)  NOT NULL,
    atualizado_em     DATETIME(6)  NOT NULL,
    CONSTRAINT pk_maquinas PRIMARY KEY (id),
    CONSTRAINT uk_maquinas_nome UNIQUE (nome),
    CONSTRAINT ck_maquinas_capacidade CHECK (capacidade_kg > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE servicos (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        ENUM('LAVAR','SECAR','LAVAR_SECAR') NOT NULL,
    descricao   VARCHAR(200) NOT NULL,
    preco       DECIMAL(8,2) NOT NULL,
    duracao_min INT          NOT NULL,
    CONSTRAINT pk_servicos PRIMARY KEY (id),
    CONSTRAINT uk_servicos_nome UNIQUE (nome),
    CONSTRAINT ck_servicos_preco CHECK (preco > 0),
    CONSTRAINT ck_servicos_duracao CHECK (duracao_min > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE pedidos (
    id            CHAR(36)     NOT NULL,
    usuario_id    CHAR(36)     NOT NULL,
    maquina_id    INT          NOT NULL,
    servico_id    INT          NOT NULL,
    valor_total   DECIMAL(8,2) NOT NULL,
    status        ENUM('AGUARDANDO_PAGAMENTO','PAGO','EM_ANDAMENTO','CONCLUIDO','CANCELADO','EXPIRADO','ESTORNADO') NOT NULL,
    criado_em     DATETIME(6)  NOT NULL,
    expira_em     DATETIME(6)  NOT NULL,
    atualizado_em DATETIME(6)  NOT NULL,
    CONSTRAINT pk_pedidos PRIMARY KEY (id),
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_pedidos_maquina FOREIGN KEY (maquina_id) REFERENCES maquinas (id),
    CONSTRAINT fk_pedidos_servico FOREIGN KEY (servico_id) REFERENCES servicos (id),
    INDEX ix_pedidos_usuario (usuario_id, criado_em),
    INDEX ix_pedidos_maquina_status (maquina_id, status),
    INDEX ix_pedidos_status_expira (status, expira_em)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE pagamentos (
    id              CHAR(36)     NOT NULL,
    pedido_id       CHAR(36)     NOT NULL,
    metodo          ENUM('PIX','CARTAO_CREDITO','CARTAO_DEBITO') NOT NULL,
    valor           DECIMAL(8,2) NOT NULL,
    status          ENUM('PENDENTE','APROVADO','RECUSADO','EXPIRADO','ESTORNO_PENDENTE','ESTORNADO') NOT NULL,
    provedor        VARCHAR(30)  NOT NULL,
    codigo_pix      TEXT         NULL,
    checkout_url    VARCHAR(500) NULL,
    transacao_id    VARCHAR(100) NULL,
    motivo          VARCHAR(255) NULL,
    pago_em         DATETIME(6)  NULL,
    estornado_em    DATETIME(6)  NULL,
    criado_em       DATETIME(6)  NOT NULL,
    atualizado_em   DATETIME(6)  NOT NULL,
    -- RN13: no máximo um pagamento aprovado (ou derivado de aprovação) por pedido.
    pedido_aprovado CHAR(36) GENERATED ALWAYS AS (
        CASE WHEN status IN ('APROVADO','ESTORNO_PENDENTE','ESTORNADO') THEN pedido_id END
    ) STORED,
    CONSTRAINT pk_pagamentos PRIMARY KEY (id),
    CONSTRAINT fk_pagamentos_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT uk_pagamentos_transacao UNIQUE (provedor, transacao_id),
    CONSTRAINT uk_pagamentos_um_aprovado UNIQUE (pedido_aprovado),
    INDEX ix_pagamentos_pedido (pedido_id),
    INDEX ix_pagamentos_status_pago (status, pago_em)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Idempotência de notificações do gateway: o mesmo evento nunca é processado duas vezes.
CREATE TABLE eventos_pagamento (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    provedor     VARCHAR(30)  NOT NULL,
    evento_id    VARCHAR(100) NOT NULL,
    transacao_id VARCHAR(100) NOT NULL,
    tipo         VARCHAR(40)  NOT NULL,
    recebido_em  DATETIME(6)  NOT NULL,
    CONSTRAINT pk_eventos_pagamento PRIMARY KEY (id),
    CONSTRAINT uk_eventos_pagamento UNIQUE (provedor, evento_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Combo (RN08): um pedido pode gerar uma etapa de lavagem e uma de secagem.
CREATE TABLE ciclos (
    id              CHAR(36)    NOT NULL,
    pedido_id       CHAR(36)    NOT NULL,
    maquina_id      INT         NOT NULL,
    etapa           ENUM('LAVAGEM','SECAGEM') NOT NULL,
    status          ENUM('EM_ANDAMENTO','CONCLUIDO','FALHOU') NOT NULL,
    inicio_em       DATETIME(6) NOT NULL,
    fim_previsto_em DATETIME(6) NOT NULL,
    fim_em          DATETIME(6) NULL,
    CONSTRAINT pk_ciclos PRIMARY KEY (id),
    CONSTRAINT fk_ciclos_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT fk_ciclos_maquina FOREIGN KEY (maquina_id) REFERENCES maquinas (id),
    CONSTRAINT uk_ciclos_pedido_etapa UNIQUE (pedido_id, etapa),
    INDEX ix_ciclos_status_fim (status, fim_previsto_em),
    INDEX ix_ciclos_maquina (maquina_id, status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Rastreio de comandos enviados ao equipamento (AD-005).
CREATE TABLE comandos_maquina (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    ciclo_id           CHAR(36)     NULL,
    maquina_id         INT          NOT NULL,
    comando            ENUM('INICIAR') NOT NULL,
    status             ENUM('CONFIRMADO','FALHOU') NOT NULL,
    chave_idempotencia VARCHAR(120) NOT NULL,
    erro               VARCHAR(300) NULL,
    criado_em          DATETIME(6)  NOT NULL,
    CONSTRAINT pk_comandos_maquina PRIMARY KEY (id),
    CONSTRAINT fk_comandos_maquina FOREIGN KEY (maquina_id) REFERENCES maquinas (id),
    CONSTRAINT fk_comandos_ciclo FOREIGN KEY (ciclo_id) REFERENCES ciclos (id),
    CONSTRAINT uk_comandos_idempotencia UNIQUE (chave_idempotencia)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE metodos_pagamento (
    id            CHAR(36)     NOT NULL,
    usuario_id    CHAR(36)     NOT NULL,
    token_gateway VARCHAR(150) NOT NULL,
    bandeira      VARCHAR(20)  NULL,
    ultimos4      CHAR(4)      NULL,
    criado_em     DATETIME(6)  NOT NULL,
    CONSTRAINT pk_metodos_pagamento PRIMARY KEY (id),
    CONSTRAINT fk_metodos_pagamento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    INDEX ix_metodos_pagamento_usuario (usuario_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
