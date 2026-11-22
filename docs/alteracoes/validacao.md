# Validação da entrega

## 2026-11-22
- Backend compilado com JDK 21 (`./mvnw`); **23/23 testes** passando, inclusive integração com MySQL 8.4 real.
- API executada contra MySQL local; Flyway aplicou V1–V3 e `ddl-auto: validate` passou.
- Android: `assembleDebug` gera `app-debug.apk`; **9/9 testes unitários**; lint sem erros (avisos restantes: versões de dependências mais novas, textos concatenados com “·”).
- App executado no emulador contra a API: fluxos principais de cliente e administrador verificados visualmente.
- Sem segredos versionados: credenciais apenas por variáveis de ambiente; `backend/.env.example` sem valores; senhas de desenvolvimento só no container local.
- Não validado: Bex e hardware reais, e-mail real, login Google real, carga (CT25), tablet.

## 2026-11-18
Estrutura inicial; Maven/Gradle não estavam disponíveis, nada compilado; nenhum teste de domínio executado.
