# lav.cametá — GUIA DE DESIGN E PADRÃO VISUAL DO PROJETO

> Documento de referência para definir a identidade visual, a estrutura das interfaces e os padrões de componentes do aplicativo lav.cametá.
>
> Campos marcados como **[A DEFINIR]** dependem de informações do responsável pelo projeto e não devem ser inventados.

## 1. IDENTIFICAÇÃO DO PROJETO

- Nome do projeto: lav.cametá
- Descrição: Aplicativo de lavanderia self-service em Cametá (PA).
- Objetivo principal: Permitir que o cliente escolha uma máquina disponível, faça o pagamento pelo aplicativo e use a máquina para lavar e/ou secar suas roupas.
- Público-alvo: Pessoas que não têm lavanderia (ou máquina de lavar) em casa e não têm tempo para lavar roupa.
- Localização ou região de atuação: Cametá – Pará
- Tipo de aplicação: ( ) Site ( ) Sistema Web ( ) SaaS (x) Aplicativo ( ) Outro
- Tecnologias utilizadas: Conforme já definido no projeto existente.
- Frameworks e bibliotecas: Conforme já definido no projeto existente.
- Responsável pelo projeto: Joel M. Miranda (desenvolvedor)

## 2. IDENTIDADE VISUAL

- Nome da marca: lav.cametá (sempre em minúsculas, com ponto entre "lav" e "cametá")
- Slogan: Não possui. Na logo, o nome é acompanhado apenas do descritor "LAVANDERIA".
- Descrição da identidade: Moderna e clean, baseada em roxo, lilás, cinza e branco, transmitindo limpeza, cuidado e confiança. O símbolo é a porta redonda de uma máquina de lavar com uma onda de água e bolhas no interior. No nome, o ponto é substituído por uma gota d'água.
- Personalidade visual: (x) Moderna (x) Minimalista ( ) Corporativa ( ) Tecnológica (x) Acolhedora ( ) Outro
- Logo oficial: Canvas "Logotipo lav.cametá" — https://claude.ai/artifact/DJpgLqurhxYfkRF28FvDz5
- Variações da logo:
  - **Logo principal (horizontal):** símbolo à esquerda + nome + "LAVANDERIA". "lav" e símbolo em roxo (#45257A), "cametá" e "LAVANDERIA" em cinza (#5E5A6B), onda e gota em lilás (#9B7FE0). Uso padrão em fundo claro.
  - **Versão negativa:** símbolo e nome em branco, onda/gota em lilás-claro (#CDB8F5), sobre fundo roxo (#45257A).
  - **Versão vertical:** símbolo acima, nome abaixo, sobre fundo cinza-névoa (#F4F3F7) ou branco. Para formatos quadrados e tela de abertura (splash).
  - **Ícone:** apenas o símbolo, dentro de um círculo ou quadrado arredondado roxo (#45257A), com onda em lilás-claro (#CDB8F5). Para ícone do aplicativo, favicon e perfil de redes sociais.
  - **Só o nome:** "lav.cametá" com a gota, com ou sem "LAVANDERIA", em fundo claro ou escuro. Para cabeçalhos e espaços horizontais estreitos.
- Regras de utilização:
  - "lav" sempre em peso 700 e "cametá" em peso 300 (Outfit).
  - A gota substitui o ponto e fica sempre em lilás (#9B7FE0 no claro, #CDB8F5 no escuro).
  - "LAVANDERIA" em caixa alta, peso 400, com espaçamento entre letras amplo (~0,5em).
  - Área de proteção mínima ao redor da logo: a altura da letra "l".
  - Tamanho mínimo da logo horizontal: 120 px de largura em tela. Abaixo disso, usar o ícone.
- Elementos visuais que devem ser preservados: o símbolo da porta com onda, a gota no lugar do ponto, o contraste de peso entre "lav" e "cametá", a escrita em minúsculas.
- Elementos que devem ser evitados: distorcer ou rotacionar a logo, trocar a fonte, usar a logo sobre fotos sem contraste, adicionar sombras/contornos/efeitos 3D, trocar a gota por um ponto comum, escrever "Lav Cametá" ou "LAV.CAMETÁ", usar a logo nas cores azuis antigas.

## 3. PALETA DE CORES

| Aplicação | Nome da cor | Código HEX |
|---|---|---|
| Cor principal | Roxo | #45257A |
| Cor secundária | Lilás | #9B7FE0 |
| Cor de destaque | Lilás-claro (sobre fundos roxos) | #CDB8F5 |
| Cor de fundo | Branco | #FFFFFF |
| Fundo de seções | Cinza-névoa | #F4F3F7 |
| Texto principal | Cinza-grafite | #2E2B36 |
| Texto secundário | Cinza | #5E5A6B |
| Bordas | Cinza-lilás claro | #E2DEEB |
| Sucesso | Verde | #1E7F50 |
| Alerta | Âmbar | #B26B00 |
| Erro | Vermelho | #C62828 |
| Informação | Roxo médio | #5B3A9E |

Regras adicionais:
- Contraste mínimo: 4,5:1 para textos (WCAG AA); 3:1 para textos ≥ 24 px e elementos gráficos. Todas as cores de texto acima atendem AA sobre branco e sobre #F4F3F7. O lilás (#9B7FE0) tem contraste de ~3,3:1 sobre branco: usar só em ícones, ilustrações, bordas de foco e textos ≥ 24 px, nunca em texto comum. O lilás-claro (#CDB8F5) só deve ser usado sobre fundo roxo, nunca sobre branco.
- Utilização de gradientes: Não. Usar cores sólidas.
- Modo escuro: ( ) Sim ( ) Não — [A DEFINIR]. Se adotado: fundo #2E1A4F, superfícies (cartões) #45257A, texto #FFFFFF, texto secundário #CDB8F5, destaque #CDB8F5 (mesma lógica da versão negativa da logo).
- Outras regras: Branco e cinza dominam as telas; o roxo é a cor das ações principais e da marca; o lilás é usado com moderação, como destaque gráfico. Evitar outras cores fora desta paleta.

## 4. TIPOGRAFIA

- Fonte principal: Outfit
- Fonte secundária: Outfit (família única)
- Fonte para títulos: Outfit
- Peso dos títulos: 700 (peso 300 reservado à logo)
- Peso dos textos: 400 (ênfase em 600)
- Tamanho dos títulos: H1 28 px / H2 22 px / H3 18 px no aplicativo (telas maiores: H1 40 px / H2 32 px / H3 24 px)
- Tamanho dos textos: 16 px
- Tamanho dos textos auxiliares: 14 px (mínimo 12 px)
- Altura das linhas: títulos 1,2 / textos 1,5
- Fontes locais ou externas: Outfit (Google Fonts, licença aberta). No aplicativo, preferir a fonte embutida localmente; na web: `https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap`. Fallback: `system-ui, sans-serif`.
- Regras adicionais: Rótulos curtos em caixa alta (ex.: "LAVANDERIA") podem usar espaçamento entre letras amplo, como na logo. Não usar caixa alta em parágrafos. Tempo restante de ciclo e valores podem usar 600–700 em tamanho maior (ex.: 40 px) com números tabulares.

## 5. ESTILO DOS COMPONENTES

**Botões**
- Estilo: Sólido (primário) e contornado (secundário); cantos arredondados, sem gradiente.
- Bordas: raio 12 px; botão secundário com borda 2 px #45257A.
- Altura: 52 px no aplicativo (mínimo 44 px para toque).
- Cores: Primário fundo #45257A, texto #FFFFFF. Secundário fundo transparente, texto #45257A. Em fundo roxo: fundo #FFFFFF, texto #45257A.
- Efeitos ao tocar / passar o mouse: escurecer o fundo (#341B5E) em 150 ms.
- Estado de carregamento: ícone giratório no lugar do ícone/texto, largura do botão mantida, botão desabilitado.
- Estado desabilitado: fundo #E2DEEB, texto #5E5A6B.

**Cartões**
- Estilo: Fundo branco com borda 1 px #E2DEEB.
- Arredondamento: 16 px.
- Sombras: sutil — `0 2px 8px rgba(69, 37, 122, 0.08)`.
- Espaçamento interno: 16 px no celular (24 px em telas maiores).
- Comportamento responsivo: lista de 1 coluna no celular; 2 colunas no tablet.
- Cartão de máquina: ícone da máquina, nome/número, tipo (lavar ou secar), status com ícone + texto (Disponível em #1E7F50, Em uso em #B26B00, Indisponível em #5E5A6B) e, se em uso, tempo restante.

**Campos e formulários**
- Estilo dos campos: fundo branco, rótulo visível acima do campo.
- Altura: 52 px.
- Bordas: 1 px #E2DEEB, raio 12 px.
- Foco: borda 2 px #9B7FE0 + anel `0 0 0 3px rgba(155, 127, 224, 0.3)`.
- Mensagens de erro: texto 14 px #C62828 abaixo do campo, borda do campo em #C62828, com ícone.
- Mensagens de sucesso: texto 14 px #1E7F50, com ícone.

**Ícones**
- Biblioteca: Lucide (traço, combina com o estilo do símbolo da logo).
- Estilo: contorno (stroke), traço 2 px, pontas arredondadas.
- Tamanho padrão: 24 px (20 px em textos auxiliares).
- Regras de utilização: cor herdada do texto (ou #45257A em ícones de ação); não usar emojis como ícones; ícones sozinhos precisam de rótulo acessível.

**Outros componentes**
- Modais / bottom sheets: fundo branco, raio 16 px (no topo, em bottom sheets), overlay `rgba(46, 26, 79, 0.5)`, fechar com X, gesto de arrastar ou toque fora.
- Tabelas: evitar no aplicativo; usar listas de cartões. Se necessário: cabeçalho em #F4F3F7, linhas separadas por 1 px #E2DEEB.
- Alertas: fundo claro da cor do estado, ícone + texto, raio 12 px.
- Dropdowns / seletores: mesmo estilo dos campos; lista em bottom sheet no celular.
- Paginação: rolagem contínua com carregamento ao final da lista.
- Abas: texto 16 px, aba ativa em #45257A com sublinhado 3 px #9B7FE0.
- Tooltips: fundo #2E2B36, texto branco 14 px, raio 8 px.
- Skeleton Loading: blocos #ECE9F2 com animação suave de brilho.
- Indicador de ciclo: anel circular (como a porta da logo) com progresso em #9B7FE0 sobre trilho #E2DEEB e tempo restante no centro.

## 6. ESTRUTURA DE NAVEGAÇÃO

**Desktop**
- Logo: versão "só o nome" ou horizontal, à esquerda, link para o início.
- Menu principal: mesmos itens da barra inferior do aplicativo (ver Celular), em menu horizontal — apenas se houver versão web [A DEFINIR].
- Links: texto 16 px #2E2B36; item ativo em #45257A.
- Botões de destaque: botão primário "Escolher máquina" à direita.
- Posição do menu: cabeçalho horizontal no topo, fundo branco.
- Comportamento ao rolar a página: cabeçalho fixo com borda inferior #E2DEEB ao rolar.

**Tablet**
- Tipo de navegação: barra inferior (como no celular) ou barra lateral compacta com ícones e rótulos.
- Organização dos menus: mesmos itens do celular.

**Celular**
- Tipo de menu: barra de navegação inferior fixa, com ícone + rótulo.
- Menu compacto: proposta inicial de itens — Início, Máquinas, Histórico, Perfil [confirmar].
- Atalhos principais: botão primário "Escolher máquina" na tela inicial; cartão do ciclo em andamento sempre visível no topo do Início.
- Comportamento de abertura e fechamento: telas internas (máquina, pagamento) abrem com botão "voltar" no cabeçalho; a barra inferior some no fluxo de pagamento para evitar saídas acidentais.

## 7. ESTRUTURA DAS PÁGINAS

Proposta inicial do fluxo principal (escolher máquina → pagar → lavar/secar). Ajustar conforme as telas que já existem no projeto.

**Página: Login / Cadastro**

- Objetivo: Identificar o cliente para pagamento e histórico.
- Público: Novos clientes e clientes que voltam.
- Título principal: [A DEFINIR]
- Descrição: Logo vertical no topo e formulário de acesso.
- Seções: Logo, formulário, link para cadastro/recuperar senha.
- Componentes: Campos, botão primário, botão secundário.
- Botões e ações: Entrar, Criar conta, Esqueci minha senha.
- Imagens e ilustrações: Logo vertical.
- Informações que devem aparecer: Campos de acesso [A DEFINIR: e-mail, telefone ou outro].
- Comportamentos e interações: Validação ao sair do campo.
- Estados de carregamento: Botão com ícone giratório.
- Estados vazios: Não se aplica.
- Estados de erro: Mensagem abaixo do campo; erro geral em alerta.
- Regras específicas: Seguir LGPD no cadastro.

**Página: Início**

- Objetivo: Levar o cliente rapidamente a escolher uma máquina e acompanhar o que está em andamento.
- Público: Cliente logado.
- Título principal: [A DEFINIR]
- Descrição: Resumo do ciclo em andamento (se houver) e atalho para escolher máquina.
- Seções: Cabeçalho com logo "só o nome", cartão de ciclo em andamento, botão "Escolher máquina", últimas lavagens.
- Componentes: Cartões, indicador de ciclo, botão primário.
- Botões e ações: Escolher máquina, Ver ciclo.
- Imagens e ilustrações: Ilustração em traço no estado vazio.
- Informações que devem aparecer: Ciclo atual, máquina em uso e tempo restante.
- Comportamentos e interações: Atualização automática do tempo restante.
- Estados de carregamento: Skeleton dos cartões.
- Estados vazios: Mensagem curta convidando a fazer a primeira lavagem.
- Estados de erro: Alerta com opção "Tentar novamente".
- Regras específicas: —

**Página: Máquinas**

- Objetivo: Mostrar as máquinas e o status de cada uma para o cliente escolher.
- Público: Cliente logado.
- Título principal: Máquinas
- Descrição: Lista de máquinas de lavar e de secar.
- Seções: Abas ou filtro "Lavar" / "Secar", lista de cartões de máquina.
- Componentes: Abas, cartões de máquina, indicadores de status.
- Botões e ações: Tocar no cartão de uma máquina disponível.
- Imagens e ilustrações: Ícones de máquina (Lucide ou próprios no mesmo traço).
- Informações que devem aparecer: Número/nome da máquina, tipo, status, tempo restante quando em uso, valor do ciclo [A DEFINIR].
- Comportamentos e interações: Status atualizado em tempo real; máquinas em uso ou indisponíveis não são selecionáveis.
- Estados de carregamento: Skeleton da lista.
- Estados vazios: "Nenhuma máquina disponível agora", com tempo da próxima liberação se possível.
- Estados de erro: Alerta com "Tentar novamente".
- Regras específicas: Status sempre com ícone + texto, nunca só cor.

**Página: Detalhe da máquina**

- Objetivo: Confirmar a máquina e escolher o tipo de ciclo.
- Público: Cliente logado.
- Título principal: Nome/número da máquina.
- Descrição: Opções do ciclo e resumo do valor.
- Seções: Dados da máquina, opções de ciclo, resumo, botão de continuar.
- Componentes: Cartões selecionáveis, resumo de valor, botão primário fixo no rodapé.
- Botões e ações: Selecionar ciclo, Continuar para pagamento.
- Imagens e ilustrações: Ícone da máquina.
- Informações que devem aparecer: Opções de ciclo e duração [A DEFINIR], valor [A DEFINIR].
- Comportamentos e interações: Botão "Continuar" habilitado só após escolher o ciclo.
- Estados de carregamento: Skeleton.
- Estados vazios: Não se aplica.
- Estados de erro: Se a máquina ficar ocupada, avisar e voltar para a lista.
- Regras específicas: Não inventar preços; usar [VALOR] em protótipos.

**Página: Pagamento**

- Objetivo: Concluir o pagamento do ciclo escolhido.
- Público: Cliente logado.
- Título principal: Pagamento
- Descrição: Resumo do pedido e forma de pagamento.
- Seções: Resumo (máquina, ciclo, valor), formas de pagamento, botão de pagar.
- Componentes: Cartão de resumo, opções selecionáveis, botão primário.
- Botões e ações: Escolher forma de pagamento, Pagar.
- Imagens e ilustrações: Ícones das formas de pagamento.
- Informações que devem aparecer: Valor total e formas de pagamento aceitas [A DEFINIR].
- Comportamentos e interações: Barra inferior oculta; confirmação clara após pagar.
- Estados de carregamento: Botão "Pagar" com ícone giratório; impedir toque duplo.
- Estados vazios: Não se aplica.
- Estados de erro: Mensagem clara de falha com opção de tentar novamente ou trocar a forma de pagamento.
- Regras específicas: Não exibir dados completos de pagamento; seguir LGPD.

**Página: Ciclo em andamento**

- Objetivo: Liberar a máquina e acompanhar a lavagem/secagem.
- Público: Cliente que pagou.
- Título principal: Lavando / Secando
- Descrição: Indicador circular com tempo restante.
- Seções: Indicador de ciclo, dados da máquina, instruções de uso.
- Componentes: Indicador de ciclo, cartão de informações, alertas.
- Botões e ações: [A DEFINIR — ex.: iniciar máquina, se o início for pelo app].
- Imagens e ilustrações: Anel do indicador inspirado na porta da logo.
- Informações que devem aparecer: Máquina, tipo de ciclo, tempo restante, horário previsto de término.
- Comportamentos e interações: Atualização em tempo real; notificação quando o ciclo terminar.
- Estados de carregamento: Skeleton do indicador.
- Estados vazios: Não se aplica.
- Estados de erro: Aviso se a máquina não responder, com contato de suporte [A DEFINIR].
- Regras específicas: Respeitar `prefers-reduced-motion` na animação do anel.

**Página: Histórico**

- Objetivo: Mostrar lavagens e pagamentos anteriores.
- Público: Cliente logado.
- Título principal: Histórico
- Descrição: Lista de ciclos anteriores.
- Seções: Lista agrupada por data.
- Componentes: Cartões de histórico.
- Botões e ações: Tocar para ver detalhes/comprovante.
- Imagens e ilustrações: Ilustração no estado vazio.
- Informações que devem aparecer: Data, máquina, ciclo, valor, status do pagamento.
- Comportamentos e interações: Rolagem contínua.
- Estados de carregamento: Skeleton.
- Estados vazios: "Você ainda não fez nenhuma lavagem."
- Estados de erro: Alerta com "Tentar novamente".
- Regras específicas: —

**Página: Perfil**

- Objetivo: Gerenciar dados da conta.
- Público: Cliente logado.
- Título principal: Perfil
- Descrição: Dados do cliente e configurações.
- Seções: Dados pessoais, formas de pagamento salvas [A DEFINIR], notificações, ajuda, sair.
- Componentes: Lista de opções, campos, botão secundário.
- Botões e ações: Editar dados, Sair.
- Imagens e ilustrações: —
- Informações que devem aparecer: Nome e contato do cliente.
- Comportamentos e interações: Confirmação antes de sair da conta.
- Estados de carregamento: Skeleton.
- Estados vazios: Não se aplica.
- Estados de erro: Mensagem abaixo do campo.
- Regras específicas: Seguir LGPD.

## 8. LAYOUT E ORGANIZAÇÃO

- Largura máxima do conteúdo: 600 px no tablet (conteúdo centralizado); 1200 px em uma eventual versão web.
- Sistema de grid: 4 colunas no celular (margens 16 px, espaçamento 16 px); 8 colunas no tablet; 12 colunas na web.
- Espaçamento padrão: escala de 8 px (4, 8, 16, 24, 32, 48, 64)
- Margens: 16 px nas laterais (celular), 32 px (tablet), 80 px (web).
- Alinhamento: textos alinhados à esquerda; centralizado apenas em telas de status (ciclo, sucesso de pagamento, estados vazios).
- Largura do menu lateral: não se aplica no celular; 80 px (barra compacta) no tablet, se usada.
- Altura do cabeçalho: 56 px
- Altura do rodapé: barra de navegação inferior de 64 px (+ área segura do aparelho).
- Organização das seções: espaçamento vertical de 24 px entre blocos; fundo da tela #F4F3F7 com cartões brancos.
- Padrão de containers: tela com margens de 16 px; botão de ação principal fixo acima da barra inferior quando necessário.
- Regras de posicionamento: usar flex/grid com `gap`; respeitar áreas seguras (notch e barra de gestos); evitar posicionamento absoluto para layout.

## 9. RESPONSIVIDADE

**Celular**
- Largura de referência: 390 px (testar a partir de 360 px)
- Organização dos componentes: uma coluna, empilhados.
- Quantidade de colunas: 1
- Comportamento dos botões: largura total nas ações principais; botão principal fixo no rodapé das telas de detalhe e pagamento.
- Navegação: barra inferior (ver seção 6).

**Tablet**
- Largura de referência: 768 px
- Organização dos componentes: conteúdo centralizado; lista de máquinas em 2 colunas.
- Quantidade de colunas: 2

**Desktop**
- Largura de referência: 1440 px (apenas se houver versão web)
- Organização dos componentes: grid de 12 colunas; máquinas em 3 colunas.
- Quantidade de colunas: 3 (cartões) / 12 (grid)

Regras gerais:
- Evitar rolagem horizontal.
- Preservar proporções das imagens.
- Adaptar tabelas e formulários.
- Garantir áreas de toque confortáveis (mínimo 44 × 44 px).
- Testar em diferentes resoluções.

## 10. ACESSIBILIDADE

- HTML semântico: na web, `header`, `nav`, `main`, `section`, `footer`; no aplicativo, usar os componentes nativos com papéis corretos (botão, link, título, campo).
- Navegação por teclado: todos os elementos interativos acessíveis por Tab / teclado externo, na ordem visual.
- Indicador de foco: anel 3 px `rgba(155, 127, 224, 0.4)` + borda #9B7FE0; nunca remover o foco.
- Textos alternativos: obrigatórios em imagens informativas; logo com rótulo "lav.cametá"; imagens decorativas ocultas para leitores de tela.
- Contraste: mínimo AA (4,5:1 texto, 3:1 texto grande e ícones).
- Rótulos dos formulários: rótulo visível ligado ao campo; placeholder não substitui rótulo.
- Mensagens acessíveis: erros associados ao campo; avisos de status (pagamento aprovado, ciclo terminado) anunciados pelo leitor de tela.
- Compatibilidade com leitores de tela: VoiceOver e TalkBack; ícones sozinhos com rótulo; elementos decorativos ocultos.
- Outras exigências: não depender só da cor para comunicar estado (sempre ícone ou texto junto); suportar aumento do tamanho de fonte do sistema; idioma pt-BR.

## 11. ANIMAÇÕES E INTERAÇÕES

- Estilo das animações: suaves e discretas, lembrando fluidez da água.
- Transições: cor, sombra, opacidade e deslize entre telas.
- Duração: 150–250 ms, curva `ease-out`.
- Efeitos de hover / toque: escurecimento de cor em botões; leve redução de escala (0,98) ao tocar em cartões.
- Feedback visual: estados de foco, carregamento, sucesso e erro sempre visíveis.
- Loading: ícone giratório em #45257A.
- Skeleton: blocos #ECE9F2 com brilho suave.
- Microinterações: confirmação de pagamento com ícone de check e mensagem curta; progresso do anel do ciclo.
- Restrições de animação: respeitar a configuração de reduzir movimento do sistema; sem animações em loop contínuo (exceto o progresso do ciclo), parallax ou movimentos bruscos.

## 12. IMAGENS E RECURSOS VISUAIS

- Estilo das imagens: claras, iluminadas, com roupas limpas, tecidos e ambientes organizados; tons neutros que combinem com branco, cinza e roxo.
- Estilo das ilustrações: traço linear, mesmo estilo do símbolo (contorno roxo + preenchimento lilás), sem excesso de detalhes.
- Formato preferencial: WebP para fotos, SVG para logo, ícones e ilustrações.
- Proporções: 16:9 (banners), 4:3 ou 1:1 (cartões).
- Tratamento de imagens: cantos arredondados 16 px; sem filtros fortes.
- Utilização de vídeos: [A DEFINIR]
- Origem dos recursos: fotos próprias da lavanderia preferencialmente — [A DEFINIR]
- Regras de otimização: compressão, carregamento sob demanda, dimensões definidas para evitar saltos de layout.
- Elementos que não devem ser utilizados: gradientes chamativos, emojis como ícones, imagens genéricas sem relação com o serviço, cores fora da paleta (incluindo o azul da versão anterior da logo).

## 13. PRIVACIDADE E EXIBIÇÃO DE DADOS

- Informações públicas: Status e disponibilidade das máquinas; endereço e horário da lavanderia [A DEFINIR].
- Informações privadas: Dados pessoais do cliente, histórico de lavagens e pagamentos.
- Dados que exigem autorização: Dados de pagamento, localização (se usada) e notificações.
- Regras de exibição: dados pessoais e de pagamento visíveis apenas para o próprio cliente; dados de cartão sempre mascarados.
- Dados fictícios para demonstração: usar placeholders claramente marcados (ex.: [NOME], [TELEFONE], [VALOR]); não inventar preços ou dados reais.
- Restrições específicas: seguir a LGPD.

## 14. TOM DE VOZ E TEXTOS

- Personalidade da comunicação: próxima, simples e confiável — como a marca: limpa e direta.
- Linguagem: português do Brasil, frases curtas.
- Formalidade: informal respeitosa (tratar o cliente por "você").
- Termos preferenciais: [A DEFINIR]
- Termos que devem ser evitados: jargões técnicos, termos em inglês sem necessidade.
- Exemplos de títulos: [A DEFINIR]
- Exemplos de mensagens: [A DEFINIR]
- Padrão de notificações: curtas, com ícone, dizendo o que aconteceu e o próximo passo (ex.: ciclo terminou → retirar as roupas da máquina [número]).

## 15. RODAPÉ

- Logo: no aplicativo, não há rodapé (a barra de navegação inferior ocupa essa posição). Em uma eventual versão web: versão negativa sobre fundo #45257A.
- Informações institucionais: [A DEFINIR]
- Links: [A DEFINIR]
- Contatos: [A DEFINIR]
- Redes sociais: [A DEFINIR] (ícones Lucide em branco)
- Políticas: Termos de uso e Política de privacidade [A DEFINIR] — acessíveis pelo Perfil no aplicativo.
- Créditos: [A DEFINIR]
- Elementos adicionais: texto em #FFFFFF; links e destaques em #CDB8F5.

## 16. REFERÊNCIAS VISUAIS

- Sites de referência: [A DEFINIR]
- Interfaces de inspiração: [A DEFINIR]
- Prints ou imagens: Canvas "Logotipo lav.cametá" — https://claude.ai/artifact/DJpgLqurhxYfkRF28FvDz5
- Componentes que devem servir de referência: as variações da logo do canvas acima.
- Elementos que devem ser reproduzidos: símbolo da porta com onda, gota no lugar do ponto, paleta roxo + lilás + cinza + branco, tipografia Outfit.
- Elementos que não devem ser reproduzidos: a versão azul anterior da logo.

## 17. REGRAS DE IMPLEMENTAÇÃO

- Preservar a identidade visual definida neste documento.
- Reutilizar componentes para manter consistência entre páginas.
- Não criar estilos diferentes para componentes que possuem a mesma função.
- Não modificar a logo oficial.
- Não inventar cores, fontes ou elementos fora do padrão sem necessidade.
- Preservar as funcionalidades existentes.
- Priorizar responsividade e acessibilidade.
- Evitar dependências desnecessárias.
- Manter o código organizado e reutilizável.
- Respeitar a arquitetura e as tecnologias definidas para o projeto.
- Definir as cores e medidas como variáveis (tokens), por exemplo:

```css
:root {
  --cor-principal: #45257A;
  --cor-principal-escura: #341B5E;
  --cor-secundaria: #9B7FE0;
  --cor-destaque: #CDB8F5;
  --cor-fundo: #FFFFFF;
  --cor-fundo-secao: #F4F3F7;
  --cor-texto: #2E2B36;
  --cor-texto-secundario: #5E5A6B;
  --cor-borda: #E2DEEB;
  --cor-skeleton: #ECE9F2;
  --cor-sucesso: #1E7F50;
  --cor-alerta: #B26B00;
  --cor-erro: #C62828;
  --cor-info: #5B3A9E;
  --fonte: 'Outfit', system-ui, sans-serif;
  --raio-pequeno: 8px;
  --raio: 12px;
  --raio-grande: 16px;
  --sombra: 0 2px 8px rgba(69, 37, 122, 0.08);
}
```

## 18. CRITÉRIOS DE APROVAÇÃO

- [ ] Identidade visual aplicada corretamente.
- [ ] Logo preservada.
- [ ] Paleta de cores respeitada.
- [ ] Tipografia consistente.
- [ ] Componentes padronizados.
- [ ] Navegação funcional.
- [ ] Layout responsivo.
- [ ] Acessibilidade verificada.
- [ ] Formulários e interações funcionando.
- [ ] Imagens otimizadas.
- [ ] Consistência visual entre páginas.
- [ ] Compatibilidade com as tecnologias do projeto.

---

## 19. INSTRUÇÃO FINAL PARA O AGENTE DE IA

Este documento representa a especificação visual oficial do projeto.

Antes de implementar qualquer interface:

1. Analise integralmente este guia.
2. Examine a arquitetura e os arquivos existentes.
3. Identifique os componentes que podem ser reutilizados.
4. Implemente o layout seguindo as definições estabelecidas.
5. Mantenha consistência visual em todas as páginas.
6. Não invente elementos visuais que contrariem as especificações.
7. Teste a interface em dispositivos móveis e desktops.
8. Confira todos os critérios de aprovação.

Em caso de informação não preenchida, utilize uma solução coerente com a identidade visual existente, sem inventar características institucionais.

**Resultado esperado:** uma interface consistente, organizada, responsiva e fiel à identidade visual definida para o projeto.
