-- Inserindo dados de exemplo

-- Pessoas
INSERT INTO pessoa (cpf, nome, email, senha) VALUES
('11111111111', 'Admin User', 'admin@pouso.com', 'admin123'),
('22222222222', 'John Doe', 'john.doe@example.com', 'user123'),
('33333333333', 'Jane Doe', 'jane.doe@example.com', 'user123');

-- Administrador
INSERT INTO administrador (cpf, nivel) VALUES
('11111111111', 'M');

-- Usuários
INSERT INTO usuario (cpf, username, bio, genero, telefone, foto_perfil) VALUES
('22222222222', 'johndoe', 'Amante de animais.', 'M', '11987654321', NULL),
('33333333333', 'janedoe', 'Adoro gatos!', 'F', '11912345678', NULL);

-- Tipos de Pet
INSERT INTO tipo_pet (tipo_mae, nome) VALUES (NULL, 'Cachorro');
INSERT INTO tipo_pet (tipo_mae, nome) VALUES ((SELECT id FROM tipo_pet WHERE nome = 'Cachorro'), 'Golden Retriever');
INSERT INTO tipo_pet (tipo_mae, nome) VALUES ((SELECT id FROM tipo_pet WHERE nome = 'Cachorro'), 'Bulldog');

-- Pets
INSERT INTO pet (nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte, is_permanente, is_castrado, adm_aprovou) VALUES
('Max', '22222222222', 'Um golden retriever amigável.', 'M', (SELECT id FROM tipo_pet WHERE nome = 'Golden Retriever'), '2022-01-15', '2024-05-10', 'G', false, true, '11111111111'),
('Bella', '33333333333', 'Uma bulldog calma e dócil.', 'F', (SELECT id FROM tipo_pet WHERE nome = 'Bulldog'), '2023-03-20', '2024-05-12', 'M', false, true, '11111111111'),
('Rex', '22222222222', 'Um vira-lata brincalhão.', 'M', (SELECT id FROM tipo_pet WHERE nome = 'Cachorro'), '2021-07-01', '2024-05-15', 'M', false, true, NULL);
