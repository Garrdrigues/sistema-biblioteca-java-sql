
-- 1. CRIAÇÃO DAS TABELAS (DDL)

-- Tabela 1: item
CREATE TABLE item (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(150) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('livro', 'revista')),
    autor VARCHAR(100),
    edicao INT,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabela 2: usuario
CREATE TABLE usuario (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('aluno', 'professor')),
    limite_itens INT NOT NULL CHECK (limite_itens > 0)
);

-- Tabela 3: emprestimo (depende de item e usuario)
CREATE TABLE emprestimo (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id INT NOT NULL REFERENCES item(id),
    usuario_id INT NOT NULL REFERENCES usuario(id),
    data_retirada DATE NOT NULL,
    data_devolucao_prevista DATE NOT NULL,
    data_devolucao DATE, -- Permanece NULL enquanto o empréstimo estiver em aberto
    valor_multa NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (valor_multa >= 0)
);


-- 2. INSERÇÃO DE DADOS DE TESTE (DML)

INSERT INTO item (codigo, titulo, tipo, autor, edicao, disponivel) VALUES
('LIV-001', 'Sistemas de Banco de Dados', 'livro', 'Elmasri & Navathe', NULL, FALSE),
('LIV-002', 'Arquitetura Limpa', 'livro', 'Robert C. Martin', NULL, TRUE),
('REV-001', 'MIT Technology Review Brasil', 'revista', NULL, 45, TRUE),
('REV-002', 'Revista IEEE Software', 'revista', NULL, 118, TRUE);

INSERT INTO usuario (nome, tipo, limite_itens) VALUES
('Gabriel Rodrigues', 'aluno', 3),
('Juliana Costa', 'professor', 5);

INSERT INTO emprestimo (item_id, usuario_id, data_retirada, data_devolucao_prevista, data_devolucao, valor_multa) VALUES
(1, 1, '2026-10-15', '2026-10-29', NULL, 0.00),          
(3, 1, '2026-10-01', '2026-10-08', '2026-10-11', 3.00),  
(2, 2, '2026-10-05', '2026-10-19', '2026-10-18', 0.00);  