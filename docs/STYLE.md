# STYLE.md — identidade e padrões de interface

**Status:** identidade definida em [`lav-cameta-guia-de-design.md`](lav-cameta-guia-de-design.md), que é a especificação visual oficial e substitui o protótipo roxo WashFlow citado no PDF (RNF09). Este arquivo descreve como o guia foi aplicado no app Android.

## Onde ficam os tokens (Android)
| Guia | Recurso |
|---|---|
| Paleta (seção 3) | `res/values/colors.xml` — `marinho`, `agua`, `destaque`, `fundo_secao`, `texto`, `texto_secundario`, `borda`, `sucesso`, `alerta`, `erro`, `info` e fundos claros dos estados |
| Tipografia Outfit 300/400/600/700 (seção 4) | `res/font/outfit*.ttf` + `outfit.xml`; estilos `Lav.Texto`, `.H1`, `.H2`, `.H3`, `.Enfase`, `.Auxiliar`, `.Rotulo`, `.Valor` |
| Espaçamento 8 dp, raios 8/12/16, alturas 48/64 (seções 5 e 8) | `res/values/dimens.xml` (e `values-sw600dp` para tablet) |
| Componentes (seção 5) | `res/values/themes.xml` e `styles.xml`: `Lav.Botao` (primário azul-água), `Lav.Botao.Secundario` (contorno marinho 2 dp), `Lav.Campo` (borda 1 dp, foco azul 2 dp, erro com ícone), `Lav.Cartao` (raio 16, borda), `Lav.Abas` (sublinhado 3 dp), `Lav.Selo`, `Lav.Alerta.*` |
| Ícones Lucide, traço 2 (seção 5) | `res/drawable/ic_*.xml`, cor por `tint` |
| Logo (seção 2) | `res/drawable-nodpi/logo_principal.png`, `logo_vertical.png`; ícone do app em `mipmap-*`. Recortados de `assets/img` sem alterar o desenho |

## Regras de implementação
- Textos em pt-BR em `strings.xml`, tratando o cliente por “você”; moeda em BRL; datas no fuso `America/Belem`.
- Nenhuma cor ou medida literal em Activity/Fragment; estilos reaproveitados entre telas.
- Estado sempre com **texto** além da cor: Disponível (verde), Em uso · N min (azul), Reservada (azul), Manutenção (âmbar).
- Botão em carregamento: desabilitado e com indicador giratório (`Ui.carregando`).
- Estados de carregamento, vazio e erro com “Tentar novamente” (`view_estado.xml`, `EstadoView`).
- Acessibilidade: rótulos visíveis nos campos, `contentDescription` em ícones informativos, títulos marcados como heading, regiões vivas para erros, área de toque ≥ 48 dp.
- Responsividade: cartões em 1/2/3 colunas; formulários com largura máxima de 600 dp centralizados em tablet.

## Pendências
- Modo escuro: o guia marca como “[A DEFINIR]”; o app usa tema claro.
- Validação com o responsável das telas que o PDF não prototipou.
