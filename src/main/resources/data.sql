INSERT INTO perfil (nome) VALUES ('ROLE_ADMIN');

INSERT INTO usuario (nome, email, senha_hash, enabled, perfil_id)
VALUES (
    'Administrador',
    'admin',
    '$2a$12$7LK1XrIFO/bVSe0quxodCeKM.fMbGvltSl1ohXu1e9izp1OfKbqf6',
    TRUE,
    (SELECT id FROM perfil WHERE nome = 'ROLE_ADMIN')
);
