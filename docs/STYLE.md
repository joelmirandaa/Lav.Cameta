# STYLE.md — identidade e padrões de interface

**Status:** a especificação visual oficial é [`lav-cameta-guia-de-design.md`](lav-cameta-guia-de-design.md) (roxo, lilás, cinza e branco; Outfit). Em caso de divergência, o guia prevalece. Este arquivo descreve **como o guia foi aplicado** no app Android e na página de checkout sandbox do backend. A identidade azul anterior foi substituída em 2026-11-25 e não deve ser reutilizada.

## Onde ficam os tokens (Android)
| Guia | Recurso |
|---|---|
| Paleta (seção 3) | `res/values/colors.xml` — marca: `roxo` #45257A, `roxo_escuro` #341B5E (toque), `lilas` #9B7FE0; neutros: `branco`, `fundo_secao` #F4F3F7, `texto` #2E2B36, `texto_secundario` #5E5A6B, `borda` #E2DEEB; estados: `sucesso`, `alerta`, `erro`, `info` #5B3A9E e os fundos claros `*_fundo` |
| Tipografia Outfit 300/400/600/700 (seção 4) | `res/font/outfit*.ttf` + `outfit.xml`; estilos `Lav.Texto`, `.H1`, `.H2`, `.H3`, `.Enfase`, `.Auxiliar`, `.Rotulo`, `.Valor` (números tabulares) |
| Tamanhos e altura de linha (seção 4) | `res/values/dimens.xml`: títulos 28/22/18 sp, corpo 16, auxiliar 14, mínimo 12; `linha_*` = 1,2 nos títulos e 1,5 nos textos (via `android:lineHeight`). Tablet (`values-sw600dp`): 40/32/24 sp |
| Espaçamento 8 dp, raios 8/12/16, controles 52 dp, cabeçalho 56 dp (seções 5 e 8) | `res/values/dimens.xml` (`espaco_*`, `raio*`, `altura_controle`, `altura_cabecalho`); `values-sw600dp` com margens de 32 dp e padding de cartão de 24 dp |
| Componentes (seção 5) | `res/values/themes.xml` e `styles.xml` — ver tabela abaixo |
| Ícones Lucide, traço 2 (seção 5) | `res/drawable/ic_*.xml`; traço padrão em `texto`, cor real definida por `tint` |
| Logo (seção 2) | `res/drawable-nodpi/logo_principal.png` (horizontal), `logo_vertical.png`, `logo_nome.png` ("só o nome"), todos com fundo transparente; ícone do app em `mipmap-*/ic_launcher.png`. Recortados de `assets/img` sem alterar o desenho |

## Componentes
| Componente | Estilo | Aplicação do guia |
|---|---|---|
| Tela | `Theme.Lavicameta` | Fundo `fundo_secao`; cabeçalhos, cartões e campos brancos |
| Botão primário | `Lav.Botao` (+ `Lav.BotaoPrincipal` para largura total) | Fundo `roxo`, texto branco, toque `roxo_escuro`; desabilitado `borda` / `texto_secundario`; raio 12, 52 dp |
| Botão secundário | `Lav.Botao.Secundario` | Contorno 2 dp `roxo`, texto `roxo` |
| Botão secundário em pares | `Lav.Botao.Secundario.Compacto` | Ícone 20 dp e padding de 12 dp, rótulo em uma linha (cartões em 2 colunas) |
| Botão de texto | `Lav.Botao.Texto` | Texto `roxo` |
| Campo | `Lav.Campo` | Fundo branco, rótulo visível, borda 1 dp `borda`, foco 2 dp `lilas`, erro em `erro` com ícone |
| Cartão | `Lav.Cartao` | Branco, borda 1 dp, raio 16, padding 16 (24 no tablet) |
| Abas | `Lav.Abas` | Ativa em `roxo` com sublinhado 3 dp `lilas`; inativa em `texto` |
| Filtros | `Lav.Filtro` | Selecionado: fundo `roxo`, texto branco |
| Barra inferior | `Lav.BottomNav` | Ativo em `roxo` com indicador `info_fundo`; inativo em `texto` |
| Selo de status | `Lav.Selo` + `UiText.coresStatus*` | Texto na cor do estado sobre o fundo claro correspondente |
| Alertas | `Lav.Alerta.Erro`, `.Sucesso`, `.Info`, `.Aviso` | Fundo claro do estado, ícone + texto, raio 12 |
| Carregamento | `Lav.Carregando` | Indicador `roxo` sobre trilho `borda` |
| Indicador de ciclo | `activity_cycle.xml` (`CircularProgressIndicator`) | Anel `lilas` sobre trilho `borda`, tempo restante no centro |
| Ícone de máquina | `bg_icone_maquina` | Ícone `roxo` sobre `info_fundo` |

## Status das máquinas
| Status | Texto | Cor |
|---|---|---|
| Disponível | "Disponível" | `sucesso` sobre `sucesso_fundo` |
| Em uso / Reservada | "Em uso · N min restantes" / "Reservada" | `alerta` sobre `alerta_fundo` |
| Manutenção (indisponível) | "Manutenção" | `texto_secundario` sobre `fundo_secao` |

## Regras de implementação
- Nenhuma cor ou medida literal em Activity/Fragment/layout: usar os recursos acima; estilos reaproveitados entre telas.
- `lilas` só em ícones, gráficos, bordas de foco e textos ≥ 24 sp (contraste ~3,3:1 sobre branco); nunca em texto comum.
- Sem gradientes, sombras fortes ou cores fora da paleta; a marca é sempre escrita "lav.cametá"; a logo não tem slogan.
- Logo horizontal com no mínimo 120 dp de largura; abaixo disso, usar o ícone.
- Textos em pt-BR em `strings.xml`, tratando o cliente por “você”; moeda em BRL; datas no fuso `America/Belem`.
- Estado sempre com **texto** além da cor.
- Botão em carregamento: desabilitado e com indicador giratório (`Ui.carregando`).
- Estados de carregamento, vazio e erro com “Tentar novamente” (`view_estado.xml`, `EstadoView`).
- Acessibilidade: rótulos visíveis nos campos, `contentDescription` em ícones informativos, títulos marcados como heading, regiões vivas para erros, área de toque ≥ 48 dp.
- Responsividade: cartões em 1 coluna no celular e 2 no tablet (`integers.xml`); formulários com largura máxima de 600 dp centralizados no tablet.

## Página web (checkout sandbox)
`backend/.../controller/SandboxController.java` gera a página de homologação com os tokens CSS da seção 17 do guia (`--cor-principal`, `--cor-secundaria`, `--cor-fundo-secao` etc.) e carrega a Outfit pelo Google Fonts. Ao criar novas páginas web, reutilizar esses mesmos nomes de variáveis.

## Pendências
- Modo escuro: o guia marca como “[A DEFINIR]”; o app usa só o tema claro.
- Status com ícone além do texto (guia, seção 5): hoje é texto + cor.
- Skeleton loading (#ECE9F2): não implementado; o app usa indicador giratório.
- `values-sw900dp` usa 3 colunas, mas o guia prevê 3 colunas apenas para uma eventual versão web.
- Validação com o responsável das telas que o PDF não prototipou.
