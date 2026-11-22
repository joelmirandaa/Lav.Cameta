# lav.cametá — GUIA DE DESIGN E PADRÃO VISUAL DO PROJETO

> Documento de referência para definir a identidade visual, a estrutura das interfaces e os padrões de componentes do projeto lav.cametá.
>
> Campos marcados como **[A DEFINIR]** dependem de informações do responsável pelo projeto e não devem ser inventados.

## 1. IDENTIFICAÇÃO DO PROJETO

- Nome do projeto: lav.cametá
- Descrição: Lavanderia localizada em Cametá (PA).
- Objetivo principal: [A DEFINIR]
- Público-alvo: [A DEFINIR]
- Localização ou região de atuação: Cametá – Pará
- Tipo de aplicação: ( ) Site ( ) Sistema Web ( ) SaaS ( ) Aplicativo ( ) Outro — [A DEFINIR]
- Tecnologias utilizadas: [A DEFINIR]
- Frameworks e bibliotecas: [A DEFINIR]
- Responsável pelo projeto: [A DEFINIR]

## 2. IDENTIDADE VISUAL

- Nome da marca: lav.cametá (sempre em minúsculas, com ponto entre "lav" e "cametá")
- Slogan: [A DEFINIR] — descritor atual: "LAVANDERIA"
- Descrição da identidade: Moderna e clean, baseada em azul-marinho e azul-água, transmitindo limpeza, frescor e confiança. O símbolo é a porta redonda de uma máquina de lavar com uma onda de água e bolhas no interior. No nome, o ponto é substituído por uma gota d'água.
- Personalidade visual: (x) Moderna (x) Minimalista ( ) Corporativa ( ) Tecnológica (x) Acolhedora ( ) Outro
- Logo oficial: Canvas "Logotipo lav.cametá" — https://claude.ai/artifact/DJpgLqurhxYfkRF28FvDz5
- Variações da logo:
  - **Logo principal (horizontal):** símbolo à esquerda + nome + "LAVANDERIA". Uso padrão em fundo claro.
  - **Versão negativa:** símbolo e nome em branco, onda/gota em azul-água claro (#6EC6F5), sobre fundo azul-marinho.
  - **Versão vertical:** símbolo acima, nome abaixo. Para formatos quadrados.
  - **Ícone:** apenas o símbolo, dentro de um círculo ou quadrado arredondado azul-marinho. Para perfil de redes sociais, favicon e ícone de app.
  - **Só o nome:** "lav.cametá" com a gota, com ou sem "LAVANDERIA", em fundo claro ou escuro. Para cabeçalhos e espaços horizontais estreitos.
- Regras de utilização:
  - "lav" sempre em peso 700 e "cametá" em peso 300 (Outfit).
  - A gota substitui o ponto e fica sempre em azul (#1F6FD1 no claro, #6EC6F5 no escuro).
  - "LAVANDERIA" em caixa alta, peso 400, com espaçamento entre letras amplo (~0,5em).
  - Área de proteção mínima ao redor da logo: a altura da letra "l".
  - Tamanho mínimo da logo horizontal: 120 px de largura em tela. Abaixo disso, usar o ícone.
- Elementos visuais que devem ser preservados: o símbolo da porta com onda, a gota no lugar do ponto, o contraste de peso entre "lav" e "cametá", a escrita em minúsculas.
- Elementos que devem ser evitados: distorcer ou rotacionar a logo, trocar a fonte, usar a logo sobre fotos sem contraste, adicionar sombras/contornos/efeitos 3D, trocar a gota por um ponto comum, escrever "Lav Cametá" ou "LAV.CAMETÁ".

## 3. PALETA DE CORES

| Aplicação | Nome da cor | Código HEX |
|---|---|---|
| Cor principal | Azul-marinho | #0B2A4A |
| Cor secundária | Azul-água | #1F6FD1 |
| Cor de destaque | Azul-claro (sobre fundos escuros) | #6EC6F5 |
| Cor de fundo | Branco | #FFFFFF |
| Fundo de seções | Azul-névoa | #F3F8FD |
| Texto principal | Azul-marinho | #0B2A4A |
| Texto secundário | Cinza-azulado | #4A5D73 |
| Bordas | Azul-cinza claro | #D6E3F0 |
| Sucesso | Verde | #1E7F50 |
| Alerta | Âmbar | #B26B00 |
| Erro | Vermelho | #C62828 |
| Informação | Azul-água | #1F6FD1 |

Regras adicionais:
- Contraste mínimo: 4,5:1 para textos (WCAG AA); 3:1 para textos ≥ 24 px e elementos gráficos. Todas as combinações de texto acima sobre branco atendem AA; #6EC6F5 só deve ser usado sobre fundo azul-marinho, nunca sobre branco.
- Utilização de gradientes: Não. Usar cores sólidas.
- Modo escuro: ( ) Sim ( ) Não — [A DEFINIR]. Se adotado: fundo #0B2A4A, texto #FFFFFF, destaque #6EC6F5 (mesma lógica da versão negativa da logo).
- Outras regras: O azul-marinho domina; o azul-água é usado com moderação (ações, links, destaques). Evitar outras cores fora desta paleta.

## 4. TIPOGRAFIA

- Fonte principal: Outfit
- Fonte secundária: Outfit (família única)
- Fonte para títulos: Outfit
- Peso dos títulos: 700 (peso 300 reservado à logo)
- Peso dos textos: 400 (ênfase em 600)
- Tamanho dos títulos: H1 40 px / H2 32 px / H3 24 px (celular: H1 32 px / H2 26 px / H3 20 px)
- Tamanho dos textos: 16 px
- Tamanho dos textos auxiliares: 14 px (mínimo 12 px)
- Altura das linhas: títulos 1,2 / textos 1,5
- Fontes locais ou externas: Externa — Google Fonts (`https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap`). Fallback: `system-ui, sans-serif`.
- Regras adicionais: Rótulos curtos em caixa alta (ex.: "LAVANDERIA") podem usar espaçamento entre letras amplo, como na logo. Não usar caixa alta em parágrafos.

## 5. ESTILO DOS COMPONENTES

**Botões**
- Estilo: Sólido (primário) e contornado (secundário); cantos arredondados, sem gradiente.
- Bordas: raio 12 px; botão secundário com borda 2 px #0B2A4A.
- Altura: 48 px (mínimo 44 px para toque).
- Cores: Primário fundo #1F6FD1, texto #FFFFFF. Secundário fundo transparente, texto #0B2A4A. Em fundo escuro: fundo #FFFFFF, texto #0B2A4A.
- Efeitos ao passar o mouse: escurecer o fundo (#185BB0) em 150 ms.
- Estado de carregamento: ícone giratório no lugar do ícone/texto, largura do botão mantida, botão desabilitado.
- Estado desabilitado: fundo #D6E3F0, texto #4A5D73, cursor not-allowed.

**Cartões**
- Estilo: Fundo branco com borda 1 px #D6E3F0.
- Arredondamento: 16 px.
- Sombras: sutil — `0 2px 8px rgba(11, 42, 74, 0.06)`.
- Espaçamento interno: 24 px (celular: 16 px).
- Comportamento responsivo: grid que passa de 3 → 2 → 1 coluna.

**Campos e formulários**
- Estilo dos campos: fundo branco, rótulo visível acima do campo.
- Altura: 48 px.
- Bordas: 1 px #D6E3F0, raio 12 px.
- Foco: borda 2 px #1F6FD1 + anel `0 0 0 3px rgba(31, 111, 209, 0.2)`.
- Mensagens de erro: texto 14 px #C62828 abaixo do campo, borda do campo em #C62828, com ícone.
- Mensagens de sucesso: texto 14 px #1E7F50, com ícone.

**Ícones**
- Biblioteca: Lucide (traço, combina com o estilo do símbolo da logo).
- Estilo: contorno (stroke), traço 2 px, pontas arredondadas.
- Tamanho padrão: 24 px (20 px em textos auxiliares).
- Regras de utilização: cor herdada do texto; não usar emojis como ícones; ícones sozinhos precisam de `aria-label`.

**Outros componentes**
- Modais: fundo branco, raio 16 px, overlay `rgba(11, 42, 74, 0.5)`, fechar com X, Esc e clique fora.
- Tabelas: cabeçalho em #F3F8FD, linhas separadas por 1 px #D6E3F0; no celular, rolagem horizontal dentro de uma caixa ou conversão em cartões.
- Alertas: fundo claro da cor do estado, ícone + texto, raio 12 px.
- Dropdowns: mesmo estilo dos campos; lista com raio 12 px e sombra sutil.
- Paginação: botões quadrados 40 px, ativo em #1F6FD1 com texto branco.
- Abas: texto 16 px, aba ativa com sublinhado 3 px #1F6FD1.
- Tooltips: fundo #0B2A4A, texto branco 14 px, raio 8 px.
- Skeleton Loading: blocos #E8F0F8 com animação suave de brilho.

## 6. ESTRUTURA DE NAVEGAÇÃO

**Desktop**
- Logo: versão "só o nome" ou horizontal, à esquerda, link para a página inicial.
- Menu principal: [A DEFINIR — depende das páginas]
- Links: texto 16 px #0B2A4A; hover e item ativo em #1F6FD1.
- Botões de destaque: um botão primário à direita (ex.: ação principal do site — [A DEFINIR]).
- Posição do menu: cabeçalho horizontal no topo, fundo branco.
- Comportamento ao rolar a página: cabeçalho fixo com borda inferior #D6E3F0 ao rolar.

**Tablet**
- Tipo de navegação: igual ao desktop se couber; senão, menu compacto.
- Organização dos menus: itens secundários agrupados no menu compacto.

**Celular**
- Tipo de menu: botão de menu (ícone) abrindo painel.
- Menu compacto: logo à esquerda, botão de menu à direita.
- Atalhos principais: botão de ação principal visível no painel e/ou fixo na parte inferior — [A DEFINIR].
- Comportamento de abertura e fechamento: painel desliza da direita, fecha com X, Esc, clique fora ou ao escolher um item.

## 7. ESTRUTURA DAS PÁGINAS

[A DEFINIR] — preencher para cada página do projeto:

**Página: [Nome da página]**

- Objetivo:
- Público:
- Título principal:
- Descrição:
- Seções:
- Componentes:
- Botões e ações:
- Imagens e ilustrações:
- Informações que devem aparecer:
- Comportamentos e interações:
- Estados de carregamento:
- Estados vazios:
- Estados de erro:
- Regras específicas:

Repetir esta estrutura para todas as páginas do projeto.

## 8. LAYOUT E ORGANIZAÇÃO

- Largura máxima do conteúdo: 1200 px
- Sistema de grid: 12 colunas, espaçamento entre colunas 24 px
- Espaçamento padrão: escala de 8 px (8, 16, 24, 32, 48, 64, 96)
- Margens: 80 px nas laterais (desktop), 32 px (tablet), 16 px (celular)
- Alinhamento: textos alinhados à esquerda; centralizado apenas em títulos de seção curtos e chamadas.
- Largura do menu lateral: não se aplica (sem menu lateral) — [A DEFINIR se for sistema web]
- Altura do cabeçalho: 72 px (celular: 64 px)
- Altura do rodapé: livre, conforme conteúdo
- Organização das seções: espaçamento vertical de 96 px entre seções (celular: 64 px); alternar fundo #FFFFFF e #F3F8FD.
- Padrão de containers: container central com largura máxima de 1200 px e margens laterais acima.
- Regras de posicionamento: usar flex/grid com `gap`; evitar posicionamento absoluto para layout.

## 9. RESPONSIVIDADE

**Celular**
- Largura de referência: 390 px
- Organização dos componentes: uma coluna, empilhados.
- Quantidade de colunas: 1
- Comportamento dos botões: largura total nas ações principais.
- Navegação: menu compacto (ver seção 6).

**Tablet**
- Largura de referência: 768 px
- Organização dos componentes: duas colunas quando possível.
- Quantidade de colunas: 2

**Desktop**
- Largura de referência: 1440 px
- Organização dos componentes: grid de 12 colunas; cartões em 3 colunas.
- Quantidade de colunas: 3 (cartões) / 12 (grid)

Regras gerais:
- Evitar rolagem horizontal.
- Preservar proporções das imagens.
- Adaptar tabelas e formulários.
- Garantir áreas de toque confortáveis (mínimo 44 × 44 px).
- Testar em diferentes resoluções.

## 10. ACESSIBILIDADE

- HTML semântico: `header`, `nav`, `main`, `section`, `footer`; um único `h1` por página; `button` e `a` reais.
- Navegação por teclado: todos os elementos interativos acessíveis por Tab, na ordem visual.
- Indicador de foco: anel 3 px `rgba(31, 111, 209, 0.4)` + borda #1F6FD1; nunca remover o foco.
- Textos alternativos: obrigatórios em imagens informativas; logo com `alt="lav.cametá"`; imagens decorativas com `alt=""`.
- Contraste: mínimo AA (4,5:1 texto, 3:1 texto grande e ícones).
- Rótulos dos formulários: `label` visível ligado ao campo; placeholder não substitui rótulo.
- Mensagens acessíveis: erros associados ao campo via `aria-describedby`; avisos com `role="status"`/`alert`.
- Compatibilidade com leitores de tela: ícones sozinhos com `aria-label`; SVGs decorativos com `aria-hidden="true"`.
- Outras exigências: não depender só da cor para comunicar estado (sempre ícone ou texto junto); idioma `lang="pt-BR"`.

## 11. ANIMAÇÕES E INTERAÇÕES

- Estilo das animações: suaves e discretas, lembrando fluidez da água.
- Transições: cor, sombra e opacidade.
- Duração: 150–250 ms, curva `ease-out`.
- Efeitos de hover: escurecimento de cor em botões/links; cartões clicáveis sobem 2 px com sombra levemente maior.
- Feedback visual: estados de foco, carregamento, sucesso e erro sempre visíveis.
- Loading: ícone giratório em #1F6FD1.
- Skeleton: blocos #E8F0F8 com brilho suave.
- Microinterações: confirmação de ação com ícone de check e mensagem curta.
- Restrições de animação: respeitar `prefers-reduced-motion`; sem animações em loop contínuo, parallax ou movimentos bruscos.

## 12. IMAGENS E RECURSOS VISUAIS

- Estilo das imagens: claras, iluminadas, com roupas limpas, tecidos e ambientes organizados; tons frios/neutros que combinem com a paleta.
- Estilo das ilustrações: traço linear, mesmo estilo do símbolo (contorno + preenchimento azul), sem excesso de detalhes.
- Formato preferencial: WebP para fotos, SVG para logo, ícones e ilustrações.
- Proporções: 16:9 (banners), 4:3 ou 1:1 (cartões).
- Tratamento de imagens: cantos arredondados 16 px; sem filtros fortes.
- Utilização de vídeos: [A DEFINIR]
- Origem dos recursos: fotos próprias da lavanderia preferencialmente — [A DEFINIR]
- Regras de otimização: compressão, `loading="lazy"`, dimensões definidas para evitar saltos de layout.
- Elementos que não devem ser utilizados: gradientes chamativos, emojis como ícones, imagens genéricas sem relação com o serviço, cores fora da paleta.

## 13. PRIVACIDADE E EXIBIÇÃO DE DADOS

- Informações públicas: [A DEFINIR] (ex.: endereço, horário, contatos)
- Informações privadas: [A DEFINIR] (ex.: dados de clientes e pedidos)
- Dados que exigem autorização: [A DEFINIR]
- Regras de exibição: dados pessoais de clientes nunca visíveis publicamente.
- Dados fictícios para demonstração: usar placeholders claramente marcados (ex.: [NOME], [TELEFONE]); não inventar preços ou dados reais.
- Restrições específicas: seguir a LGPD.

## 14. TOM DE VOZ E TEXTOS

- Personalidade da comunicação: próxima, simples e confiável — como a marca: limpa e direta.
- Linguagem: português do Brasil, frases curtas.
- Formalidade: informal respeitosa (tratar o cliente por "você").
- Termos preferenciais: [A DEFINIR]
- Termos que devem ser evitados: jargões técnicos, termos em inglês sem necessidade.
- Exemplos de títulos: [A DEFINIR]
- Exemplos de mensagens: [A DEFINIR]
- Padrão de notificações: curtas, com ícone, dizendo o que aconteceu e o próximo passo.

## 15. RODAPÉ

- Logo: versão negativa (fundo #0B2A4A).
- Informações institucionais: [A DEFINIR]
- Links: [A DEFINIR]
- Contatos: [A DEFINIR]
- Redes sociais: [A DEFINIR] (ícones Lucide em branco)
- Políticas: [A DEFINIR]
- Créditos: [A DEFINIR]
- Elementos adicionais: texto em #FFFFFF; links e destaques em #6EC6F5.

## 16. REFERÊNCIAS VISUAIS

- Sites de referência: [A DEFINIR]
- Interfaces de inspiração: [A DEFINIR]
- Prints ou imagens: Canvas "Logotipo lav.cametá" — https://claude.ai/artifact/DJpgLqurhxYfkRF28FvDz5
- Componentes que devem servir de referência: as variações da logo do canvas acima.
- Elementos que devem ser reproduzidos: símbolo da porta com onda, gota no lugar do ponto, paleta azul-marinho + azul-água, tipografia Outfit.
- Elementos que não devem ser reproduzidos: [A DEFINIR]

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
  --cor-principal: #0B2A4A;
  --cor-secundaria: #1F6FD1;
  --cor-destaque: #6EC6F5;
  --cor-fundo: #FFFFFF;
  --cor-fundo-secao: #F3F8FD;
  --cor-texto: #0B2A4A;
  --cor-texto-secundario: #4A5D73;
  --cor-borda: #D6E3F0;
  --cor-sucesso: #1E7F50;
  --cor-alerta: #B26B00;
  --cor-erro: #C62828;
  --cor-info: #1F6FD1;
  --fonte: 'Outfit', system-ui, sans-serif;
  --raio-pequeno: 8px;
  --raio: 12px;
  --raio-grande: 16px;
  --sombra: 0 2px 8px rgba(11, 42, 74, 0.06);
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
