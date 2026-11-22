package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AdminRepository;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.domain.usecase.FormValidator;
import br.com.lavicameta.domain.usecase.FormValidator.Erro;
import br.com.lavicameta.util.Result;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** Cadastro e edição de máquina (UC15). */
public class AdminMachineFormViewModel extends ViewModel {

    private final AdminRepository admin;
    private final MutableLiveData<Result<Maquina>> salvamento = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Erro>> errosCampos = new MutableLiveData<>(new HashMap<>());

    public AdminMachineFormViewModel(AdminRepository admin) {
        this.admin = admin;
    }

    public LiveData<Result<Maquina>> salvamento() { return salvamento; }
    public LiveData<Map<String, Erro>> errosCampos() { return errosCampos; }

    public void salvar(Integer id, String nome, String tipo, String capacidade, String recursos) {
        Map<String, Erro> erros = new HashMap<>();
        Erro erroNome = FormValidator.obrigatorio(nome, 50);
        if (erroNome != null) {
            erros.put("nome", erroNome);
        }
        if (tipo == null) {
            erros.put("tipo", Erro.OBRIGATORIO);
        }
        BigDecimal kg = FormValidator.decimalPositivo(capacidade);
        if (kg == null || kg.compareTo(new BigDecimal("999.9")) > 0 || kg.scale() > 1) {
            erros.put("capacidade", Erro.NUMERO_INVALIDO);
        }
        if (recursos != null && recursos.length() > 200) {
            erros.put("recursos", Erro.MUITO_LONGO);
        }
        errosCampos.setValue(erros);
        if (!erros.isEmpty() || (salvamento.getValue() != null && salvamento.getValue().isLoading())) {
            return;
        }
        salvamento.setValue(Result.loading());
        admin.salvarMaquina(id, nome.trim(), tipo, kg, recursos, salvamento::setValue);
    }
}
