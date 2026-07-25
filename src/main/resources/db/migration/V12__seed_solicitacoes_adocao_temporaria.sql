INSERT INTO adocao (
    data_inicio,
    cpf_adotante,
    pet_nome,
    pet_dono,
    data_fim,
    data_solicitacao,
    status,
    is_permanente
) VALUES
('2026-07-24', '10000000002', 'Bidu', '44444444444', '2026-08-24', '2026-07-24', 'SOLICITADA', false),
('2026-07-25', '10000000004', 'Thor', '10000000001', NULL, '2026-07-24', 'SOLICITADA', false),
('2026-07-26', '10000000006', 'Mingau', '10000000042', NULL, '2026-07-24', 'SOLICITADA', false),
('2026-07-27', '10000000008', 'Mel', '10000000055', NULL, '2026-07-24', 'SOLICITADA', false),
('2026-07-28', '10000000013', 'Fred', '55555555555', NULL, '2026-07-24', 'SOLICITADA', false),
('2026-07-29', '10000000014', 'Simão', '10000000072', NULL, '2026-07-24', 'SOLICITADA', false),
('2026-07-30', '10000000016', 'Gordo', '10000000011', NULL, '2026-07-24', 'SOLICITADA', false),
('2026-07-31', '10000000018', 'Princesa', '10000000099', NULL, '2026-07-24', 'SOLICITADA', false)
ON CONFLICT (data_inicio, cpf_adotante, pet_nome, pet_dono) DO NOTHING;
