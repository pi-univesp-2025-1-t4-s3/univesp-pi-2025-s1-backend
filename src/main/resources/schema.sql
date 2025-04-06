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
