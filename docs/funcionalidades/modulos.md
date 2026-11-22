# Módulos e cobertura

| Módulo | Origem | Backend | Android | Estado |
|---|---|---|---|---|
| Autenticação cliente | RF01–04, UC01–03 | `AuthService`, `RecuperacaoSenhaService`, `GoogleTokenVerifier` | Login, Cadastro, Recuperar senha | Concluído (Google e e-mail dependem de configuração) |
| Catálogo de máquinas | RF05–07, UC04 | `CatalogoService` | Início (T02), Detalhe (T03) | Concluído |
| Serviço/reserva | RF08–09, RN01–05, RN07 | `PedidoService` | Detalhe → Pagamento | Concluído |
| Pagamento | RF10–13, RN04, RN13 | `PagamentoService`, `EstornoService`, `PaymentGateway` | Pagamento (T04) | Fluxo concluído; adaptador Bex aguardando contrato (sandbox disponível) |
| Ciclo | UC08, RN06, RN08 | `CicloService`, `MachineController`, `TarefasAgendadas` | Acompanhamento | Concluído; adaptador de hardware aguardando protocolo (simulador disponível) |
| Conta | RF14–16 | `PedidoConsultaService`, `CarteiraService`, `PerfilService` | Histórico, Carteira, Perfil | Concluído |
| Administração | RF17–21, UC12–15 | `RelatorioService`, `AdminCatalogoService` | Acesso (T05), Relatórios (T06/T07), Máquinas, Preços | Concluído |

Telas não prototipadas no PDF (cadastro, recuperação, acompanhamento, histórico, carteira, perfil, gestão) foram desenhadas seguindo o guia de design e devem ser validadas com o responsável.
