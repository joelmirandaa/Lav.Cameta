# Como rodar e testar o lav.cametá localmente

Guia prático para subir a API, o banco e o app Android no computador e testar os fluxos em **modo de homologação**: gateway de pagamento *sandbox* e máquinas simuladas. Nenhum dinheiro real é cobrado e nenhuma máquina física é acionada.

> Os valores entre `< >` são seus e ficam só no `backend/.env`, que o Git ignora. Nunca versione senhas ou segredos.

## Pré-requisitos
- **JDK 21.** O Gradle 8.13 não roda com JDK 25/26; aponte `JAVA_HOME` para o 21.
- **Docker** ligado.
- **Android SDK** com um emulador (AVD). Neste computador o AVD se chama `Medium_Phone`.

Itens que precisaram ser baixados estão em [utilitarios.md](utilitarios.md).

---

## 1. Banco de dados (MySQL 8.4)

**Se o container `lavicameta-mysql` já existe** (criado durante o desenvolvimento):
```bash
docker start lavicameta-mysql
```
Use no passo 2 o mesmo usuário e senha definidos quando o container foi criado.

**Se for a primeira vez**, preencha o `backend/.env` (passo 2) e suba o banco pelo compose:
```bash
cd ~/Downloads/Lav.cameta
docker compose --env-file backend/.env up -d mysql
```
O banco fica em `127.0.0.1:3307`. As tabelas são criadas automaticamente pelo Flyway quando a API inicia.

## 2. Backend (API)

```bash
cd ~/Downloads/Lav.cameta/backend
cp .env.example .env
```

Edite o arquivo `backend/.env`:
```
DB_URL=jdbc:mysql://127.0.0.1:3307/lavicameta
DB_USERNAME=lavicameta
DB_PASSWORD=<senha-do-banco-local>
JWT_SECRET=<gere com: openssl rand -base64 48>
ADMIN_BOOTSTRAP_EMAIL=admin@lavcameta.local
ADMIN_BOOTSTRAP_PASSWORD=<senha-do-admin-com-10+-caracteres>
PAYMENT_PROVIDER=sandbox
PAYMENT_WEBHOOK_SECRET=<segredo-local>
PUBLIC_BASE_URL=http://10.0.2.2:8080
MACHINE_CONTROLLER=simulador
```
Para gerar um `JWT_SECRET` aleatório: `openssl rand -base64 48`.

Inicie a API:
```bash
set -a && source .env && set +a
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./mvnw spring-boot:run
```
A API está pronta quando aparecer `Started LavicametaApplication`. Para conferir:
```bash
curl localhost:8080/api/v1/health      # {"status":"UP"}
```
Deixe este terminal aberto.

O administrador é criado na primeira execução com `ADMIN_BOOTSTRAP_EMAIL` e `ADMIN_BOOTSTRAP_PASSWORD`, se ainda não existir nenhum. A senha precisa ter 10 ou mais caracteres.

## 3. App Android

Em outro terminal:
```bash
~/Android/Sdk/emulator/emulator -avd Medium_Phone &
cd ~/Downloads/Lav.cameta/android
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew installDebug
```
Outra opção: abrir a pasta `android/` no Android Studio e clicar em **▶ Run**.

No emulador, o app acessa a API em `http://10.0.2.2:8080`, que é o endereço do computador visto de dentro do emulador.

### Usando um celular físico
1. Descubra o IP do computador na rede (`ip addr` ou `hostname -I`). Exemplo: `192.168.0.10`.
2. No `backend/.env`, defina `PUBLIC_BASE_URL=http://192.168.0.10:8080` e reinicie a API.
3. Instale o app apontando para esse IP:
   ```bash
   ./gradlew installDebug -Plavicameta.apiBaseUrl.debug=http://192.168.0.10:8080/api/v1/
   ```
4. O celular precisa estar na mesma rede Wi-Fi, e o firewall precisa permitir a porta 8080.

---

## 4. Roteiro de teste

### Administrador
1. Na tela de login, toque em **Acesso administrativo**.
2. Entre com o e-mail e a senha definidos em `ADMIN_BOOTSTRAP_EMAIL` / `ADMIN_BOOTSTRAP_PASSWORD`.
3. Toque no **ícone de máquina** (topo) → **Nova máquina**. Cadastre ao menos uma lavadora e uma secadora.
4. **Dica:** menu **⋮ → Preços**, mude as durações para **1 min** e salve. Assim o ciclo termina rápido.
5. Veja os **Relatórios** nas abas Mês e Ano. O **calendário** permite escolher outro período.
6. Saia pelo menu **⋮ → Sair**.

### Cliente
1. Toque em **Cadastre-se** e crie uma conta (e-mail ou telefone, senha com letras e números).
2. A lista mostra as máquinas com status em tempo real. Use os filtros **Lavar / Secar / Ambos**.
3. Toque em uma lavadora, escolha **Lavar + Secar** e toque em **Continuar**. A máquina fica reservada por 5 minutos.
4. Toque em **Gerar PIX**. Aparecem o QR Code e o código copia e cola.
5. **Simule o pagamento:** toque no **QR Code** para abrir a página de homologação e depois em **Aprovar pagamento**.
6. Volte ao app. Em até 3 segundos ele abre o **Acompanhamento**, com a máquina em uso e o tempo restante.
7. Ao fim da lavagem, toque em **Iniciar secagem**.
8. Confira **Histórico**, **Carteira** e **Perfil** na barra inferior.

### Outros cenários
| Cenário | Como testar | Resultado esperado |
|---|---|---|
| Cartão recusado | Escolha **Crédito** → **Pagar com cartão** → **Abrir pagamento seguro** → **Recusar pagamento** | Mensagem de recusa; dá para tentar outro método |
| PIX expirado | Gere o PIX e não pague por 5 min | Pedido expira e a máquina é liberada |
| Cancelar pedido | Na tela de pagamento, toque em **voltar** → **Cancelar pedido** | Máquina liberada na hora |
| Bloqueio de conta | Erre a senha 5 vezes | Conta bloqueada por 15 min |
| Máquina em manutenção | Como admin, coloque uma máquina em **Manutenção** | Cliente vê "Manutenção" e não consegue contratar |
| Falha da máquina e estorno | Rode o comando abaixo e depois pague | Pedido aparece como estornado |

Comando para forçar falha numa máquina simulada (troque `1` pelo id da máquina; rode na pasta `backend` depois de `set -a && source .env && set +a`):
```bash
TOKEN=$(curl -s -H 'Content-Type: application/json' \
  -d "{\"identificador\":\"$ADMIN_BOOTSTRAP_EMAIL\",\"senha\":\"$ADMIN_BOOTSTRAP_PASSWORD\"}" \
  localhost:8080/api/v1/admin/auth/login | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')
curl -X PUT -H "Authorization: Bearer $TOKEN" "localhost:8080/api/v1/admin/simulator/machines/1/failure?ativa=true"
```
Para desfazer, use `ativa=false`. A marcação de falha some quando a API reinicia.

---

## 5. Testes automatizados
```bash
cd ~/Downloads/Lav.cameta/backend && ./mvnw test                  # 23 testes (requer Docker ligado)
cd ~/Downloads/Lav.cameta/android && ./gradlew testDebugUnitTest  # 9 testes
```

## 6. Recursos que precisam de configuração extra
| Recurso | O que configurar | Sem configuração |
|---|---|---|
| Esqueceu sua senha? | `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` no `.env` | O app mostra "Recuperação de senha indisponível" |
| Entrar com Google | `GOOGLE_CLIENT_ID` no `.env` e `-Plavicameta.googleWebClientId=...` no build | O botão não aparece |
| Pagamento real (Bex) | Aguarda documentação oficial | Com `PAYMENT_PROVIDER=bex`, o pagamento responde "indisponível" |
| Máquinas reais | Aguarda protocolo do fabricante | Com `MACHINE_CONTROLLER=hardware`, todo início falha e gera estorno |

Para testar a recuperação de senha sem um provedor real, dá para usar um servidor SMTP de testes local (ex.: Mailpit em Docker, porta 1025, com `MAIL_SMTP_AUTH=false` e `MAIL_STARTTLS=false`).

## 7. Problemas comuns
| Sintoma | Solução |
|---|---|
| Gradle falha com erro de versão do Java | `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` |
| API não sobe: `JWT_SECRET deve ser definido…` | Defina `JWT_SECRET` com 32 ou mais caracteres e rode de novo o `source .env` |
| API não sobe: erro de conexão com o banco | `docker start lavicameta-mysql` e confira `DB_URL` (porta 3307) |
| App mostra "Sem conexão com o servidor" | A API está rodando? No celular físico, veja a seção de IP acima |
| Página do QR Code não abre | `PUBLIC_BASE_URL` precisa ser alcançável pelo aparelho (`10.0.2.2` no emulador) |
| Porta 8080 ocupada | `PORT=8081` no `.env` e ajuste as URLs do app e do `PUBLIC_BASE_URL` |

## 8. Parar tudo
- **API:** `Ctrl+C` no terminal dela.
- **Emulador:** feche a janela ou rode `~/Android/Sdk/platform-tools/adb emu kill`.
- **Banco:** `docker stop lavicameta-mysql`. Para apagar o banco local: `docker rm -f lavicameta-mysql`.
