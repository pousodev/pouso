INSERT INTO adocao (
    data_inicio,
    cpf_adotante,
    pet_nome,
    pet_dono,
    data_fim,
    data_solicitacao,
    status,
    is_permanente
) VALUES (
    CURRENT_DATE,
    '90000000002',
    'Bob',
    '33333333642',
    NULL,
    CURRENT_DATE,
    'SOLICITADA',
    true
) ON CONFLICT (data_inicio, cpf_adotante, pet_nome, pet_dono) DO NOTHING;
