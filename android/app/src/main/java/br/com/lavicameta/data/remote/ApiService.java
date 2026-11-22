package br.com.lavicameta.data.remote;

import br.com.lavicameta.domain.model.Carteira;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.domain.model.MaquinaDetalhe;
import br.com.lavicameta.domain.model.Pagamento;
import br.com.lavicameta.domain.model.Pagina;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.domain.model.Relatorio;
import br.com.lavicameta.domain.model.Servico;
import br.com.lavicameta.domain.model.Sessao;
import br.com.lavicameta.domain.model.Usuario;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Contrato REST do backend Lav.Cametá (docs/api/contratos.md). */
public interface ApiService {

    @POST("auth/register")
    Call<Sessao> registrar(@Body Map<String, Object> corpo);

    @POST("auth/login")
    Call<Sessao> login(@Body Map<String, Object> corpo);

    @POST("auth/google")
    Call<Sessao> loginGoogle(@Body Map<String, Object> corpo);

    @POST("auth/password-recovery")
    Call<Map<String, String>> solicitarRecuperacao(@Body Map<String, Object> corpo);

    @POST("auth/password-reset")
    Call<Map<String, String>> redefinirSenha(@Body Map<String, Object> corpo);

    @POST("admin/auth/login")
    Call<Sessao> loginAdmin(@Body Map<String, Object> corpo);

    @GET("me")
    Call<Usuario> perfil();

    @PUT("me")
    Call<Usuario> atualizarPerfil(@Body Map<String, Object> corpo);

    @PUT("me/password")
    Call<Void> alterarSenha(@Body Map<String, Object> corpo);

    @GET("machines")
    Call<List<Maquina>> maquinas(@Query("filtro") String filtro);

    @GET("machines/{id}")
    Call<MaquinaDetalhe> maquina(@Path("id") int id);

    @POST("orders")
    Call<Pedido> criarPedido(@Body Map<String, Object> corpo);

    @GET("orders")
    Call<Pagina<Pedido>> historico(@Query("pagina") int pagina, @Query("tamanho") int tamanho);

    @GET("orders/{id}")
    Call<Pedido> pedido(@Path("id") String id);

    @POST("orders/{id}/cancel")
    Call<Pedido> cancelarPedido(@Path("id") String id);

    @POST("orders/{id}/checkout")
    Call<Pagamento> checkout(@Path("id") String id, @Body Map<String, Object> corpo);

    @POST("orders/{id}/drying")
    Call<Pedido> iniciarSecagem(@Path("id") String id);

    @GET("wallet")
    Call<Carteira> carteira();

    @DELETE("wallet/methods/{id}")
    Call<Void> removerMetodo(@Path("id") String id);

    @GET("admin/reports")
    Call<Relatorio> relatorio(@Query("periodo") String periodo, @Query("ano") Integer ano, @Query("mes") Integer mes);

    @GET("admin/machines")
    Call<List<Maquina>> adminMaquinas();

    @POST("admin/machines")
    Call<Maquina> criarMaquina(@Body Map<String, Object> corpo);

    @PUT("admin/machines/{id}")
    Call<Maquina> atualizarMaquina(@Path("id") int id, @Body Map<String, Object> corpo);

    @PATCH("admin/machines/{id}/status")
    Call<Maquina> alterarStatusMaquina(@Path("id") int id, @Body Map<String, Object> corpo);

    @GET("admin/services")
    Call<List<Servico>> adminServicos();

    @PUT("admin/services/{id}")
    Call<Servico> atualizarServico(@Path("id") int id, @Body Map<String, Object> corpo);
}
