# STYLE.md — identidade e padrões de interface

**Status: identidade definitiva pendente de definição.** O PDF original descreve um protótipo denominado WashFlow, predominantemente roxo, com telas de cliente e administrador. Isso é referência visual existente, **não** aprovação da marca final Lavicameta.

## Decisões pendentes
- Logotipo, nomes visuais definitivos, cores oficiais e variantes.
- Tipografia, pesos e escalas; grade de espaçamentos; raios e sombras.
- Iconografia, fotografia, ilustrações e acessibilidade de contraste.
- Componentes: botões, cards, formulários, diálogo, modal, navbar, barras e mensagens.
- Responsividade, modo escuro, estados vazios, loading, erro, sucesso e offline.

## Padrões técnicos iniciais (propostos)
- Textos em pt-BR, moeda em BRL e datas localizadas.
- Centralizar textos em `res/values/strings.xml`, cores em `colors.xml`, aparência em `themes.xml`; dimensões em `dimens.xml` quando necessário.
- Não usar cores literais em Activities/Fragments; não duplicar estilos entre telas.
- Diferenciar estado disponível, em uso, manutenção e reserva com **texto**, não apenas cor.
- Uso de tamanho de toque acessível, contraste adequado e descrições para ícones.
- Separar layout de estado de negócio; usar componentes reutilizáveis quando houver repetição real.
- Antes de implementar telas completas, aprovar identidade visual e testar os sete protótipos referenciados.

## Telas do documento de origem
Cliente: T01 Login, T02 Escolha uma máquina, T03 Detalhe, T04 Pagamento. Admin: T05 Acesso, T06 Relatório Mês, T07 Relatório Ano. As demais telas estão previstas mas não prototipadas. A `MainActivity` incluída é apenas a tela-base técnica, **não** uma reprodução do protótipo.
