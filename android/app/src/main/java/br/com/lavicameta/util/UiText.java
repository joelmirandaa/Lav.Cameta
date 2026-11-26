package br.com.lavicameta.util;

import android.content.Context;
import androidx.core.content.ContextCompat;
import br.com.lavicameta.R;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.domain.usecase.FormValidator;

/** Textos e cores de estados de domínio. Estado sempre com texto, não só cor (STYLE.md). */
public final class UiText {

    private UiText() {
    }

    public static String erroCampo(Context c, FormValidator.Erro erro) {
        if (erro == null) {
            return null;
        }
        switch (erro) {
            case OBRIGATORIO: return c.getString(R.string.erro_obrigatorio);
            case EMAIL_INVALIDO: return c.getString(R.string.erro_email);
            case TELEFONE_INVALIDO: return c.getString(R.string.erro_telefone);
            case SENHA_FRACA: return c.getString(R.string.erro_senha_fraca);
            case CONTATO_OBRIGATORIO: return c.getString(R.string.erro_contato);
            case SENHAS_DIFERENTES: return c.getString(R.string.erro_senhas_diferentes);
            case NUMERO_INVALIDO: return c.getString(R.string.erro_numero);
            case MUITO_LONGO: return c.getString(R.string.erro_muito_longo);
            default: return null;
        }
    }

    public static String statusMaquina(Context c, Maquina m) {
        if (Maquina.MANUTENCAO.equals(m.status)) {
            return c.getString(R.string.status_manutencao);
        }
        if (Maquina.EM_USO.equals(m.status)) {
            return m.minutosRestantes == null ? c.getString(R.string.status_em_uso)
                    : c.getString(R.string.status_em_uso_minutos, m.minutosRestantes);
        }
        return m.reservada ? c.getString(R.string.status_reservada) : c.getString(R.string.status_disponivel);
    }

    /** Par (cor do texto, cor do fundo) do selo de status. */
    public static int[] coresStatusMaquina(Context c, Maquina m) {
        int texto;
        int fundo;
        if (Maquina.MANUTENCAO.equals(m.status)) {
            texto = R.color.texto_secundario;
            fundo = R.color.fundo_secao;
        } else if (Maquina.EM_USO.equals(m.status) || m.reservada) {
            texto = R.color.alerta;
            fundo = R.color.alerta_fundo;
        } else {
            texto = R.color.sucesso;
            fundo = R.color.sucesso_fundo;
        }
        return new int[] {ContextCompat.getColor(c, texto), ContextCompat.getColor(c, fundo)};
    }

    public static String tipoMaquina(Context c, String tipo) {
        return Maquina.LAVADORA.equals(tipo) ? c.getString(R.string.tipo_lavadora) : c.getString(R.string.tipo_secadora);
    }

    public static String servico(Context c, String servico) {
        if (servico == null) {
            return "";
        }
        switch (servico) {
            case "LAVAR": return c.getString(R.string.servico_lavar);
            case "SECAR": return c.getString(R.string.servico_secar);
            case "LAVAR_SECAR": return c.getString(R.string.servico_lavar_secar);
            default: return servico;
        }
    }

    public static String statusPedido(Context c, String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "AGUARDANDO_PAGAMENTO": return c.getString(R.string.pedido_aguardando);
            case "PAGO": return c.getString(R.string.pedido_pago);
            case "EM_ANDAMENTO": return c.getString(R.string.pedido_andamento);
            case "CONCLUIDO": return c.getString(R.string.pedido_concluido);
            case "CANCELADO": return c.getString(R.string.pedido_cancelado);
            case "EXPIRADO": return c.getString(R.string.pedido_expirado);
            case "ESTORNADO": return c.getString(R.string.pedido_estornado);
            default: return status;
        }
    }

    public static int[] coresStatusPedido(Context c, String status) {
        int texto = R.color.texto_secundario;
        int fundo = R.color.fundo_secao;
        if ("CONCLUIDO".equals(status) || "PAGO".equals(status)) {
            texto = R.color.sucesso;
            fundo = R.color.sucesso_fundo;
        } else if ("EM_ANDAMENTO".equals(status) || "AGUARDANDO_PAGAMENTO".equals(status)) {
            texto = R.color.info;
            fundo = R.color.info_fundo;
        } else if ("ESTORNADO".equals(status) || "EXPIRADO".equals(status)) {
            texto = R.color.alerta;
            fundo = R.color.alerta_fundo;
        }
        return new int[] {ContextCompat.getColor(c, texto), ContextCompat.getColor(c, fundo)};
    }

    public static String metodo(Context c, String metodo) {
        if ("PIX".equals(metodo)) {
            return c.getString(R.string.metodo_pix);
        }
        if ("CARTAO_CREDITO".equals(metodo)) {
            return c.getString(R.string.metodo_credito);
        }
        if ("CARTAO_DEBITO".equals(metodo)) {
            return c.getString(R.string.metodo_debito);
        }
        return metodo == null ? "" : metodo;
    }

    public static String statusPagamento(Context c, String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "PENDENTE": return c.getString(R.string.pagamento_pendente);
            case "APROVADO": return c.getString(R.string.pagamento_aprovado);
            case "RECUSADO": return c.getString(R.string.pagamento_recusado);
            case "EXPIRADO": return c.getString(R.string.pagamento_expirado);
            case "ESTORNO_PENDENTE": return c.getString(R.string.pagamento_estorno_pendente);
            case "ESTORNADO": return c.getString(R.string.pagamento_estornado);
            default: return status;
        }
    }

    public static String etapa(Context c, String etapa) {
        return "SECAGEM".equals(etapa) ? c.getString(R.string.etapa_secagem) : c.getString(R.string.etapa_lavagem);
    }
}
