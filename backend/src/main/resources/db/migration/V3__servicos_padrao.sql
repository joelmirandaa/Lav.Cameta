-- RN01: preços padrão confirmados no documento de requisitos, configuráveis pelo administrador.
-- Durações: o PDF não informa valores; estes são iniciais e DEVEM ser confirmados com o
-- proprietário/fabricante (alteráveis na gestão de preços do administrador).
INSERT INTO servicos (nome, descricao, preco, duracao_min) VALUES
    ('LAVAR', 'Lavagem completa', 12.00, 40),
    ('SECAR', 'Secagem completa', 10.00, 40),
    ('LAVAR_SECAR', 'Lavagem seguida de secagem', 18.00, 80);
