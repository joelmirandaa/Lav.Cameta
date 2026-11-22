package br.com.lavicameta.data.remote;

import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.util.Result;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Converte respostas Retrofit em {@link Result}, interpretando o formato de erro da API
 * ({@code codigo, mensagem, campos}) e tratando falhas de rede e sessão expirada.
 */
public final class ApiCallback<T> implements Callback<T> {

    public static final String SEM_CONEXAO = "SEM_CONEXAO";
    private static final String MSG_REDE = "Sem conexão com o servidor. Verifique sua internet e tente novamente.";
    private static final String MSG_GENERICA = "Não foi possível concluir. Tente novamente.";

    private final Consumer<Result<T>> destino;
    private final SessionStore sessao;
    private final boolean rotaPublica;
    private final Gson gson = ApiClient.gson();

    private ApiCallback(Consumer<Result<T>> destino, SessionStore sessao, boolean rotaPublica) {
        this.destino = destino;
        this.sessao = sessao;
        this.rotaPublica = rotaPublica;
    }

    /** Para rotas autenticadas: 401 encerra a sessão local. */
    public static <T> ApiCallback<T> autenticado(SessionStore sessao, Consumer<Result<T>> destino) {
        return new ApiCallback<>(destino, sessao, false);
    }

    /** Para login/cadastro: 401 significa credencial inválida, não sessão expirada. */
    public static <T> ApiCallback<T> publico(Consumer<Result<T>> destino) {
        return new ApiCallback<>(destino, null, true);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            destino.accept(Result.success(response.body()));
            return;
        }
        if (response.code() == 401 && !rotaPublica && sessao != null) {
            sessao.notificarExpiracao();
        }
        destino.accept(erro(response.code(), response.errorBody()));
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (call.isCanceled()) {
            return;
        }
        destino.accept(Result.error(MSG_REDE, SEM_CONEXAO, null));
    }

    private Result<T> erro(int status, ResponseBody corpo) {
        if (corpo != null) {
            try {
                JsonObject json = gson.fromJson(corpo.string(), JsonObject.class);
                if (json != null && json.has("mensagem")) {
                    Map<String, String> campos = new LinkedHashMap<>();
                    if (json.has("campos") && json.get("campos").isJsonObject()) {
                        json.getAsJsonObject("campos").entrySet()
                                .forEach(e -> campos.put(e.getKey(), e.getValue().getAsString()));
                    }
                    String codigo = json.has("codigo") ? json.get("codigo").getAsString() : String.valueOf(status);
                    return Result.error(json.get("mensagem").getAsString(), codigo, campos);
                }
            } catch (IOException | JsonParseException | IllegalStateException ex) {
                // corpo não está no formato padrão; cai na mensagem genérica
            }
        }
        return Result.error(status >= 500 ? "Serviço indisponível no momento. Tente novamente em instantes." : MSG_GENERICA,
                String.valueOf(status), null);
    }
}
