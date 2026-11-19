# Validação da entrega — 2026-11-18

- README, STYLE e AGENTS criados e não vazios: **OK**.
- Arquivos XML (`pom.xml`, Manifest, layout e recursos): **OK**, parsing padrão Python.
- `application.yml`: yaml validado.
- Java disponível: OpenJDK 21.
- Maven: **não instalado** neste ambiente, portanto backend não foi compilado/testado.
- Gradle: **não instalado** neste ambiente, portanto Android não foi compilado/testado.
- Android SDK: não inspecionado; sync/build no Android Studio requer SDK 35 e rede para resolução de dependências.
- Wrapper JAR: não disponível localmente; instrução de geração oficial registrada em android/gradle/wrapper/WRAPPER-JAR-NECESSARIO.md. Não declarar wrapper funcional sem JAR.
- `GET /api/v1/health` escrito, **não executado**.
- Nenhuma integração com gateway/hardware foi implementada ou testada.
- Ausência de segredos literais verificada por inspeção básica; credenciais exigidas via variáveis de ambiente.
