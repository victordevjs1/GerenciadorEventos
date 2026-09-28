-- =========================================================
-- GERENCIADOR DE EVENTOS ESCOLARES
-- Banco de dados
-- =========================================================

CREATE DATABASE IF NOT EXISTS gerenciador_eventos
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gerenciador_eventos;

SET FOREIGN_KEY_CHECKS = 0;


-- =========================================================
-- REMOVER TABELAS EXISTENTES
-- =========================================================

DROP TABLE IF EXISTS auditoria;

DROP TABLE IF EXISTS evento_agente_externo;
DROP TABLE IF EXISTS agente_externo;

DROP TABLE IF EXISTS responsabilidade_comissao;
DROP TABLE IF EXISTS responsabilidade;

DROP TABLE IF EXISTS comissao_atividade;
DROP TABLE IF EXISTS comissao_aluno;
DROP TABLE IF EXISTS comissao;

DROP TABLE IF EXISTS atividade;

DROP TABLE IF EXISTS inscricao_evento;

DROP TABLE IF EXISTS evento_professor;
DROP TABLE IF EXISTS professor;

DROP TABLE IF EXISTS evento;

DROP TABLE IF EXISTS aluno;

DROP TABLE IF EXISTS usuario;


SET FOREIGN_KEY_CHECKS = 1;


-- =========================================================
-- 1. USUARIO
-- =========================================================

CREATE TABLE usuario (
    id_usuario BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,

    tipo_usuario ENUM(
        'ADMINISTRADOR',
        'COORDENADOR',
        'GESTAO'
    ) NOT NULL DEFAULT 'ADMINISTRADOR',

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_usuario),

    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 2. ALUNO
-- =========================================================

CREATE TABLE aluno (
    id_aluno BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    rm VARCHAR(20) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    data_nascimento DATE NULL,

    serie VARCHAR(20) NULL,
    turma VARCHAR(50) NULL,
    curso VARCHAR(100) NULL,

    ano_conclusao YEAR NULL,

    email VARCHAR(150) NULL,
    telefone VARCHAR(20) NULL,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_aluno),

    UNIQUE KEY uk_aluno_rm (rm),

    KEY idx_aluno_turma (turma),
    KEY idx_aluno_curso (curso),
    KEY idx_aluno_serie (serie),
    KEY idx_aluno_ano_conclusao (ano_conclusao)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 3. EVENTO
-- =========================================================

CREATE TABLE evento (
    id_evento BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_usuario_criador BIGINT UNSIGNED NOT NULL,

    nome VARCHAR(200) NOT NULL,
    descricao TEXT NULL,

    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NULL,

    local VARCHAR(200) NULL,

    capacidade INT UNSIGNED NULL,

    status ENUM(
        'PLANEJADO',
        'ABERTO',
        'EM_ANDAMENTO',
        'ENCERRADO',
        'CANCELADO'
    ) NOT NULL DEFAULT 'PLANEJADO',

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_evento),

    KEY idx_evento_usuario (id_usuario_criador),
    KEY idx_evento_data (data_inicio),
    KEY idx_evento_status (status),

    CONSTRAINT fk_evento_usuario
        FOREIGN KEY (id_usuario_criador)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 4. PROFESSOR
-- =========================================================

CREATE TABLE professor (
    id_professor BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NULL,
    telefone VARCHAR(20) NULL,

    area_atuacao VARCHAR(100) NULL,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_professor),

    KEY idx_professor_nome (nome),
    KEY idx_professor_area (area_atuacao)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 5. EVENTO_PROFESSOR
-- =========================================================
-- Relaciona professores e eventos.
-- Um evento pode ter vários professores.
-- Um professor pode participar de vários eventos.
-- =========================================================

CREATE TABLE evento_professor (
    id_evento BIGINT UNSIGNED NOT NULL,
    id_professor BIGINT UNSIGNED NOT NULL,

    principal BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id_evento, id_professor),

    CONSTRAINT fk_evento_professor_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_evento_professor_professor
        FOREIGN KEY (id_professor)
        REFERENCES professor (id_professor)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 6. INSCRICAO_EVENTO
-- =========================================================
-- Relaciona alunos e eventos.
-- Um aluno pode se inscrever em vários eventos.
-- Um evento pode ter vários alunos.
-- =========================================================

CREATE TABLE inscricao_evento (
    id_inscricao BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_aluno BIGINT UNSIGNED NOT NULL,
    id_evento BIGINT UNSIGNED NOT NULL,

    data_inscricao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    status ENUM(
        'INSCRITO',
        'CANCELADO',
        'CONCLUIDO'
    ) NOT NULL DEFAULT 'INSCRITO',

    observacao TEXT NULL,

    PRIMARY KEY (id_inscricao),

    UNIQUE KEY uk_inscricao_aluno_evento (
        id_aluno,
        id_evento
    ),

    KEY idx_inscricao_aluno (id_aluno),
    KEY idx_inscricao_evento (id_evento),
    KEY idx_inscricao_status (status),

    CONSTRAINT fk_inscricao_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_inscricao_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- 7. PRESENCA_EVENTO
-- =========================================================
-- Registra a presença dos alunos nos eventos.
-- =========================================================

CREATE TABLE presenca_evento (
    id_presenca BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_evento BIGINT UNSIGNED NOT NULL,
    id_aluno BIGINT UNSIGNED NOT NULL,

    presente BOOLEAN NOT NULL DEFAULT FALSE,

    data_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    observacao TEXT NULL,

    PRIMARY KEY (id_presenca),

    UNIQUE KEY uk_presenca_evento_aluno (
        id_evento,
        id_aluno
    ),

    KEY idx_presenca_evento (id_evento),
    KEY idx_presenca_aluno (id_aluno),

    CONSTRAINT fk_presenca_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_presenca_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno)
        ON DELETE CASCADE
        ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
-- =========================================================
-- 7. COMISSAO
-- =========================================================
-- Cada comissão pertence a um evento.
-- =========================================================

CREATE TABLE comissao (
    id_comissao BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_evento BIGINT UNSIGNED NOT NULL,

    nome VARCHAR(150) NOT NULL,
    descricao TEXT NULL,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_comissao),

    KEY idx_comissao_evento (id_evento),

    CONSTRAINT fk_comissao_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 8. COMISSAO_ALUNO
-- =========================================================
-- Define quais alunos pertencem a cada comissão.
--
-- Um aluno pode participar de várias comissões.
-- Uma comissão pode possuir vários alunos.
-- =========================================================

CREATE TABLE comissao_aluno (
    id_comissao BIGINT UNSIGNED NOT NULL,
    id_aluno BIGINT UNSIGNED NOT NULL,

    funcao VARCHAR(100) NULL,

    data_entrada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_saida DATETIME NULL,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id_comissao, id_aluno),

    KEY idx_comissao_aluno_aluno (id_aluno),

    CONSTRAINT fk_comissao_aluno_comissao
        FOREIGN KEY (id_comissao)
        REFERENCES comissao (id_comissao)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_comissao_aluno_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 9. ATIVIDADE
-- =========================================================
-- Atividade pertence a um evento.
-- =========================================================

CREATE TABLE atividade (
    id_atividade BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_evento BIGINT UNSIGNED NOT NULL,

    nome VARCHAR(200) NOT NULL,
    descricao TEXT NULL,

    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NULL,

    local VARCHAR(200) NULL,

    capacidade INT UNSIGNED NULL,

    status ENUM(
        'PLANEJADA',
        'ABERTA',
        'EM_ANDAMENTO',
        'CONCLUIDA',
        'CANCELADA'
    ) NOT NULL DEFAULT 'PLANEJADA',

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_atividade),

    KEY idx_atividade_evento (id_evento),
    KEY idx_atividade_data (data_inicio),
    KEY idx_atividade_status (status),

    CONSTRAINT fk_atividade_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 10. COMISSAO_ATIVIDADE
-- =========================================================
-- Define qual comissão é responsável por uma atividade.
--
-- Não relacionamos aluno diretamente à atividade.
-- =========================================================

CREATE TABLE comissao_atividade (
    id_comissao BIGINT UNSIGNED NOT NULL,
    id_atividade BIGINT UNSIGNED NOT NULL,

    status ENUM(
        'PENDENTE',
        'EM_ANDAMENTO',
        'CONCLUIDA',
        'CANCELADA'
    ) NOT NULL DEFAULT 'PENDENTE',

    observacao TEXT NULL,

    data_atribuicao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    data_conclusao DATETIME NULL,

    PRIMARY KEY (id_comissao, id_atividade),

    KEY idx_comissao_atividade_atividade (id_atividade),

    CONSTRAINT fk_comissao_atividade_comissao
        FOREIGN KEY (id_comissao)
        REFERENCES comissao (id_comissao)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_comissao_atividade_atividade
        FOREIGN KEY (id_atividade)
        REFERENCES atividade (id_atividade)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 11. RESPONSABILIDADE
-- =========================================================
-- IMPORTANTE:
-- Nenhuma responsabilidade padrão é cadastrada.
--
-- O ADMINISTRADOR cria as responsabilidades dentro
-- de cada evento.
-- =========================================================

CREATE TABLE responsabilidade (
    id_responsabilidade BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_evento BIGINT UNSIGNED NOT NULL,

    nome VARCHAR(150) NOT NULL,
    descricao TEXT NULL,

    status ENUM(
        'PENDENTE',
        'EM_ANDAMENTO',
        'CONCLUIDA',
        'CANCELADA'
    ) NOT NULL DEFAULT 'PENDENTE',

    observacao TEXT NULL,

    data_atribuicao DATETIME NULL,
    data_conclusao DATETIME NULL,

    PRIMARY KEY (id_responsabilidade),

    KEY idx_responsabilidade_evento (id_evento),
    KEY idx_responsabilidade_status (status),

    CONSTRAINT fk_responsabilidade_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 12. RESPONSABILIDADE_COMISSAO
-- =========================================================
-- Define qual comissão ficará responsável por uma
-- responsabilidade.
-- =========================================================

CREATE TABLE responsabilidade_comissao (
    id_responsabilidade BIGINT UNSIGNED NOT NULL,
    id_comissao BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (
        id_responsabilidade,
        id_comissao
    ),

    KEY idx_responsabilidade_comissao_comissao (
        id_comissao
    ),

    CONSTRAINT fk_responsabilidade_comissao_responsabilidade
        FOREIGN KEY (id_responsabilidade)
        REFERENCES responsabilidade (id_responsabilidade)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_responsabilidade_comissao_comissao
        FOREIGN KEY (id_comissao)
        REFERENCES comissao (id_comissao)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 13. AGENTE_EXTERNO
-- =========================================================
-- Pessoas de fora da escola que podem participar dos eventos.
-- Exemplo: palestrante, convidado, especialista etc.
-- =========================================================

CREATE TABLE agente_externo (
    id_agente BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NULL,
    telefone VARCHAR(20) NULL,

    empresa VARCHAR(150) NULL,
    cargo VARCHAR(100) NULL,
    especialidade VARCHAR(150) NULL,

    observacao TEXT NULL,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_agente),

    KEY idx_agente_nome (nome),
    KEY idx_agente_empresa (empresa)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 14. EVENTO_AGENTE_EXTERNO
-- =========================================================
-- Relaciona agentes externos aos eventos.
-- =========================================================

CREATE TABLE evento_agente_externo (
    id_evento BIGINT UNSIGNED NOT NULL,
    id_agente BIGINT UNSIGNED NOT NULL,

    tipo_participacao ENUM(
        'PALESTRANTE',
        'CONVIDADO',
        'ESPECIALISTA',
        'AVALIADOR',
        'OUTRO'
    ) NOT NULL DEFAULT 'CONVIDADO',

    tema VARCHAR(200) NULL,

    observacao TEXT NULL,

    PRIMARY KEY (
        id_evento,
        id_agente
    ),

    KEY idx_evento_agente_agente (id_agente),

    CONSTRAINT fk_evento_agente_evento
        FOREIGN KEY (id_evento)
        REFERENCES evento (id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_evento_agente_agente
        FOREIGN KEY (id_agente)
        REFERENCES agente_externo (id_agente)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 15. AUDITORIA
-- =========================================================
-- Registra ações realizadas pelos usuários.
-- Exemplo:
-- ADMINISTRADOR cadastrou um aluno.
-- ADMINISTRADOR criou um evento.
-- ADMINISTRADOR alterou uma comissão.
-- =========================================================

CREATE TABLE auditoria (
    id_auditoria BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    id_usuario BIGINT UNSIGNED NOT NULL,

    acao ENUM(
        'LOGIN',
        'LOGOUT',
        'CADASTRO',
        'ALTERACAO',
        'EXCLUSAO'
    ) NOT NULL,

    tabela VARCHAR(100) NULL,

    id_registro BIGINT UNSIGNED NULL,

    descricao TEXT NULL,

    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_auditoria),

    KEY idx_auditoria_usuario (id_usuario),
    KEY idx_auditoria_acao (acao),
    KEY idx_auditoria_data (data_hora),

    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- DADOS INICIAIS
-- =========================================================


-- =========================================================
-- USUÁRIOS
-- =========================================================

INSERT INTO usuario (
    id_usuario,
    nome,
    email,
    senha,
    tipo_usuario,
    ativo,
    data_cadastro
) VALUES
(
    1,
    'Victor',
    'admin@etec.com.br',
    '$2a$10$AMmZpSVsOdmB7BrXFT2OReSxt8QhFmaUzcHVddsWfu4VooupXNrqG',
    'ADMINISTRADOR',
    TRUE,
    '2026-09-10 16:52:44'
),
(
    2,
    'João Silva',
    'joao@etec.com.br',
    '$2a$10$AMmZpSVsOdmB7BrXFT2OReSxt8QhFmaUzcHVddsWfu4VooupXNrqG',
    'GESTAO',
    TRUE,
    '2026-09-10 17:35:39'
);


-- =========================================================
-- EVENTOS
-- =========================================================

INSERT INTO evento (
    id_evento,
    id_usuario_criador,
    nome,
    descricao,
    data_inicio,
    data_fim,
    local,
    capacidade,
    status,
    data_cadastro
) VALUES
(
    1,
    1,
    'Semana de Tecnologia 2026',
    'Evento de tecnologia e desenvolvimento de sistemas da escola.',
    '2026-09-15 08:00:00',
    '2026-09-15 17:00:00',
    'Auditório da ETEC',
    100,
    'ABERTO',
    '2026-09-10 18:02:24'
),
(
    2,
    1,
    'Feira de Ciências 2026',
    'Feira de ciências e projetos desenvolvidos pelos alunos da escola.',
    '2026-10-12 09:00:00',
    '2026-10-12 16:00:00',
    'Pátio da ETEC',
    150,
    'ABERTO',
    '2026-09-10 18:35:44'
);


-- =========================================================
-- VIEWS
-- =========================================================
-- Views para facilitar o desenvolvimento do Dashboard,
-- Eventos, Inscrições e Relatórios.
-- =========================================================


-- =========================================================
-- EVENTOS
-- =========================================================

CREATE OR REPLACE VIEW vw_eventos AS
SELECT
    e.id_evento,
    e.nome AS evento,
    e.descricao,
    e.data_inicio,
    e.data_fim,
    e.local,
    e.capacidade,
    e.status,

    u.id_usuario AS id_criador,
    u.nome AS criado_por,
    u.email AS email_criador,

    e.data_cadastro

FROM evento e
INNER JOIN usuario u
    ON u.id_usuario = e.id_usuario_criador;


-- =========================================================
-- ALUNOS POR EVENTO
-- =========================================================

CREATE OR REPLACE VIEW vw_alunos_eventos AS
SELECT
    e.id_evento,
    e.nome AS evento,

    a.id_aluno,
    a.rm,
    a.nome AS aluno,
    a.turma,
    a.curso,
    a.email,
    a.telefone,

    i.data_inscricao,
    i.status AS status_inscricao,
    i.observacao

FROM inscricao_evento i

INNER JOIN aluno a
    ON a.id_aluno = i.id_aluno

INNER JOIN evento e
    ON e.id_evento = i.id_evento;


-- =========================================================
-- ATIVIDADES
-- =========================================================

CREATE OR REPLACE VIEW vw_atividades AS
SELECT
    e.id_evento,
    e.nome AS evento,

    a.id_atividade,
    a.nome AS atividade,
    a.descricao,

    a.data_inicio,
    a.data_fim,
    a.local,
    a.capacidade,
    a.status,
    a.data_cadastro

FROM atividade a

INNER JOIN evento e
    ON e.id_evento = a.id_evento;


-- =========================================================
-- QUANTIDADE DE ALUNOS POR EVENTO
-- =========================================================

CREATE OR REPLACE VIEW vw_quantidade_alunos_evento AS
SELECT
    e.id_evento,
    e.nome AS evento,
    e.capacidade,

    COUNT(
        CASE
            WHEN i.status <> 'CANCELADO'
            THEN i.id_inscricao
        END
    ) AS total_inscritos,

    CASE
        WHEN e.capacidade IS NOT NULL
        THEN
            e.capacidade -
            COUNT(
                CASE
                    WHEN i.status <> 'CANCELADO'
                    THEN i.id_inscricao
                END
            )
        ELSE NULL
    END AS vagas_disponiveis

FROM evento e

LEFT JOIN inscricao_evento i
    ON i.id_evento = e.id_evento

GROUP BY
    e.id_evento,
    e.nome,
    e.capacidade;


-- =========================================================
-- COMISSÕES
-- =========================================================

CREATE OR REPLACE VIEW vw_comissoes AS
SELECT
    c.id_comissao,
    c.id_evento,

    e.nome AS evento,

    c.nome AS comissao,
    c.descricao,
    c.ativo,
    c.data_cadastro,

    COUNT(
        DISTINCT ca.id_aluno
    ) AS total_alunos,

    COUNT(
        DISTINCT cativ.id_atividade
    ) AS total_atividades

FROM comissao c

INNER JOIN evento e
    ON e.id_evento = c.id_evento

LEFT JOIN comissao_aluno ca
    ON ca.id_comissao = c.id_comissao
    AND ca.ativo = TRUE

LEFT JOIN comissao_atividade cativ
    ON cativ.id_comissao = c.id_comissao

GROUP BY
    c.id_comissao,
    c.id_evento,
    e.nome,
    c.nome,
    c.descricao,
    c.ativo,
    c.data_cadastro;


-- =========================================================
-- RESPONSABILIDADES
-- =========================================================

CREATE OR REPLACE VIEW vw_responsabilidades AS
SELECT
    r.id_responsabilidade,
    r.id_evento,

    e.nome AS evento,

    r.nome AS responsabilidade,
    r.descricao,
    r.status,
    r.observacao,

    r.data_atribuicao,
    r.data_conclusao,

    COUNT(
        DISTINCT rc.id_comissao
    ) AS total_comissoes

FROM responsabilidade r

INNER JOIN evento e
    ON e.id_evento = r.id_evento

LEFT JOIN responsabilidade_comissao rc
    ON rc.id_responsabilidade = r.id_responsabilidade

GROUP BY
    r.id_responsabilidade,
    r.id_evento,
    e.nome,
    r.nome,
    r.descricao,
    r.status,
    r.observacao,
    r.data_atribuicao,
    r.data_conclusao;


-- =========================================================
-- PROFESSORES POR EVENTO
-- =========================================================

CREATE OR REPLACE VIEW vw_eventos_professores AS
SELECT
    e.id_evento,
    e.nome AS evento,

    p.id_professor,
    p.nome AS professor,
    p.email,
    p.telefone,
    p.area_atuacao,

    ep.principal

FROM evento_professor ep

INNER JOIN evento e
    ON e.id_evento = ep.id_evento

INNER JOIN professor p
    ON p.id_professor = ep.id_professor;


-- =========================================================
-- AGENTES EXTERNOS POR EVENTO
-- =========================================================

CREATE OR REPLACE VIEW vw_eventos_agentes_externos AS
SELECT
    e.id_evento,
    e.nome AS evento,

    a.id_agente,
    a.nome AS agente_externo,
    a.email,
    a.telefone,
    a.empresa,
    a.cargo,
    a.especialidade,

    ea.tipo_participacao,
    ea.tema,
    ea.observacao

FROM evento_agente_externo ea

INNER JOIN evento e
    ON e.id_evento = ea.id_evento

INNER JOIN agente_externo a
    ON a.id_agente = ea.id_agente;


-- =========================================================
-- RESUMO DOS EVENTOS
-- =========================================================

CREATE OR REPLACE VIEW vw_resumo_eventos AS
SELECT
    e.id_evento,
    e.nome AS evento,

    e.data_inicio,
    e.data_fim,
    e.local,
    e.capacidade,
    e.status,

    COUNT(
        DISTINCT CASE
            WHEN i.status <> 'CANCELADO'
            THEN i.id_aluno
        END
    ) AS total_inscritos,

    COUNT(
        DISTINCT atv.id_atividade
    ) AS total_atividades,

    COUNT(
        DISTINCT c.id_comissao
    ) AS total_comissoes,

    COUNT(
        DISTINCT ep.id_professor
    ) AS total_professores,

    COUNT(
        DISTINCT ea.id_agente
    ) AS total_agentes_externos,

    COUNT(
        DISTINCT CASE
            WHEN r.status = 'CONCLUIDA'
            THEN r.id_responsabilidade
        END
    ) AS responsabilidades_concluidas,

    COUNT(
        DISTINCT CASE
            WHEN r.status = 'PENDENTE'
            THEN r.id_responsabilidade
        END
    ) AS responsabilidades_pendentes

FROM evento e

LEFT JOIN inscricao_evento i
    ON i.id_evento = e.id_evento

LEFT JOIN atividade atv
    ON atv.id_evento = e.id_evento

LEFT JOIN comissao c
    ON c.id_evento = e.id_evento

LEFT JOIN evento_professor ep
    ON ep.id_evento = e.id_evento

LEFT JOIN evento_agente_externo ea
    ON ea.id_evento = e.id_evento

LEFT JOIN responsabilidade r
    ON r.id_evento = e.id_evento

GROUP BY
    e.id_evento,
    e.nome,
    e.data_inicio,
    e.data_fim,
    e.local,
    e.capacidade,
    e.status;


-- =========================================================
-- FIM
-- =========================================================