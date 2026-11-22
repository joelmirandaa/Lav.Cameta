package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AdminRepository;
import br.com.lavicameta.data.repository.AuthRepository;
import br.com.lavicameta.domain.model.Relatorio;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.Result;
import java.time.LocalDate;

/** T06/T07 Relatórios Mês/Ano com seleção de período (RF18–RF21, UC13/UC14). */
public class AdminReportsViewModel extends ViewModel {

    public static final String MES = "MES";
    public static final String ANO = "ANO";

    private final AdminRepository admin;
    private final MutableLiveData<Result<Relatorio>> relatorio = new MutableLiveData<>();
    private String periodo = MES;
    private int ano;
    private int mes;
    private int requisicao;

    public AdminReportsViewModel(AdminRepository admin) {
        this.admin = admin;
        LocalDate hoje = LocalDate.now(Formatters.ZONA);
        this.ano = hoje.getYear();
        this.mes = hoje.getMonthValue();
    }

    public LiveData<Result<Relatorio>> relatorio() { return relatorio; }
    public String periodo() { return periodo; }
    public int ano() { return ano; }
    public int mes() { return mes; }

    public void carregar() {
        int minha = ++requisicao;
        relatorio.setValue(Result.loading());
        admin.relatorio(periodo, ano, MES.equals(periodo) ? mes : null, resultado -> {
            if (minha == requisicao) {
                relatorio.setValue(resultado);
            }
        });
    }

    public void alterarPeriodo(String novo) {
        if (!novo.equals(periodo)) {
            periodo = novo;
            carregar();
        }
    }

    public void escolherData(int novoAno, int novoMes) {
        ano = novoAno;
        mes = novoMes;
        carregar();
    }
}
