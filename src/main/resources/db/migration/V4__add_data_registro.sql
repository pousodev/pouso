ALTER TABLE pessoa ADD COLUMN data_registro TIMESTAMP;

UPDATE pessoa SET data_registro = NOW() - (row_num * interval '3 days')
FROM (SELECT cpf, ROW_NUMBER() OVER (ORDER BY cpf) AS row_num FROM pessoa) sub
WHERE pessoa.cpf = sub.cpf;

ALTER TABLE pessoa ALTER COLUMN data_registro SET DEFAULT NOW();
