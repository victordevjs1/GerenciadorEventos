-- MIGRAÇÃO: ÚLTIMA ALTERAÇÃO + AUDITORIA COMPLETA
-- Execute uma vez no banco gerenciador_eventos.
-- A tabela auditoria é append-only e não audita a si própria para evitar recursão.

USE gerenciador_eventos;

ALTER TABLE auditoria
    MODIFY COLUMN id_usuario BIGINT UNSIGNED NULL;

ALTER TABLE auditoria
    DROP FOREIGN KEY fk_auditoria_usuario;

ALTER TABLE auditoria
    ADD CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE SET NULL
        ON UPDATE CASCADE;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_etask_add_coluna$$
CREATE PROCEDURE sp_etask_add_coluna(IN p_tabela VARCHAR(64))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_tabela AND COLUMN_NAME = 'alterado_em'
    ) THEN
        SET @sql = CONCAT('ALTER TABLE `', p_tabela, '` ADD COLUMN alterado_em DATETIME NULL');
        PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_tabela AND COLUMN_NAME = 'alterado_por'
    ) THEN
        SET @sql = CONCAT('ALTER TABLE `', p_tabela, '` ADD COLUMN alterado_por BIGINT UNSIGNED NULL');
        PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_tabela
          AND INDEX_NAME = CONCAT('idx_', p_tabela, '_alterado_por')
    ) THEN
        SET @sql = CONCAT('CREATE INDEX `idx_', p_tabela, '_alterado_por` ON `', p_tabela, '` (alterado_por)');
        PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = p_tabela
          AND CONSTRAINT_NAME = CONCAT('fk_', p_tabela, '_alterado_por')
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ) THEN
        SET @sql = CONCAT(
            'ALTER TABLE `', p_tabela, '` ADD CONSTRAINT `fk_', p_tabela, '_alterado_por` ',
            'FOREIGN KEY (alterado_por) REFERENCES usuario(id_usuario) ON DELETE SET NULL ON UPDATE CASCADE'
        );
        PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL sp_etask_add_coluna('usuario');
CALL sp_etask_add_coluna('recuperacao_senha');
CALL sp_etask_add_coluna('curso');
CALL sp_etask_add_coluna('serie');
CALL sp_etask_add_coluna('aluno');
CALL sp_etask_add_coluna('evento');
CALL sp_etask_add_coluna('evento_publico');
CALL sp_etask_add_coluna('professor');
CALL sp_etask_add_coluna('evento_professor');
CALL sp_etask_add_coluna('inscricao_evento');
CALL sp_etask_add_coluna('presenca_evento');
CALL sp_etask_add_coluna('comissao');
CALL sp_etask_add_coluna('comissao_aluno');
CALL sp_etask_add_coluna('atividade');
CALL sp_etask_add_coluna('comissao_atividade');
CALL sp_etask_add_coluna('responsabilidade');
CALL sp_etask_add_coluna('responsabilidade_comissao');
CALL sp_etask_add_coluna('agente_externo');
CALL sp_etask_add_coluna('evento_agente_externo');
CALL sp_etask_add_coluna('auditoria');

DROP PROCEDURE sp_etask_add_coluna;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_etask_add_coluna_auditoria$$
CREATE PROCEDURE sp_etask_add_coluna_auditoria(IN p_coluna VARCHAR(64), IN p_definicao TEXT)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'auditoria' AND COLUMN_NAME = p_coluna
    ) THEN
        SET @sql = CONCAT('ALTER TABLE auditoria ADD COLUMN `', p_coluna, '` ', p_definicao);
        PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL sp_etask_add_coluna_auditoria('registro_chave', 'VARCHAR(255) NULL AFTER id_registro');
CALL sp_etask_add_coluna_auditoria('dados_anteriores', 'LONGTEXT NULL AFTER descricao');
CALL sp_etask_add_coluna_auditoria('dados_novos', 'LONGTEXT NULL AFTER dados_anteriores');
DROP PROCEDURE sp_etask_add_coluna_auditoria;

DELIMITER $$

DROP TRIGGER IF EXISTS `trg_auditoria_bi`$$
CREATE TRIGGER `trg_auditoria_bi`
BEFORE INSERT ON auditoria
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = NEW.id_usuario;
END$$

DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS `trg_usuario_bi`$$
DROP TRIGGER IF EXISTS `trg_usuario_bu`$$
DROP TRIGGER IF EXISTS `trg_usuario_ai`$$
DROP TRIGGER IF EXISTS `trg_usuario_au`$$
DROP TRIGGER IF EXISTS `trg_usuario_ad`$$
CREATE TRIGGER `trg_usuario_bi`
BEFORE INSERT ON `usuario`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_usuario_bu`
BEFORE UPDATE ON `usuario`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_usuario_ai`
AFTER INSERT ON `usuario`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'usuario', NEW.`id_usuario`, CONCAT('id_usuario=', NEW.`id_usuario`), 'Registro criado em usuario.', NULL, JSON_OBJECT('id_usuario', NEW.`id_usuario`, 'nome', NEW.`nome`, 'email', NEW.`email`, 'tipo_usuario', NEW.`tipo_usuario`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_usuario_au`
AFTER UPDATE ON `usuario`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'usuario', NEW.`id_usuario`, CONCAT('id_usuario=', NEW.`id_usuario`), 'Registro alterado em usuario.', JSON_OBJECT('id_usuario', OLD.`id_usuario`, 'nome', OLD.`nome`, 'email', OLD.`email`, 'tipo_usuario', OLD.`tipo_usuario`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_usuario', NEW.`id_usuario`, 'nome', NEW.`nome`, 'email', NEW.`email`, 'tipo_usuario', NEW.`tipo_usuario`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_usuario_ad`
AFTER DELETE ON `usuario`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (CASE WHEN @etask_usuario_id = OLD.id_usuario THEN NULL ELSE @etask_usuario_id END, 'EXCLUSAO', 'usuario', OLD.`id_usuario`, CONCAT('id_usuario=', OLD.`id_usuario`), 'Registro excluído de usuario.', JSON_OBJECT('id_usuario', OLD.`id_usuario`, 'nome', OLD.`nome`, 'email', OLD.`email`, 'tipo_usuario', OLD.`tipo_usuario`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_recuperacao_senha_bi`$$
DROP TRIGGER IF EXISTS `trg_recuperacao_senha_bu`$$
DROP TRIGGER IF EXISTS `trg_recuperacao_senha_ai`$$
DROP TRIGGER IF EXISTS `trg_recuperacao_senha_au`$$
DROP TRIGGER IF EXISTS `trg_recuperacao_senha_ad`$$
CREATE TRIGGER `trg_recuperacao_senha_bi`
BEFORE INSERT ON `recuperacao_senha`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_recuperacao_senha_bu`
BEFORE UPDATE ON `recuperacao_senha`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_recuperacao_senha_ai`
AFTER INSERT ON `recuperacao_senha`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'recuperacao_senha', NEW.`id_recuperacao`, CONCAT('id_recuperacao=', NEW.`id_recuperacao`), 'Registro criado em recuperacao_senha.', NULL, JSON_OBJECT('id_recuperacao', NEW.`id_recuperacao`, 'id_usuario', NEW.`id_usuario`, 'data_expiracao', NEW.`data_expiracao`, 'tentativas', NEW.`tentativas`, 'utilizado', NEW.`utilizado`, 'data_solicitacao', NEW.`data_solicitacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_recuperacao_senha_au`
AFTER UPDATE ON `recuperacao_senha`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'recuperacao_senha', NEW.`id_recuperacao`, CONCAT('id_recuperacao=', NEW.`id_recuperacao`), 'Registro alterado em recuperacao_senha.', JSON_OBJECT('id_recuperacao', OLD.`id_recuperacao`, 'id_usuario', OLD.`id_usuario`, 'data_expiracao', OLD.`data_expiracao`, 'tentativas', OLD.`tentativas`, 'utilizado', OLD.`utilizado`, 'data_solicitacao', OLD.`data_solicitacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_recuperacao', NEW.`id_recuperacao`, 'id_usuario', NEW.`id_usuario`, 'data_expiracao', NEW.`data_expiracao`, 'tentativas', NEW.`tentativas`, 'utilizado', NEW.`utilizado`, 'data_solicitacao', NEW.`data_solicitacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_recuperacao_senha_ad`
AFTER DELETE ON `recuperacao_senha`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'recuperacao_senha', OLD.`id_recuperacao`, CONCAT('id_recuperacao=', OLD.`id_recuperacao`), 'Registro excluído de recuperacao_senha.', JSON_OBJECT('id_recuperacao', OLD.`id_recuperacao`, 'id_usuario', OLD.`id_usuario`, 'data_expiracao', OLD.`data_expiracao`, 'tentativas', OLD.`tentativas`, 'utilizado', OLD.`utilizado`, 'data_solicitacao', OLD.`data_solicitacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_curso_bi`$$
DROP TRIGGER IF EXISTS `trg_curso_bu`$$
DROP TRIGGER IF EXISTS `trg_curso_ai`$$
DROP TRIGGER IF EXISTS `trg_curso_au`$$
DROP TRIGGER IF EXISTS `trg_curso_ad`$$
CREATE TRIGGER `trg_curso_bi`
BEFORE INSERT ON `curso`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_curso_bu`
BEFORE UPDATE ON `curso`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_curso_ai`
AFTER INSERT ON `curso`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'curso', NEW.`id_curso`, CONCAT('id_curso=', NEW.`id_curso`), 'Registro criado em curso.', NULL, JSON_OBJECT('id_curso', NEW.`id_curso`, 'nome', NEW.`nome`, 'sigla', NEW.`sigla`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_curso_au`
AFTER UPDATE ON `curso`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'curso', NEW.`id_curso`, CONCAT('id_curso=', NEW.`id_curso`), 'Registro alterado em curso.', JSON_OBJECT('id_curso', OLD.`id_curso`, 'nome', OLD.`nome`, 'sigla', OLD.`sigla`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_curso', NEW.`id_curso`, 'nome', NEW.`nome`, 'sigla', NEW.`sigla`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_curso_ad`
AFTER DELETE ON `curso`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'curso', OLD.`id_curso`, CONCAT('id_curso=', OLD.`id_curso`), 'Registro excluído de curso.', JSON_OBJECT('id_curso', OLD.`id_curso`, 'nome', OLD.`nome`, 'sigla', OLD.`sigla`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_serie_bi`$$
DROP TRIGGER IF EXISTS `trg_serie_bu`$$
DROP TRIGGER IF EXISTS `trg_serie_ai`$$
DROP TRIGGER IF EXISTS `trg_serie_au`$$
DROP TRIGGER IF EXISTS `trg_serie_ad`$$
CREATE TRIGGER `trg_serie_bi`
BEFORE INSERT ON `serie`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_serie_bu`
BEFORE UPDATE ON `serie`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_serie_ai`
AFTER INSERT ON `serie`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'serie', NEW.`id_serie`, CONCAT('id_serie=', NEW.`id_serie`), 'Registro criado em serie.', NULL, JSON_OBJECT('id_serie', NEW.`id_serie`, 'id_curso', NEW.`id_curso`, 'nome', NEW.`nome`, 'numero', NEW.`numero`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_serie_au`
AFTER UPDATE ON `serie`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'serie', NEW.`id_serie`, CONCAT('id_serie=', NEW.`id_serie`), 'Registro alterado em serie.', JSON_OBJECT('id_serie', OLD.`id_serie`, 'id_curso', OLD.`id_curso`, 'nome', OLD.`nome`, 'numero', OLD.`numero`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_serie', NEW.`id_serie`, 'id_curso', NEW.`id_curso`, 'nome', NEW.`nome`, 'numero', NEW.`numero`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_serie_ad`
AFTER DELETE ON `serie`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'serie', OLD.`id_serie`, CONCAT('id_serie=', OLD.`id_serie`), 'Registro excluído de serie.', JSON_OBJECT('id_serie', OLD.`id_serie`, 'id_curso', OLD.`id_curso`, 'nome', OLD.`nome`, 'numero', OLD.`numero`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_aluno_bi`$$
DROP TRIGGER IF EXISTS `trg_aluno_bu`$$
DROP TRIGGER IF EXISTS `trg_aluno_ai`$$
DROP TRIGGER IF EXISTS `trg_aluno_au`$$
DROP TRIGGER IF EXISTS `trg_aluno_ad`$$
CREATE TRIGGER `trg_aluno_bi`
BEFORE INSERT ON `aluno`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_aluno_bu`
BEFORE UPDATE ON `aluno`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_aluno_ai`
AFTER INSERT ON `aluno`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'aluno', NEW.`id_aluno`, CONCAT('id_aluno=', NEW.`id_aluno`), 'Registro criado em aluno.', NULL, JSON_OBJECT('id_aluno', NEW.`id_aluno`, 'rm', NEW.`rm`, 'nome', NEW.`nome`, 'data_nascimento', NEW.`data_nascimento`, 'id_curso', NEW.`id_curso`, 'id_serie', NEW.`id_serie`, 'ano_conclusao', NEW.`ano_conclusao`, 'email', NEW.`email`, 'telefone', NEW.`telefone`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_aluno_au`
AFTER UPDATE ON `aluno`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'aluno', NEW.`id_aluno`, CONCAT('id_aluno=', NEW.`id_aluno`), 'Registro alterado em aluno.', JSON_OBJECT('id_aluno', OLD.`id_aluno`, 'rm', OLD.`rm`, 'nome', OLD.`nome`, 'data_nascimento', OLD.`data_nascimento`, 'id_curso', OLD.`id_curso`, 'id_serie', OLD.`id_serie`, 'ano_conclusao', OLD.`ano_conclusao`, 'email', OLD.`email`, 'telefone', OLD.`telefone`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_aluno', NEW.`id_aluno`, 'rm', NEW.`rm`, 'nome', NEW.`nome`, 'data_nascimento', NEW.`data_nascimento`, 'id_curso', NEW.`id_curso`, 'id_serie', NEW.`id_serie`, 'ano_conclusao', NEW.`ano_conclusao`, 'email', NEW.`email`, 'telefone', NEW.`telefone`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_aluno_ad`
AFTER DELETE ON `aluno`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'aluno', OLD.`id_aluno`, CONCAT('id_aluno=', OLD.`id_aluno`), 'Registro excluído de aluno.', JSON_OBJECT('id_aluno', OLD.`id_aluno`, 'rm', OLD.`rm`, 'nome', OLD.`nome`, 'data_nascimento', OLD.`data_nascimento`, 'id_curso', OLD.`id_curso`, 'id_serie', OLD.`id_serie`, 'ano_conclusao', OLD.`ano_conclusao`, 'email', OLD.`email`, 'telefone', OLD.`telefone`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_evento_bi`$$
DROP TRIGGER IF EXISTS `trg_evento_bu`$$
DROP TRIGGER IF EXISTS `trg_evento_ai`$$
DROP TRIGGER IF EXISTS `trg_evento_au`$$
DROP TRIGGER IF EXISTS `trg_evento_ad`$$
CREATE TRIGGER `trg_evento_bi`
BEFORE INSERT ON `evento`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_bu`
BEFORE UPDATE ON `evento`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_ai`
AFTER INSERT ON `evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'evento', NEW.`id_evento`, CONCAT('id_evento=', NEW.`id_evento`), 'Registro criado em evento.', NULL, JSON_OBJECT('id_evento', NEW.`id_evento`, 'id_usuario_criador', NEW.`id_usuario_criador`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'data_inicio', NEW.`data_inicio`, 'data_fim', NEW.`data_fim`, 'local', NEW.`local`, 'capacidade', NEW.`capacidade`, 'status', NEW.`status`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_au`
AFTER UPDATE ON `evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'evento', NEW.`id_evento`, CONCAT('id_evento=', NEW.`id_evento`), 'Registro alterado em evento.', JSON_OBJECT('id_evento', OLD.`id_evento`, 'id_usuario_criador', OLD.`id_usuario_criador`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'data_inicio', OLD.`data_inicio`, 'data_fim', OLD.`data_fim`, 'local', OLD.`local`, 'capacidade', OLD.`capacidade`, 'status', OLD.`status`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_evento', NEW.`id_evento`, 'id_usuario_criador', NEW.`id_usuario_criador`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'data_inicio', NEW.`data_inicio`, 'data_fim', NEW.`data_fim`, 'local', NEW.`local`, 'capacidade', NEW.`capacidade`, 'status', NEW.`status`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_ad`
AFTER DELETE ON `evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'evento', OLD.`id_evento`, CONCAT('id_evento=', OLD.`id_evento`), 'Registro excluído de evento.', JSON_OBJECT('id_evento', OLD.`id_evento`, 'id_usuario_criador', OLD.`id_usuario_criador`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'data_inicio', OLD.`data_inicio`, 'data_fim', OLD.`data_fim`, 'local', OLD.`local`, 'capacidade', OLD.`capacidade`, 'status', OLD.`status`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_evento_publico_bi`$$
DROP TRIGGER IF EXISTS `trg_evento_publico_bu`$$
DROP TRIGGER IF EXISTS `trg_evento_publico_ai`$$
DROP TRIGGER IF EXISTS `trg_evento_publico_au`$$
DROP TRIGGER IF EXISTS `trg_evento_publico_ad`$$
CREATE TRIGGER `trg_evento_publico_bi`
BEFORE INSERT ON `evento_publico`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_publico_bu`
BEFORE UPDATE ON `evento_publico`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_publico_ai`
AFTER INSERT ON `evento_publico`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'evento_publico', NEW.`id_evento_publico`, CONCAT('id_evento_publico=', NEW.`id_evento_publico`), 'Registro criado em evento_publico.', NULL, JSON_OBJECT('id_evento_publico', NEW.`id_evento_publico`, 'id_evento', NEW.`id_evento`, 'id_curso', NEW.`id_curso`, 'id_serie', NEW.`id_serie`, 'publico_todos', NEW.`publico_todos`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_publico_au`
AFTER UPDATE ON `evento_publico`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'evento_publico', NEW.`id_evento_publico`, CONCAT('id_evento_publico=', NEW.`id_evento_publico`), 'Registro alterado em evento_publico.', JSON_OBJECT('id_evento_publico', OLD.`id_evento_publico`, 'id_evento', OLD.`id_evento`, 'id_curso', OLD.`id_curso`, 'id_serie', OLD.`id_serie`, 'publico_todos', OLD.`publico_todos`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_evento_publico', NEW.`id_evento_publico`, 'id_evento', NEW.`id_evento`, 'id_curso', NEW.`id_curso`, 'id_serie', NEW.`id_serie`, 'publico_todos', NEW.`publico_todos`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_publico_ad`
AFTER DELETE ON `evento_publico`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'evento_publico', OLD.`id_evento_publico`, CONCAT('id_evento_publico=', OLD.`id_evento_publico`), 'Registro excluído de evento_publico.', JSON_OBJECT('id_evento_publico', OLD.`id_evento_publico`, 'id_evento', OLD.`id_evento`, 'id_curso', OLD.`id_curso`, 'id_serie', OLD.`id_serie`, 'publico_todos', OLD.`publico_todos`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_professor_bi`$$
DROP TRIGGER IF EXISTS `trg_professor_bu`$$
DROP TRIGGER IF EXISTS `trg_professor_ai`$$
DROP TRIGGER IF EXISTS `trg_professor_au`$$
DROP TRIGGER IF EXISTS `trg_professor_ad`$$
CREATE TRIGGER `trg_professor_bi`
BEFORE INSERT ON `professor`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_professor_bu`
BEFORE UPDATE ON `professor`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_professor_ai`
AFTER INSERT ON `professor`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'professor', NEW.`id_professor`, CONCAT('id_professor=', NEW.`id_professor`), 'Registro criado em professor.', NULL, JSON_OBJECT('id_professor', NEW.`id_professor`, 'nome', NEW.`nome`, 'email', NEW.`email`, 'telefone', NEW.`telefone`, 'area_atuacao', NEW.`area_atuacao`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_professor_au`
AFTER UPDATE ON `professor`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'professor', NEW.`id_professor`, CONCAT('id_professor=', NEW.`id_professor`), 'Registro alterado em professor.', JSON_OBJECT('id_professor', OLD.`id_professor`, 'nome', OLD.`nome`, 'email', OLD.`email`, 'telefone', OLD.`telefone`, 'area_atuacao', OLD.`area_atuacao`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_professor', NEW.`id_professor`, 'nome', NEW.`nome`, 'email', NEW.`email`, 'telefone', NEW.`telefone`, 'area_atuacao', NEW.`area_atuacao`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_professor_ad`
AFTER DELETE ON `professor`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'professor', OLD.`id_professor`, CONCAT('id_professor=', OLD.`id_professor`), 'Registro excluído de professor.', JSON_OBJECT('id_professor', OLD.`id_professor`, 'nome', OLD.`nome`, 'email', OLD.`email`, 'telefone', OLD.`telefone`, 'area_atuacao', OLD.`area_atuacao`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_evento_professor_bi`$$
DROP TRIGGER IF EXISTS `trg_evento_professor_bu`$$
DROP TRIGGER IF EXISTS `trg_evento_professor_ai`$$
DROP TRIGGER IF EXISTS `trg_evento_professor_au`$$
DROP TRIGGER IF EXISTS `trg_evento_professor_ad`$$
CREATE TRIGGER `trg_evento_professor_bi`
BEFORE INSERT ON `evento_professor`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_professor_bu`
BEFORE UPDATE ON `evento_professor`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_professor_ai`
AFTER INSERT ON `evento_professor`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'evento_professor', NULL, CONCAT('id_evento=', NEW.`id_evento`, ', ', 'id_professor=', NEW.`id_professor`), 'Registro criado em evento_professor.', NULL, JSON_OBJECT('id_evento', NEW.`id_evento`, 'id_professor', NEW.`id_professor`, 'principal', NEW.`principal`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_professor_au`
AFTER UPDATE ON `evento_professor`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'evento_professor', NULL, CONCAT('id_evento=', NEW.`id_evento`, ', ', 'id_professor=', NEW.`id_professor`), 'Registro alterado em evento_professor.', JSON_OBJECT('id_evento', OLD.`id_evento`, 'id_professor', OLD.`id_professor`, 'principal', OLD.`principal`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_evento', NEW.`id_evento`, 'id_professor', NEW.`id_professor`, 'principal', NEW.`principal`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_professor_ad`
AFTER DELETE ON `evento_professor`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'evento_professor', NULL, CONCAT('id_evento=', OLD.`id_evento`, ', ', 'id_professor=', OLD.`id_professor`), 'Registro excluído de evento_professor.', JSON_OBJECT('id_evento', OLD.`id_evento`, 'id_professor', OLD.`id_professor`, 'principal', OLD.`principal`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_inscricao_evento_bi`$$
DROP TRIGGER IF EXISTS `trg_inscricao_evento_bu`$$
DROP TRIGGER IF EXISTS `trg_inscricao_evento_ai`$$
DROP TRIGGER IF EXISTS `trg_inscricao_evento_au`$$
DROP TRIGGER IF EXISTS `trg_inscricao_evento_ad`$$
CREATE TRIGGER `trg_inscricao_evento_bi`
BEFORE INSERT ON `inscricao_evento`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_inscricao_evento_bu`
BEFORE UPDATE ON `inscricao_evento`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_inscricao_evento_ai`
AFTER INSERT ON `inscricao_evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'inscricao_evento', NEW.`id_inscricao`, CONCAT('id_inscricao=', NEW.`id_inscricao`), 'Registro criado em inscricao_evento.', NULL, JSON_OBJECT('id_inscricao', NEW.`id_inscricao`, 'id_aluno', NEW.`id_aluno`, 'id_evento', NEW.`id_evento`, 'data_inscricao', NEW.`data_inscricao`, 'status', NEW.`status`, 'observacao', NEW.`observacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_inscricao_evento_au`
AFTER UPDATE ON `inscricao_evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'inscricao_evento', NEW.`id_inscricao`, CONCAT('id_inscricao=', NEW.`id_inscricao`), 'Registro alterado em inscricao_evento.', JSON_OBJECT('id_inscricao', OLD.`id_inscricao`, 'id_aluno', OLD.`id_aluno`, 'id_evento', OLD.`id_evento`, 'data_inscricao', OLD.`data_inscricao`, 'status', OLD.`status`, 'observacao', OLD.`observacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_inscricao', NEW.`id_inscricao`, 'id_aluno', NEW.`id_aluno`, 'id_evento', NEW.`id_evento`, 'data_inscricao', NEW.`data_inscricao`, 'status', NEW.`status`, 'observacao', NEW.`observacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_inscricao_evento_ad`
AFTER DELETE ON `inscricao_evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'inscricao_evento', OLD.`id_inscricao`, CONCAT('id_inscricao=', OLD.`id_inscricao`), 'Registro excluído de inscricao_evento.', JSON_OBJECT('id_inscricao', OLD.`id_inscricao`, 'id_aluno', OLD.`id_aluno`, 'id_evento', OLD.`id_evento`, 'data_inscricao', OLD.`data_inscricao`, 'status', OLD.`status`, 'observacao', OLD.`observacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_presenca_evento_bi`$$
DROP TRIGGER IF EXISTS `trg_presenca_evento_bu`$$
DROP TRIGGER IF EXISTS `trg_presenca_evento_ai`$$
DROP TRIGGER IF EXISTS `trg_presenca_evento_au`$$
DROP TRIGGER IF EXISTS `trg_presenca_evento_ad`$$
CREATE TRIGGER `trg_presenca_evento_bi`
BEFORE INSERT ON `presenca_evento`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_presenca_evento_bu`
BEFORE UPDATE ON `presenca_evento`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_presenca_evento_ai`
AFTER INSERT ON `presenca_evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'presenca_evento', NEW.`id_presenca`, CONCAT('id_presenca=', NEW.`id_presenca`), 'Registro criado em presenca_evento.', NULL, JSON_OBJECT('id_presenca', NEW.`id_presenca`, 'id_inscricao', NEW.`id_inscricao`, 'status', NEW.`status`, 'data_presenca', NEW.`data_presenca`, 'observacao', NEW.`observacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_presenca_evento_au`
AFTER UPDATE ON `presenca_evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'presenca_evento', NEW.`id_presenca`, CONCAT('id_presenca=', NEW.`id_presenca`), 'Registro alterado em presenca_evento.', JSON_OBJECT('id_presenca', OLD.`id_presenca`, 'id_inscricao', OLD.`id_inscricao`, 'status', OLD.`status`, 'data_presenca', OLD.`data_presenca`, 'observacao', OLD.`observacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_presenca', NEW.`id_presenca`, 'id_inscricao', NEW.`id_inscricao`, 'status', NEW.`status`, 'data_presenca', NEW.`data_presenca`, 'observacao', NEW.`observacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_presenca_evento_ad`
AFTER DELETE ON `presenca_evento`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'presenca_evento', OLD.`id_presenca`, CONCAT('id_presenca=', OLD.`id_presenca`), 'Registro excluído de presenca_evento.', JSON_OBJECT('id_presenca', OLD.`id_presenca`, 'id_inscricao', OLD.`id_inscricao`, 'status', OLD.`status`, 'data_presenca', OLD.`data_presenca`, 'observacao', OLD.`observacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_comissao_bi`$$
DROP TRIGGER IF EXISTS `trg_comissao_bu`$$
DROP TRIGGER IF EXISTS `trg_comissao_ai`$$
DROP TRIGGER IF EXISTS `trg_comissao_au`$$
DROP TRIGGER IF EXISTS `trg_comissao_ad`$$
CREATE TRIGGER `trg_comissao_bi`
BEFORE INSERT ON `comissao`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_comissao_bu`
BEFORE UPDATE ON `comissao`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_comissao_ai`
AFTER INSERT ON `comissao`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'comissao', NEW.`id_comissao`, CONCAT('id_comissao=', NEW.`id_comissao`), 'Registro criado em comissao.', NULL, JSON_OBJECT('id_comissao', NEW.`id_comissao`, 'id_evento', NEW.`id_evento`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_comissao_au`
AFTER UPDATE ON `comissao`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'comissao', NEW.`id_comissao`, CONCAT('id_comissao=', NEW.`id_comissao`), 'Registro alterado em comissao.', JSON_OBJECT('id_comissao', OLD.`id_comissao`, 'id_evento', OLD.`id_evento`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_comissao', NEW.`id_comissao`, 'id_evento', NEW.`id_evento`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_comissao_ad`
AFTER DELETE ON `comissao`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'comissao', OLD.`id_comissao`, CONCAT('id_comissao=', OLD.`id_comissao`), 'Registro excluído de comissao.', JSON_OBJECT('id_comissao', OLD.`id_comissao`, 'id_evento', OLD.`id_evento`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_comissao_aluno_bi`$$
DROP TRIGGER IF EXISTS `trg_comissao_aluno_bu`$$
DROP TRIGGER IF EXISTS `trg_comissao_aluno_ai`$$
DROP TRIGGER IF EXISTS `trg_comissao_aluno_au`$$
DROP TRIGGER IF EXISTS `trg_comissao_aluno_ad`$$
CREATE TRIGGER `trg_comissao_aluno_bi`
BEFORE INSERT ON `comissao_aluno`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_comissao_aluno_bu`
BEFORE UPDATE ON `comissao_aluno`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_comissao_aluno_ai`
AFTER INSERT ON `comissao_aluno`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'comissao_aluno', NULL, CONCAT('id_comissao=', NEW.`id_comissao`, ', ', 'id_aluno=', NEW.`id_aluno`), 'Registro criado em comissao_aluno.', NULL, JSON_OBJECT('id_comissao', NEW.`id_comissao`, 'id_aluno', NEW.`id_aluno`, 'funcao', NEW.`funcao`, 'data_entrada', NEW.`data_entrada`, 'data_saida', NEW.`data_saida`, 'ativo', NEW.`ativo`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_comissao_aluno_au`
AFTER UPDATE ON `comissao_aluno`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'comissao_aluno', NULL, CONCAT('id_comissao=', NEW.`id_comissao`, ', ', 'id_aluno=', NEW.`id_aluno`), 'Registro alterado em comissao_aluno.', JSON_OBJECT('id_comissao', OLD.`id_comissao`, 'id_aluno', OLD.`id_aluno`, 'funcao', OLD.`funcao`, 'data_entrada', OLD.`data_entrada`, 'data_saida', OLD.`data_saida`, 'ativo', OLD.`ativo`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_comissao', NEW.`id_comissao`, 'id_aluno', NEW.`id_aluno`, 'funcao', NEW.`funcao`, 'data_entrada', NEW.`data_entrada`, 'data_saida', NEW.`data_saida`, 'ativo', NEW.`ativo`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_comissao_aluno_ad`
AFTER DELETE ON `comissao_aluno`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'comissao_aluno', NULL, CONCAT('id_comissao=', OLD.`id_comissao`, ', ', 'id_aluno=', OLD.`id_aluno`), 'Registro excluído de comissao_aluno.', JSON_OBJECT('id_comissao', OLD.`id_comissao`, 'id_aluno', OLD.`id_aluno`, 'funcao', OLD.`funcao`, 'data_entrada', OLD.`data_entrada`, 'data_saida', OLD.`data_saida`, 'ativo', OLD.`ativo`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_atividade_bi`$$
DROP TRIGGER IF EXISTS `trg_atividade_bu`$$
DROP TRIGGER IF EXISTS `trg_atividade_ai`$$
DROP TRIGGER IF EXISTS `trg_atividade_au`$$
DROP TRIGGER IF EXISTS `trg_atividade_ad`$$
CREATE TRIGGER `trg_atividade_bi`
BEFORE INSERT ON `atividade`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_atividade_bu`
BEFORE UPDATE ON `atividade`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_atividade_ai`
AFTER INSERT ON `atividade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'atividade', NEW.`id_atividade`, CONCAT('id_atividade=', NEW.`id_atividade`), 'Registro criado em atividade.', NULL, JSON_OBJECT('id_atividade', NEW.`id_atividade`, 'id_evento', NEW.`id_evento`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'data_inicio', NEW.`data_inicio`, 'data_fim', NEW.`data_fim`, 'local', NEW.`local`, 'capacidade', NEW.`capacidade`, 'status', NEW.`status`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_atividade_au`
AFTER UPDATE ON `atividade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'atividade', NEW.`id_atividade`, CONCAT('id_atividade=', NEW.`id_atividade`), 'Registro alterado em atividade.', JSON_OBJECT('id_atividade', OLD.`id_atividade`, 'id_evento', OLD.`id_evento`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'data_inicio', OLD.`data_inicio`, 'data_fim', OLD.`data_fim`, 'local', OLD.`local`, 'capacidade', OLD.`capacidade`, 'status', OLD.`status`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_atividade', NEW.`id_atividade`, 'id_evento', NEW.`id_evento`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'data_inicio', NEW.`data_inicio`, 'data_fim', NEW.`data_fim`, 'local', NEW.`local`, 'capacidade', NEW.`capacidade`, 'status', NEW.`status`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_atividade_ad`
AFTER DELETE ON `atividade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'atividade', OLD.`id_atividade`, CONCAT('id_atividade=', OLD.`id_atividade`), 'Registro excluído de atividade.', JSON_OBJECT('id_atividade', OLD.`id_atividade`, 'id_evento', OLD.`id_evento`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'data_inicio', OLD.`data_inicio`, 'data_fim', OLD.`data_fim`, 'local', OLD.`local`, 'capacidade', OLD.`capacidade`, 'status', OLD.`status`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_comissao_atividade_bi`$$
DROP TRIGGER IF EXISTS `trg_comissao_atividade_bu`$$
DROP TRIGGER IF EXISTS `trg_comissao_atividade_ai`$$
DROP TRIGGER IF EXISTS `trg_comissao_atividade_au`$$
DROP TRIGGER IF EXISTS `trg_comissao_atividade_ad`$$
CREATE TRIGGER `trg_comissao_atividade_bi`
BEFORE INSERT ON `comissao_atividade`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_comissao_atividade_bu`
BEFORE UPDATE ON `comissao_atividade`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_comissao_atividade_ai`
AFTER INSERT ON `comissao_atividade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'comissao_atividade', NULL, CONCAT('id_comissao=', NEW.`id_comissao`, ', ', 'id_atividade=', NEW.`id_atividade`), 'Registro criado em comissao_atividade.', NULL, JSON_OBJECT('id_comissao', NEW.`id_comissao`, 'id_atividade', NEW.`id_atividade`, 'status', NEW.`status`, 'observacao', NEW.`observacao`, 'data_atribuicao', NEW.`data_atribuicao`, 'data_conclusao', NEW.`data_conclusao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_comissao_atividade_au`
AFTER UPDATE ON `comissao_atividade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'comissao_atividade', NULL, CONCAT('id_comissao=', NEW.`id_comissao`, ', ', 'id_atividade=', NEW.`id_atividade`), 'Registro alterado em comissao_atividade.', JSON_OBJECT('id_comissao', OLD.`id_comissao`, 'id_atividade', OLD.`id_atividade`, 'status', OLD.`status`, 'observacao', OLD.`observacao`, 'data_atribuicao', OLD.`data_atribuicao`, 'data_conclusao', OLD.`data_conclusao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_comissao', NEW.`id_comissao`, 'id_atividade', NEW.`id_atividade`, 'status', NEW.`status`, 'observacao', NEW.`observacao`, 'data_atribuicao', NEW.`data_atribuicao`, 'data_conclusao', NEW.`data_conclusao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_comissao_atividade_ad`
AFTER DELETE ON `comissao_atividade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'comissao_atividade', NULL, CONCAT('id_comissao=', OLD.`id_comissao`, ', ', 'id_atividade=', OLD.`id_atividade`), 'Registro excluído de comissao_atividade.', JSON_OBJECT('id_comissao', OLD.`id_comissao`, 'id_atividade', OLD.`id_atividade`, 'status', OLD.`status`, 'observacao', OLD.`observacao`, 'data_atribuicao', OLD.`data_atribuicao`, 'data_conclusao', OLD.`data_conclusao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_bi`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_bu`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_ai`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_au`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_ad`$$
CREATE TRIGGER `trg_responsabilidade_bi`
BEFORE INSERT ON `responsabilidade`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_responsabilidade_bu`
BEFORE UPDATE ON `responsabilidade`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_responsabilidade_ai`
AFTER INSERT ON `responsabilidade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'responsabilidade', NEW.`id_responsabilidade`, CONCAT('id_responsabilidade=', NEW.`id_responsabilidade`), 'Registro criado em responsabilidade.', NULL, JSON_OBJECT('id_responsabilidade', NEW.`id_responsabilidade`, 'id_evento', NEW.`id_evento`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'status', NEW.`status`, 'observacao', NEW.`observacao`, 'data_atribuicao', NEW.`data_atribuicao`, 'data_conclusao', NEW.`data_conclusao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_responsabilidade_au`
AFTER UPDATE ON `responsabilidade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'responsabilidade', NEW.`id_responsabilidade`, CONCAT('id_responsabilidade=', NEW.`id_responsabilidade`), 'Registro alterado em responsabilidade.', JSON_OBJECT('id_responsabilidade', OLD.`id_responsabilidade`, 'id_evento', OLD.`id_evento`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'status', OLD.`status`, 'observacao', OLD.`observacao`, 'data_atribuicao', OLD.`data_atribuicao`, 'data_conclusao', OLD.`data_conclusao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_responsabilidade', NEW.`id_responsabilidade`, 'id_evento', NEW.`id_evento`, 'nome', NEW.`nome`, 'descricao', NEW.`descricao`, 'status', NEW.`status`, 'observacao', NEW.`observacao`, 'data_atribuicao', NEW.`data_atribuicao`, 'data_conclusao', NEW.`data_conclusao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_responsabilidade_ad`
AFTER DELETE ON `responsabilidade`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'responsabilidade', OLD.`id_responsabilidade`, CONCAT('id_responsabilidade=', OLD.`id_responsabilidade`), 'Registro excluído de responsabilidade.', JSON_OBJECT('id_responsabilidade', OLD.`id_responsabilidade`, 'id_evento', OLD.`id_evento`, 'nome', OLD.`nome`, 'descricao', OLD.`descricao`, 'status', OLD.`status`, 'observacao', OLD.`observacao`, 'data_atribuicao', OLD.`data_atribuicao`, 'data_conclusao', OLD.`data_conclusao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_comissao_bi`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_comissao_bu`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_comissao_ai`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_comissao_au`$$
DROP TRIGGER IF EXISTS `trg_responsabilidade_comissao_ad`$$
CREATE TRIGGER `trg_responsabilidade_comissao_bi`
BEFORE INSERT ON `responsabilidade_comissao`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_responsabilidade_comissao_bu`
BEFORE UPDATE ON `responsabilidade_comissao`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_responsabilidade_comissao_ai`
AFTER INSERT ON `responsabilidade_comissao`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'responsabilidade_comissao', NULL, CONCAT('id_responsabilidade=', NEW.`id_responsabilidade`, ', ', 'id_comissao=', NEW.`id_comissao`), 'Registro criado em responsabilidade_comissao.', NULL, JSON_OBJECT('id_responsabilidade', NEW.`id_responsabilidade`, 'id_comissao', NEW.`id_comissao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_responsabilidade_comissao_au`
AFTER UPDATE ON `responsabilidade_comissao`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'responsabilidade_comissao', NULL, CONCAT('id_responsabilidade=', NEW.`id_responsabilidade`, ', ', 'id_comissao=', NEW.`id_comissao`), 'Registro alterado em responsabilidade_comissao.', JSON_OBJECT('id_responsabilidade', OLD.`id_responsabilidade`, 'id_comissao', OLD.`id_comissao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_responsabilidade', NEW.`id_responsabilidade`, 'id_comissao', NEW.`id_comissao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_responsabilidade_comissao_ad`
AFTER DELETE ON `responsabilidade_comissao`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'responsabilidade_comissao', NULL, CONCAT('id_responsabilidade=', OLD.`id_responsabilidade`, ', ', 'id_comissao=', OLD.`id_comissao`), 'Registro excluído de responsabilidade_comissao.', JSON_OBJECT('id_responsabilidade', OLD.`id_responsabilidade`, 'id_comissao', OLD.`id_comissao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_agente_externo_bi`$$
DROP TRIGGER IF EXISTS `trg_agente_externo_bu`$$
DROP TRIGGER IF EXISTS `trg_agente_externo_ai`$$
DROP TRIGGER IF EXISTS `trg_agente_externo_au`$$
DROP TRIGGER IF EXISTS `trg_agente_externo_ad`$$
CREATE TRIGGER `trg_agente_externo_bi`
BEFORE INSERT ON `agente_externo`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_agente_externo_bu`
BEFORE UPDATE ON `agente_externo`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_agente_externo_ai`
AFTER INSERT ON `agente_externo`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'agente_externo', NEW.`id_agente`, CONCAT('id_agente=', NEW.`id_agente`), 'Registro criado em agente_externo.', NULL, JSON_OBJECT('id_agente', NEW.`id_agente`, 'nome', NEW.`nome`, 'email', NEW.`email`, 'telefone', NEW.`telefone`, 'empresa', NEW.`empresa`, 'cargo', NEW.`cargo`, 'especialidade', NEW.`especialidade`, 'observacao', NEW.`observacao`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_agente_externo_au`
AFTER UPDATE ON `agente_externo`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'agente_externo', NEW.`id_agente`, CONCAT('id_agente=', NEW.`id_agente`), 'Registro alterado em agente_externo.', JSON_OBJECT('id_agente', OLD.`id_agente`, 'nome', OLD.`nome`, 'email', OLD.`email`, 'telefone', OLD.`telefone`, 'empresa', OLD.`empresa`, 'cargo', OLD.`cargo`, 'especialidade', OLD.`especialidade`, 'observacao', OLD.`observacao`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_agente', NEW.`id_agente`, 'nome', NEW.`nome`, 'email', NEW.`email`, 'telefone', NEW.`telefone`, 'empresa', NEW.`empresa`, 'cargo', NEW.`cargo`, 'especialidade', NEW.`especialidade`, 'observacao', NEW.`observacao`, 'ativo', NEW.`ativo`, 'data_cadastro', NEW.`data_cadastro`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_agente_externo_ad`
AFTER DELETE ON `agente_externo`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'agente_externo', OLD.`id_agente`, CONCAT('id_agente=', OLD.`id_agente`), 'Registro excluído de agente_externo.', JSON_OBJECT('id_agente', OLD.`id_agente`, 'nome', OLD.`nome`, 'email', OLD.`email`, 'telefone', OLD.`telefone`, 'empresa', OLD.`empresa`, 'cargo', OLD.`cargo`, 'especialidade', OLD.`especialidade`, 'observacao', OLD.`observacao`, 'ativo', OLD.`ativo`, 'data_cadastro', OLD.`data_cadastro`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DROP TRIGGER IF EXISTS `trg_evento_agente_externo_bi`$$
DROP TRIGGER IF EXISTS `trg_evento_agente_externo_bu`$$
DROP TRIGGER IF EXISTS `trg_evento_agente_externo_ai`$$
DROP TRIGGER IF EXISTS `trg_evento_agente_externo_au`$$
DROP TRIGGER IF EXISTS `trg_evento_agente_externo_ad`$$
CREATE TRIGGER `trg_evento_agente_externo_bi`
BEFORE INSERT ON `evento_agente_externo`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_agente_externo_bu`
BEFORE UPDATE ON `evento_agente_externo`
FOR EACH ROW
BEGIN
    SET NEW.alterado_em = CURRENT_TIMESTAMP;
    SET NEW.alterado_por = @etask_usuario_id;
END$$
CREATE TRIGGER `trg_evento_agente_externo_ai`
AFTER INSERT ON `evento_agente_externo`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'CADASTRO', 'evento_agente_externo', NULL, CONCAT('id_evento=', NEW.`id_evento`, ', ', 'id_agente=', NEW.`id_agente`), 'Registro criado em evento_agente_externo.', NULL, JSON_OBJECT('id_evento', NEW.`id_evento`, 'id_agente', NEW.`id_agente`, 'tipo_participacao', NEW.`tipo_participacao`, 'tema', NEW.`tema`, 'observacao', NEW.`observacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_agente_externo_au`
AFTER UPDATE ON `evento_agente_externo`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'ALTERACAO', 'evento_agente_externo', NULL, CONCAT('id_evento=', NEW.`id_evento`, ', ', 'id_agente=', NEW.`id_agente`), 'Registro alterado em evento_agente_externo.', JSON_OBJECT('id_evento', OLD.`id_evento`, 'id_agente', OLD.`id_agente`, 'tipo_participacao', OLD.`tipo_participacao`, 'tema', OLD.`tema`, 'observacao', OLD.`observacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), JSON_OBJECT('id_evento', NEW.`id_evento`, 'id_agente', NEW.`id_agente`, 'tipo_participacao', NEW.`tipo_participacao`, 'tema', NEW.`tema`, 'observacao', NEW.`observacao`, 'alterado_em', NEW.`alterado_em`, 'alterado_por', NEW.`alterado_por`));
END$$
CREATE TRIGGER `trg_evento_agente_externo_ad`
AFTER DELETE ON `evento_agente_externo`
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao, dados_anteriores, dados_novos)
    VALUES (@etask_usuario_id, 'EXCLUSAO', 'evento_agente_externo', NULL, CONCAT('id_evento=', OLD.`id_evento`, ', ', 'id_agente=', OLD.`id_agente`), 'Registro excluído de evento_agente_externo.', JSON_OBJECT('id_evento', OLD.`id_evento`, 'id_agente', OLD.`id_agente`, 'tipo_participacao', OLD.`tipo_participacao`, 'tema', OLD.`tema`, 'observacao', OLD.`observacao`, 'alterado_em', OLD.`alterado_em`, 'alterado_por', OLD.`alterado_por`), NULL);
END$$
DELIMITER ;

CREATE OR REPLACE VIEW vw_auditoria AS
SELECT
    a.id_auditoria,
    a.id_usuario,
    COALESCE(u.nome, 'SISTEMA') AS usuario,
    a.acao,
    a.tabela,
    a.id_registro,
    a.registro_chave,
    a.descricao,
    a.dados_anteriores,
    a.dados_novos,
    a.data_hora,
    a.alterado_em,
    a.alterado_por
FROM auditoria a
LEFT JOIN usuario u ON u.id_usuario = a.id_usuario;

