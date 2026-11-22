# Plano de testes

**Fonte:** PDF seção 10. Ambiente previsto: Android 12+, servidor de homologação, gateway sandbox e máquina simulada. Critério: 100% dos testes de alta prioridade passando, sem defeito crítico.

**Problemas de rastreabilidade no PDF (mantidos como registro):** CT03 cita RN12 (deveria ser UC02); CT16 cita RN15 inexistente; CT24 cita RN13 (deveria ser RN12); CT25 cita RNF02, que trata de atualização de status.

## Automatizados (executados em 2026-11-22)
Backend: `cd backend && ./mvnw test` — **23 testes, 0 falhas**. Integração com API completa sobre **MySQL 8.4 real** (Testcontainers), gateway sandbox e máquina simulada.

| Caso | Teste | Resultado |
|---|---|---|
| CT01/CT02 login válido e inválido; cadastro, duplicidade, validação | `AutenticacaoIntegracaoTest.cadastroLoginEValidacoes` | OK |
| CT03 bloqueio por 15 min após 5 tentativas | `bloqueioAposCincoTentativasCt03`, `UsuarioBloqueioTest` | OK |
| CT05 recuperação de senha (código por e-mail, uso único) | `recuperacaoDeSenhaCt05` (SMTP simulado com mock) | OK |
| CT18/CT19 login admin, cliente barrado no admin, admin barrado no cliente | `separacaoClienteAdministradorCt18Ct19` | OK |
| RF16 perfil e troca de senha | `perfilEdicaoRf16` | OK |
| CT06–CT11 listar, filtrar, manutenção, preços R$ 12 / R$ 18, serviço incompatível | `catalogoFiltrosPrecosEStatusCt06aCt11` | OK |
| CT12 PIX aprovado inicia a máquina; webhook sem assinatura e com valor divergente rejeitados; evento duplicado processado uma vez; fim de ciclo libera a máquina; CT17 histórico | `pixAprovadoIniciaMaquinaEReentregaNaoDuplicaCt12` | OK |
| CT13 PIX expirado libera máquina; pagamento tardio é estornado | `pixExpiradoLiberaMaquinaEPagamentoTardioEEstornadoCt13` | OK |
| CT14 cartão recusado permite outro método | `cartaoRecusadoPermiteOutroMetodoCt14` | OK |
| CT15 falha da máquina gera estorno integral | `falhaNaMaquinaGeraEstornoIntegralCt15` | OK |
| CT16 6 clientes simultâneos na mesma máquina: só 1 reserva | `concorrenciaApenasOPrimeiroReservaCt16` | OK |
| RN08 combo lavadora → secadora | `comboLavarSecarUsaLavadoraESecadoraRn08` | OK |
| Cancelamento e acesso a pedido de outro cliente | `cancelamentoLiberaMaquinaEOutroClienteNaoVePedido` | OK |
| CT24 carteira sem dados de cartão | `carteiraNaoExpoeDadosDeCartaoCt24` | OK |
| CT20–CT23 relatórios mensal/anual, série, data de atualização | `relatoriosMensalEAnualCt20aCt23` | OK |
| CT22 variação = +12,5% | `RelatorioServiceTest` | OK |
| UC15 gestão de máquinas e preços | `gestaoDeMaquinasEPrecosUc15` | OK |
| Padrão de produção sem contratos: checkout 503, webhook e sandbox indisponíveis | `ProvedoresNaoConfiguradosIntegracaoTest` | OK |

Android: `cd android && ./gradlew testDebugUnitTest` — **9 testes, 0 falhas** (validações de formulário, regras do cadastro, formatação BRL/percentual/fuso de Belém, mudança de etapa do ciclo). `./gradlew lintDebug`: 0 erros.

**Defeitos encontrados pelos testes e corrigidos:** reservas concorrentes duplicadas (isolamento MySQL, ver AD-008); chave de idempotência de falha maior que a coluna.

## Manuais (executados em 2026-11-22)
- Fluxo da API ponta a ponta via script contra MySQL local (cadastro → reserva → cartão recusado → PIX aprovado → máquina em uso → fim da lavagem → secagem → histórico → carteira → relatórios).
- App no emulador (Android 17, 1080×2400) contra a API local: login, lista de máquinas, detalhe, seleção do combo, pagamento com contagem de 5 min, geração do PIX com QR Code, aprovação no sandbox → navegação automática para o acompanhamento (40 min), login administrativo, relatórios Mês/Ano, lista de máquinas com status em tempo real e validação do formulário de máquina. Ajustes visuais feitos a partir disso (edge-to-edge, tingimento de superfícies, rótulos cortados).

## Não executados
- CT04 Google real (exige Client ID OAuth e conta Google no aparelho).
- Envio real de e-mail (SMTP não configurado neste ambiente).
- Gateway Bex e máquinas reais (sem contrato/protocolo).
- CT25 carga de 200 usuários simultâneos.
- Testes instrumentados (Espresso) e verificação em tablet físico/emulador de tablet (layouts `sw600dp`/`sw900dp` existem, mas não foram inspecionados visualmente).
