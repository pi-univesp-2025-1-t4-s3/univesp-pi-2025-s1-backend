CREATE TABLE public.perfil (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE public.usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    perfil_id INTEGER NOT NULL REFERENCES perfil(id)
);

CREATE TABLE public.produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10,2) NOT NULL
);

CREATE TABLE public.estoque (
    id SERIAL PRIMARY KEY,
    quantidade INTEGER NOT NULL,
    produto_id INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id)
        REFERENCES produto (id)
        ON DELETE CASCADE
);

CREATE TABLE public.venda (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL
);

CREATE TABLE public.item_venda (
    id SERIAL PRIMARY KEY,
    quantidade INTEGER NOT NULL,
    venda_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    CONSTRAINT fk_item_venda_venda FOREIGN KEY (venda_id)
        REFERENCES venda (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_item_venda_produto FOREIGN KEY (produto_id)
        REFERENCES produto (id)
        ON DELETE CASCADE
);
