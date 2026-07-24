-- =========================================================================
-- 1. TABELA: tipo_pet (Categorias base para os pets)
-- =========================================================================
INSERT INTO tipo_pet (nome) VALUES
('Cachorro'), -- 1
('Gato'),     -- 2
('Roedor'),   -- 3
('Ave');      -- 4

-- =========================================================================
-- 2. TABELA: pet
-- Diversificando: alguns com vários pets, outros com apenas um, e booleanos mistos.
-- =========================================================================
INSERT INTO pet (nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte, is_permanente, is_castrado, adm_aprovou) VALUES
-- Usuário '33333333333' (João Silva) possui 2 pets permanentes
('Rex', '33333333333', 'Cachorro brincalhão e enérgico', 'M', 1, '2020-05-10', '2023-01-15', 'G', true, true, '11111111111'),
('Mimi', '33333333333', 'Gata preguiçosa e carinhosa', 'F', 2, '2019-10-20', '2023-01-15', 'P', true, true, '11111111111'),

-- Usuário '44444444444' (Maria Souza - Veterinária) possui 3 pets, alguns temporários para adoção
('Bidu', '44444444444', 'Cachorro resgatado, muito dócil', 'M', 1, '2021-02-14', '2023-02-10', 'M', false, false, '22222222222'),
('Nina', '44444444444', 'Gatinha assustada, precisa de paciência', 'F', 2, '2022-08-05', '2023-03-01', 'P', false, true, '22222222222'),
('Piu', '44444444444', 'Calopsita cantora (Posse definitiva)', 'M', 4, '2022-01-10', '2023-04-12', 'P', true, false, '11111111111'),

-- Usuário '10000000001' (Lucas Almeida) possui 1 pet para adoção
('Thor', '10000000001', 'Pitbull mansinho, adora crianças', 'M', 1, '2018-11-20', '2023-05-20', 'G', false, true, '11111111111'),

-- Usuário '10000000010' (Amanda Rodrigues) possui 1 pet permanente
('Luna', '10000000010', 'Vira-lata caramelo clássica', 'F', 1, '2020-12-25', '2023-06-15', 'M', true, true, '22222222222'),

-- Usuário '10000000022' (Mariana Neves) possui 2 pets permanentes (Irmãos)
('Simba', '10000000022', 'Golden Retriever muito amigável', 'M', 1, '2019-04-04', '2023-07-10', 'G', true, true, '11111111111'),
('Nala', '10000000022', 'Golden Retriever, irmã do Simba', 'F', 1, '2019-04-04', '2023-07-10', 'G', true, true, '11111111111'),

-- Usuário '10000000042' (Natália Lopes - "Mãe de pet") possui 4 gatos!
('Frajola', '10000000042', 'Gato preto e branco, o chefe da casa', 'M', 2, '2021-03-12', '2023-08-01', 'M', true, true, '22222222222'),
('Garfield', '10000000042', 'Adora comer e dormir', 'M', 2, '2017-07-18', '2023-08-01', 'G', true, true, '22222222222'),
('Marie', '10000000042', 'Gatinha branca, muito vaidosa', 'F', 2, '2022-10-10', '2023-08-01', 'P', true, false, '22222222222'),
('Mingau', '10000000042', 'Filhote resgatado, em busca de um lar', 'M', 2, '2023-01-05', '2023-08-01', 'P', false, false, '22222222222'),

-- Usuário '10000000096' (Karol Conka) possui 1 pet permanente
('Scooby', '10000000096', 'Medroso, mas muito leal', 'M', 1, '2016-09-09', '2023-09-10', 'G', true, true, '11111111111'),


-- Usuário '10000000012' (Bruna Gomes)
('Bolinha', '10000000012', 'Gato muito fofinho e comilão', 'M', 2, '2020-02-10', '2023-10-01', 'M', true, true, '11111111111'),

-- Usuário '10000000033' (André Silva - Ciclista)
('Marley', '10000000033', 'Companheiro de trilhas e corridas', 'M', 1, '2019-06-15', '2023-10-02', 'G', true, true, '22222222222'),

-- Usuário '10000000055' (Lorena Porto) - Resgatou uma cachorrinha, está para adoção
('Mel', '10000000055', 'Encontrada na estrada, dócil e carente', 'F', 1, '2022-01-20', '2023-10-05', 'P', false, false, '11111111111'),

-- Usuário '10000000078' (Gal Costa)
('Zeus', '10000000078', 'Pastor Alemão protetor da casa', 'M', 1, '2018-11-30', '2023-10-06', 'G', true, true, '22222222222'),

-- Usuário '10000000089' (Seu Jorge)
('Chico', '10000000089', 'Papagaio falador que adora música', 'M', 4, '2015-04-12', '2023-10-07', 'P', true, false, '11111111111'),

-- Usuário '10000000028' (Caroline Vieira - Colecionadora de plantas)
('Amora', '10000000028', 'Gosta de dormir nos vasos de planta', 'F', 2, '2021-08-22', '2023-10-08', 'P', true, true, '22222222222'),

-- Usuário '10000000045' (Murilo Campos)
('Paçoca', '10000000045', 'Porquinho da índia muito agitado', 'M', 3, '2023-05-10', '2023-10-09', 'P', true, false, '11111111111'),

-- Usuário '10000000067' (Gisele Bundchen)
('Lulu', '10000000067', 'Lulu da Pomerânia, super vaidosa', 'F', 1, '2020-09-09', '2023-10-10', 'P', true, true, '22222222222'),

-- Usuário '55555555555' (Carlos Pereira) - Lar temporário
('Fred', '55555555555', 'Cachorro brincalhão aguardando um lar definitivo', 'M', 1, '2022-12-01', '2023-10-11', 'M', false, true, '11111111111'),

-- Usuário '10000000019' (Bruno Cavalcanti)
('Pipoca', '10000000019', 'Hamster anão russo, adora a rodinha', 'F', 3, '2023-08-15', '2023-10-12', 'P', true, false, '22222222222'),

-- Usuário '10000000052' (Alexandre Frota)
('Belinha', '10000000052', 'Poodle toy bem companheira', 'F', 1, '2017-03-03', '2023-10-13', 'P', true, true, '11111111111'),

-- Usuário '10000000072' (Caetano Veloso) - Gato resgatado para adoção
('Simão', '10000000072', 'Gato rajado charmoso, procura casa segura', 'M', 2, '2021-11-11', '2023-10-14', 'M', false, true, '22222222222'),

-- Usuário '10000000093' (Projota Tiago)
('Apolo', '10000000093', 'Pitbull de linhagem, muito manso e babão', 'M', 1, '2019-07-25', '2023-10-15', 'G', true, true, '11111111111'),

-- Usuário '10000000024' (Beatriz Souza)
('Juma', '10000000024', 'Gata de rua resgatada que virou onça mansa', 'F', 2, '2022-02-18', '2023-10-16', 'M', true, true, '22222222222'),

-- Usuário '10000000084' (Thiaguinho Silva) - Possui duas aves irmãs
('Tico', '10000000084', 'Canário belga cantor', 'M', 4, '2022-10-01', '2023-10-17', 'P', true, false, '11111111111'),
('Teco', '10000000084', 'Canário belga, irmão do Tico', 'M', 4, '2022-10-01', '2023-10-17', 'P', true, false, '11111111111'),

-- Usuário '10000000011' (Daniel Carvalho) - Tem um pet permanente e um para adoção
('Bela', '10000000011', 'Labradora muito inteligente e leal', 'F', 1, '2020-05-20', '2023-10-18', 'G', true, true, '22222222222'),
('Gordo', '10000000011', 'Gatinho resgatado da chuva, aguardando adoção', 'M', 2, '2023-09-01', '2023-10-18', 'P', false, false, '22222222222'),

-- Usuário '10000000005' (Gabriel Santos)
('Duque', '10000000005', 'Dálmata puro, super ativo', 'M', 1, '2021-04-14', '2023-10-19', 'G', true, true, '11111111111'),

-- Usuário '10000000099' (Gloria Groove) - Pet temporário
('Princesa', '10000000099', 'Gatinha persa resgatada de maus tratos, pronta para adoção', 'F', 2, '2019-12-10', '2023-10-20', 'M', false, true, '22222222222');


-- =========================================================================
-- 3. TABELA: adocao
-- Relações de adoções definitivas e temporárias (lares temporários)
-- =========================================================================
INSERT INTO adocao (data_inicio, cpf_adotante, pet_nome, pet_dono, data_fim, data_solicitacao, status, is_permanente) VALUES
-- Adoção Temporária (Lar temporário): Bidu foi abrigado por Rafael Ribeiro (cpf ...015), já concluído (data_fim preenchida)
('2023-03-01', '10000000015', 'Bidu', '44444444444', '2023-06-01', '2023-02-20', 'CONCLUIDA', false),

-- Adoção Permanente: Nina foi adotada em definitivo por Julia Marques (cpf ...030)
('2023-04-10', '10000000030', 'Nina', '44444444444', NULL, '2023-04-01', 'CONCLUIDA', true),

-- Adoção Temporária em andamento: Thor está de lar temporário com Renan Garcia (cpf ...050)
('2023-06-01', '10000000050', 'Thor', '10000000001', '2023-12-01', '2023-05-25', 'EM_ANDAMENTO', false),

-- Adoção Permanente pendente (em andamento): Mingau sendo adotado por Ludmilla (cpf ...085)
('2023-09-01', '10000000085', 'Mingau', '10000000042', NULL, '2023-08-20', 'EM_ANDAMENTO', true);


-- =========================================================================
-- 4. TABELA: saude_pet (Para dar mais realismo, adicionamos algumas condições)
-- =========================================================================
INSERT INTO saude_pet (pet_nome, pet_dono, usa_medicamento, desc_medicamento, condicao_especial, desc_cuidados) VALUES
('Garfield', '10000000042', false, NULL, true, 'Está com sobrepeso, requer dieta restritiva controlada'),
('Thor', '10000000001', true, 'Anti-inflamatório para articulações', false, NULL),
('Rex', '33333333333', false, NULL, true, 'Alergia a pólen, evitar mato alto');
