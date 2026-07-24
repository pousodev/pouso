-- Catálogo de tipos de pet (espécie -> raça, usando tipo_mae)
INSERT INTO tipo_pet (nome) VALUES ('Cachorro');              -- id 1
INSERT INTO tipo_pet (nome) VALUES ('Gato');                   -- id 2
INSERT INTO tipo_pet (tipo_mae, nome) VALUES (1, 'Vira-lata'); -- id 3
INSERT INTO tipo_pet (tipo_mae, nome) VALUES (1, 'Labrador');  -- id 4
INSERT INTO tipo_pet (tipo_mae, nome) VALUES (2, 'Siamês');    -- id 5

-- Moderador de teste
INSERT INTO pessoa (cpf, nome, email, senha)
VALUES ('11111111111', 'Ana Moderadora', 'moderadora@pouso.com', 'senha123');
INSERT INTO administrador (cpf, nivel) VALUES ('11111111111', 'M');

-- Doador 1
INSERT INTO pessoa (cpf, nome, email, senha)
VALUES ('66666666666', 'Carlos Doador', 'carlos@pouso.com', 'senha123');
INSERT INTO usuario (cpf, username, bio, genero, telefone)
VALUES ('66666666666', 'carlosd', 'Adoro cuidar de bichos', 'M', '51999990001');

-- Doador 2 (corrigido: mesmo cpf nas duas tabelas)
INSERT INTO pessoa (cpf, nome, email, senha)
VALUES ('33333333642', 'Victor Doador', 'victor@pouso.com', 'senha123');
INSERT INTO usuario (cpf, username, bio, genero, telefone)
VALUES ('33333333642', 'victord', 'Resgato animais de rua', 'M', '51999990002');

-- Pets aguardando aprovação (solicitações pendentes)
INSERT INTO pet (nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte, is_permanente, is_castrado, status_aprovacao)
VALUES ('Rex', '66666666666', 'Cachorro dócil, adora brincar', 'M', 4, '2023-05-10', CURRENT_DATE, 'M', true, true, 'PENDENTE');

INSERT INTO pet (nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte, is_permanente, is_castrado, status_aprovacao)
VALUES ('Bob', '33333333642', 'Filhote encontrada na rua', 'F', 3, '2024-11-20', CURRENT_DATE, 'P', true, false, 'PENDENTE');

INSERT INTO pet (nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte, is_permanente, is_castrado, status_aprovacao)
VALUES ('Thor', '33333333642', 'Cão de porte grande, muito calmo', 'M', 3, '2020-01-15', CURRENT_DATE, 'G', true, true, 'PENDENTE');

INSERT INTO pet (nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte, is_permanente, is_castrado, status_aprovacao)
VALUES ('Mel', '33333333642', 'Gata calma, gosta de colo', 'F', 5, '2022-08-02', CURRENT_DATE, 'P', true, true, 'PENDENTE');
