# Utilitários baixados para rodar localmente

Registro do que **não estava instalado** na máquina de desenvolvimento e foi baixado durante a implementação (2026-11-22). Nada foi instalado no sistema operacional com privilégios de administrador.

| Item | Versão | Origem | Onde ficou | Para quê |
|---|---|---|---|---|
| Apache Maven (binário) | 3.9.11 | archive.apache.org | pasta temporária da sessão (descartável) | Usado uma vez para gerar o Maven Wrapper |
| Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`) | 3.3.4, aponta para Maven 3.9.11 | plugin oficial `maven-wrapper-plugin` | `backend/` (versionado) | Rodar o backend sem Maven instalado. O Maven em si é baixado em `~/.m2/wrapper` na primeira execução |
| Gradle Wrapper JAR | 8.13 | gerado com a distribuição Gradle 8.13 já presente em `~/.gradle` (`gradle wrapper`) | `android/gradle/wrapper/gradle-wrapper.jar` (versionado) | Substitui a pendência registrada em `WRAPPER-JAR-NECESSARIO.md` (arquivo removido) |
| Android SDK Platform 35 | rev. 2 | baixado automaticamente pelo Android Gradle Plugin | `~/Android/Sdk/platforms/android-35` | `compileSdk 35` do app (só havia a 37) |
| Android SDK Build-Tools | 35.0.0 | baixado automaticamente pelo Android Gradle Plugin | `~/Android/Sdk/build-tools/35.0.0` | Build do APK |
| Imagem Docker `mysql` | 8.4 (8.4.13) | Docker Hub | cache do Docker | Banco local (`docker-compose.yml`) e testes de integração (Testcontainers) |
| Imagem Docker `testcontainers/ryuk` | 0.14.0 | Docker Hub (automático) | cache do Docker | Limpeza dos containers de teste |
| Dependências Maven (Spring Boot 4.1.2, Flyway, Testcontainers etc.) | conforme `backend/pom.xml` | Maven Central | `~/.m2/repository` | Build e testes do backend |
| Dependências Gradle (AndroidX, Material, Retrofit, ZXing, Credential Manager etc.) | conforme `android/app/build.gradle` | Google Maven / Maven Central | `~/.gradle/caches` | Build do app |
| Fonte Outfit (variável) | Google Fonts, licença SIL OFL 1.1 | github.com/google/fonts (`ofl/outfit`) | convertida em `android/app/src/main/res/font/outfit_{light,regular,semibold,bold}.ttf` | Tipografia oficial do guia de design |
| fontTools (Python) | última disponível | PyPI, em ambiente virtual temporário | pasta temporária da sessão (descartável) | Gerar os pesos estáticos 300/400/600/700 a partir da fonte variável |

## Recursos de execução criados localmente (não versionados)
- Container Docker `lavicameta-mysql` (MySQL 8.4, porta `127.0.0.1:3307`) com credenciais **apenas de desenvolvimento**. Para remover: `docker rm -f lavicameta-mysql`.
- `android/local.properties` com `sdk.dir` (ignorado pelo Git).

## Já existentes e reaproveitados
OpenJDK 21 (`/usr/lib/jvm/java-21-openjdk-amd64`), distribuição Gradle 8.13 em `~/.gradle/wrapper/dists`, Android SDK com emulador e AVD `Medium_Phone` (Android 17), Docker, Python 3.
