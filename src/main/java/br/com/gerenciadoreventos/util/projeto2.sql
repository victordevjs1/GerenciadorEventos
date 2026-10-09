-- GERENCIADOR DE EVENTOS ESCOLARES
-- BANCO DE DADOS COMPLETO

DROP DATABASE IF EXISTS gerenciador_eventos;

CREATE DATABASE gerenciador_eventos
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gerenciador_eventos;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. USUARIO

DROP TABLE IF EXISTS auditoria;
DROP TABLE IF EXISTS evento_agente_externo;
DROP TABLE IF EXISTS agente_externo;
DROP TABLE IF EXISTS responsabilidade_comissao;
DROP TABLE IF EXISTS responsabilidade;
DROP TABLE IF EXISTS comissao_atividade;
DROP TABLE IF EXISTS comissao_aluno;
DROP TABLE IF EXISTS comissao;
DROP TABLE IF EXISTS atividade;
DROP TABLE IF EXISTS presenca_evento;
DROP TABLE IF EXISTS inscricao_evento;
DROP TABLE IF EXISTS evento_professor;
DROP TABLE IF EXISTS professor;
DROP TABLE IF EXISTS evento_publico;
DROP TABLE IF EXISTS serie;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS evento;
DROP TABLE IF EXISTS aluno;
DROP TABLE IF EXISTS recuperacao_senha;
DROP TABLE IF EXISTS usuario;

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

-- 2. RECUPERAÇÃO DE SENHA

CREATE TABLE recuperacao_senha (
    id_recuperacao BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT UNSIGNED NOT NULL,
    codigo_hash VARCHAR(255) NOT NULL,
    data_expiracao DATETIME NOT NULL,
    tentativas TINYINT UNSIGNED NOT NULL DEFAULT 0,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,
    data_solicitacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_recuperacao),
    KEY idx_recuperacao_usuario (id_usuario),
    KEY idx_recuperacao_expiracao (data_expiracao),
    CONSTRAINT fk_recuperacao_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 3. CURSO

CREATE TABLE curso (
                       id_curso BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                       nome VARCHAR(150) NOT NULL,
                       sigla VARCHAR(30) NULL,

                       ativo BOOLEAN NOT NULL DEFAULT TRUE,

                       data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id_curso),

                       UNIQUE KEY uk_curso_nome (nome)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 4. SERIE
--
-- Cada curso possui suas próprias séries.
--
-- Exemplo:
--
-- Desenvolvimento de Sistemas
--   1º Ano
--   2º Ano
--   3º Ano
--
-- Nutrição e Dietética
--   1º Ano
--   2º Ano
--   3º Ano
--

CREATE TABLE serie (
                       id_serie BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                       id_curso BIGINT UNSIGNED NOT NULL,

                       nome VARCHAR(50) NOT NULL,
                       numero TINYINT UNSIGNED NOT NULL,

                       ativo BOOLEAN NOT NULL DEFAULT TRUE,

                       data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id_serie),

                       UNIQUE KEY uk_serie_curso_numero (
                           id_curso,
                           numero
                           ),

                       UNIQUE KEY uk_serie_curso_id (
                           id_curso,
                           id_serie
                           ),

                       KEY idx_serie_curso (
        id_curso
    ),

                       CONSTRAINT fk_serie_curso
                           FOREIGN KEY (id_curso)
                               REFERENCES curso (id_curso)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 5. ALUNO

CREATE TABLE aluno (
                       id_aluno BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                       rm VARCHAR(20) NOT NULL,

                       nome VARCHAR(150) NOT NULL,

                       data_nascimento DATE NULL,

                       id_curso BIGINT UNSIGNED NULL,

                       id_serie BIGINT UNSIGNED NULL,

                       ano_conclusao YEAR NULL,

                       email VARCHAR(150) NULL,

                       telefone VARCHAR(20) NULL,

                       ativo BOOLEAN NOT NULL DEFAULT TRUE,

                       data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id_aluno),

                       UNIQUE KEY uk_aluno_rm (
                           rm
                           ),

                       KEY idx_aluno_curso (
        id_curso
    ),

                       KEY idx_aluno_serie (
        id_serie
    ),

                       KEY idx_aluno_ano_conclusao (
        ano_conclusao
    ),

                       CONSTRAINT fk_aluno_curso
                           FOREIGN KEY (id_curso)
                               REFERENCES curso (id_curso)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE,

                       CONSTRAINT fk_aluno_serie
                           FOREIGN KEY (id_serie)
                               REFERENCES serie (id_serie)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE,

                       CONSTRAINT fk_aluno_curso_serie
                           FOREIGN KEY (id_curso, id_serie)
                               REFERENCES serie (id_curso, id_serie)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 6. EVENTO

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

                        KEY idx_evento_usuario (
        id_usuario_criador
    ),

                        KEY idx_evento_data (
        data_inicio
    ),

                        KEY idx_evento_status (
        status
    ),

                        CONSTRAINT fk_evento_usuario
                            FOREIGN KEY (id_usuario_criador)
                                REFERENCES usuario (id_usuario)
                                ON DELETE RESTRICT
                                ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 7. EVENTO_PUBLICO
--
-- Define quem pode participar de cada evento.
--
-- REGRAS:
--
-- 1. TODA A ESCOLA
--
-- publico_todos = TRUE
-- id_curso = NULL
-- id_serie = NULL
--
--
-- 2. CURSO INTEIRO
--
-- publico_todos = FALSE
-- id_curso = 1
-- id_serie = NULL
--
-- Exemplo:
-- Todo Desenvolvimento de Sistemas.
--
--
-- 3. SÉRIE ESPECÍFICA
--
-- publico_todos = FALSE
-- id_curso = 1
-- id_serie = 1
--
-- Exemplo:
-- Somente Desenvolvimento de Sistemas 1º Ano.
--
--
-- Um evento pode possuir vários registros.
--
-- Exemplo:
--
-- DS 1º ano
-- DS 2º ano
-- Nutrição inteira
--

CREATE TABLE evento_publico (
                                id_evento_publico BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                                id_evento BIGINT UNSIGNED NOT NULL,

                                id_curso BIGINT UNSIGNED NULL,

                                id_serie BIGINT UNSIGNED NULL,

                                publico_todos BOOLEAN NOT NULL DEFAULT FALSE,

                                PRIMARY KEY (
                                             id_evento_publico
                                    ),

                                KEY idx_evento_publico_evento (
        id_evento
    ),

                                KEY idx_evento_publico_curso (
        id_curso
    ),

                                KEY idx_evento_publico_serie (
        id_serie
    ),

                                KEY idx_evento_publico_todos (
        publico_todos
    ),

                                CONSTRAINT fk_evento_publico_evento
                                    FOREIGN KEY (id_evento)
                                        REFERENCES evento (id_evento)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE,

                                CONSTRAINT fk_evento_publico_curso
                                    FOREIGN KEY (id_curso)
                                        REFERENCES curso (id_curso)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE,

                                CONSTRAINT fk_evento_publico_serie
                                    FOREIGN KEY (id_serie)
                                        REFERENCES serie (id_serie)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE,

                                CONSTRAINT fk_evento_publico_curso_serie
                                    FOREIGN KEY (id_curso, id_serie)
                                        REFERENCES serie (id_curso, id_serie)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE,

                                CONSTRAINT chk_evento_publico_regra
                                    CHECK (
                                        (publico_todos = TRUE
                                            AND id_curso IS NULL
                                            AND id_serie IS NULL)
                                        OR
                                        (publico_todos = FALSE
                                            AND id_curso IS NOT NULL)
                                    )

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 8. PROFESSOR

CREATE TABLE professor (
                           id_professor BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                           nome VARCHAR(150) NOT NULL,

                           email VARCHAR(150) NULL,

                           telefone VARCHAR(20) NULL,

                           area_atuacao VARCHAR(100) NULL,

                           ativo BOOLEAN NOT NULL DEFAULT TRUE,

                           data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           PRIMARY KEY (id_professor),

                           KEY idx_professor_nome (
        nome
    ),

                           KEY idx_professor_area (
        area_atuacao
    )

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 9. EVENTO_PROFESSOR

CREATE TABLE evento_professor (
                                  id_evento BIGINT UNSIGNED NOT NULL,

                                  id_professor BIGINT UNSIGNED NOT NULL,

                                  principal BOOLEAN NOT NULL DEFAULT FALSE,

                                  PRIMARY KEY (
                                               id_evento,
                                               id_professor
                                      ),

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

-- 10. INSCRICAO_EVENTO

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

                                  PRIMARY KEY (
                                               id_inscricao
                                      ),

                                  UNIQUE KEY uk_inscricao_aluno_evento (
                                      id_aluno,
                                      id_evento
                                      ),

                                  KEY idx_inscricao_aluno (
        id_aluno
    ),

                                  KEY idx_inscricao_evento (
        id_evento
    ),

                                  KEY idx_inscricao_status (
        status
    ),

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

-- 11. PRESENCA_EVENTO

CREATE TABLE presenca_evento (
                                 id_presenca BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                                 id_inscricao BIGINT UNSIGNED NOT NULL,

                                 status ENUM(
        'PRESENTE',
        'AUSENTE'
    ) NOT NULL DEFAULT 'AUSENTE',

                                 data_presenca DATE NOT NULL,

                                 observacao TEXT NULL,

                                 PRIMARY KEY (
                                              id_presenca
                                     ),

                                 UNIQUE KEY uk_presenca_inscricao_data (
                                     id_inscricao,
                                     data_presenca
                                     ),

                                 KEY idx_presenca_inscricao (
        id_inscricao
    ),

                                 KEY idx_presenca_status (
        status
    ),

                                 KEY idx_presenca_data (
        data_presenca
    ),

                                 CONSTRAINT fk_presenca_inscricao
                                     FOREIGN KEY (id_inscricao)
                                         REFERENCES inscricao_evento (id_inscricao)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 12. COMISSAO

CREATE TABLE comissao (
                          id_comissao BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                          id_evento BIGINT UNSIGNED NOT NULL,

                          nome VARCHAR(150) NOT NULL,

                          descricao TEXT NULL,

                          ativo BOOLEAN NOT NULL DEFAULT TRUE,

                          data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          PRIMARY KEY (
                                       id_comissao
                              ),

                          KEY idx_comissao_evento (
        id_evento
    ),

                          CONSTRAINT fk_comissao_evento
                              FOREIGN KEY (id_evento)
                                  REFERENCES evento (id_evento)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 13. COMISSAO_ALUNO

CREATE TABLE comissao_aluno (
                                id_comissao BIGINT UNSIGNED NOT NULL,

                                id_aluno BIGINT UNSIGNED NOT NULL,

                                funcao VARCHAR(100) NULL,

                                data_entrada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                data_saida DATETIME NULL,

                                ativo BOOLEAN NOT NULL DEFAULT TRUE,

                                PRIMARY KEY (
                                             id_comissao,
                                             id_aluno
                                    ),

                                KEY idx_comissao_aluno_aluno (
        id_aluno
    ),

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

-- 14. ATIVIDADE

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

                           PRIMARY KEY (
                                        id_atividade
                               ),

                           KEY idx_atividade_evento (
        id_evento
    ),

                           KEY idx_atividade_data (
        data_inicio
    ),

                           KEY idx_atividade_status (
        status
    ),

                           CONSTRAINT fk_atividade_evento
                               FOREIGN KEY (id_evento)
                                   REFERENCES evento (id_evento)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 15. COMISSAO_ATIVIDADE

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

                                    PRIMARY KEY (
                                                 id_comissao,
                                                 id_atividade
                                        ),

                                    KEY idx_comissao_atividade_atividade (
        id_atividade
    ),

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

-- 16. RESPONSABILIDADE

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

                                  PRIMARY KEY (
                                               id_responsabilidade
                                      ),

                                  KEY idx_responsabilidade_evento (
        id_evento
    ),

                                  KEY idx_responsabilidade_status (
        status
    ),

                                  CONSTRAINT fk_responsabilidade_evento
                                      FOREIGN KEY (id_evento)
                                          REFERENCES evento (id_evento)
                                          ON DELETE CASCADE
                                          ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 17. RESPONSABILIDADE_COMISSAO

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

-- 18. AGENTE_EXTERNO

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

                                PRIMARY KEY (
                                             id_agente
                                    ),

                                KEY idx_agente_nome (
        nome
    ),

                                KEY idx_agente_empresa (
        empresa
    )

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- 19. EVENTO_AGENTE_EXTERNO

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

                                       KEY idx_evento_agente_agente (
        id_agente
    ),

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

-- 20. AUDITORIA

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

                           PRIMARY KEY (
                                        id_auditoria
                               ),

                           KEY idx_auditoria_usuario (
        id_usuario
    ),

                           KEY idx_auditoria_acao (
        acao
    ),

                           KEY idx_auditoria_data (
        data_hora
    ),

                           CONSTRAINT fk_auditoria_usuario
                               FOREIGN KEY (id_usuario)
                                   REFERENCES usuario (id_usuario)
                                   ON DELETE RESTRICT
                                   ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

-- DADOS INICIAIS

-- USUÁRIOS

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

-- CURSOS

INSERT INTO curso (
    id_curso,
    nome,
    sigla
) VALUES
      (
          1,
          'Desenvolvimento de Sistemas',
          'DS'
      ),
      (
          2,
          'Nutrição e Dietética',
          'ND'
      ),
      (
          3,
          'Administração',
          'ADM'
      ),
      (
          4,
          'Marketing',
          'MKT'
      );

-- SÉRIES

INSERT INTO serie (
    id_serie,
    id_curso,
    nome,
    numero
) VALUES

-- Desenvolvimento de Sistemas
(
    1,
    1,
    '1º Ano',
    1
),
(
    2,
    1,
    '2º Ano',
    2
),
(
    3,
    1,
    '3º Ano',
    3
),

-- Nutrição e Dietética
(
    4,
    2,
    '1º Ano',
    1
),
(
    5,
    2,
    '2º Ano',
    2
),
(
    6,
    2,
    '3º Ano',
    3
),

-- Administração
(
    7,
    3,
    '1º Ano',
    1
),
(
    8,
    3,
    '2º Ano',
    2
),
(
    9,
    3,
    '3º Ano',
    3
),

-- Marketing
(
    10,
    4,
    '1º Ano',
    1
),
(
    11,
    4,
    '2º Ano',
    2
),
(
    12,
    4,
    '3º Ano',
    3
);

-- EVENTOS

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

-- PÚBLICO DOS EVENTOS

-- EVENTO 1
-- Semana de Tecnologia
--
-- Desenvolvimento de Sistemas
-- TODAS AS SÉRIES
--
-- id_serie = NULL

INSERT INTO evento_publico (
    id_evento,
    id_curso,
    id_serie,
    publico_todos
) VALUES (
             1,
             1,
             NULL,
             FALSE
         );

-- EVENTO 2
-- Feira de Ciências
--
-- TODA A ESCOLA

INSERT INTO evento_publico (
    id_evento,
    id_curso,
    id_serie,
    publico_todos
) VALUES (
             2,
             NULL,
             NULL,
             TRUE
         );

-- VIEWS

-- VIEW EVENTOS

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

-- VIEW PÚBLICO DOS EVENTOS

CREATE OR REPLACE VIEW vw_eventos_publico AS
SELECT
    ep.id_evento_publico,

    ep.id_evento,

    e.nome AS evento,

    ep.publico_todos,

    ep.id_curso,

    c.nome AS curso,

    ep.id_serie,

    s.nome AS serie

FROM evento_publico ep

         INNER JOIN evento e
                    ON e.id_evento = ep.id_evento

         LEFT JOIN curso c
                   ON c.id_curso = ep.id_curso

         LEFT JOIN serie s
                   ON s.id_serie = ep.id_serie;

-- VIEW ALUNOS POR EVENTO

CREATE OR REPLACE VIEW vw_alunos_eventos AS
SELECT
    e.id_evento,

    e.nome AS evento,

    a.id_aluno,

    a.rm,

    a.nome AS aluno,

    c.id_curso,

    c.nome AS curso,

    s.id_serie,

    s.nome AS serie,

    a.email,

    a.telefone,

    i.data_inscricao,

    i.status AS status_inscricao,

    i.observacao

FROM inscricao_evento i

         INNER JOIN aluno a
                    ON a.id_aluno = i.id_aluno

         INNER JOIN evento e
                    ON e.id_evento = i.id_evento

         LEFT JOIN curso c
                   ON c.id_curso = a.id_curso

         LEFT JOIN serie s
                   ON s.id_serie = a.id_serie;

-- VIEW ATIVIDADES

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

-- VIEW QUANTIDADE DE ALUNOS POR EVENTO

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

-- VIEW COMISSÕES

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

-- VIEW RESPONSABILIDADES

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
                   ON rc.id_responsabilidade =
                      r.id_responsabilidade

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

-- VIEW PROFESSORES POR EVENTO

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

-- VIEW AGENTES EXTERNOS POR EVENTO

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

-- VIEW CURSOS E SÉRIES

CREATE OR REPLACE VIEW vw_cursos_series AS
SELECT
    c.id_curso,

    c.nome AS curso,

    c.sigla,

    s.id_serie,

    s.nome AS serie,

    s.numero

FROM curso c

         LEFT JOIN serie s
                   ON s.id_curso = c.id_curso

WHERE c.ativo = TRUE

ORDER BY
    c.nome,
    s.numero;

-- VIEW RESUMO DOS EVENTOS

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

-- FIM DO BANCO
