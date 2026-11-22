package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AccountRepository;
import br.com.lavicameta.domain.model.Carteira;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Result;

/** Carteira: pagamentos realizados e formas de pagamento tokenizadas (RF15). */
public class WalletViewModel extends ViewModel {

    private final AccountRepository conta;
    private final MutableLiveData<Result<Carteira>> carteira = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> mensagem = new MutableLiveData<>();

    public WalletViewModel(AccountRepository conta) {
        this.conta = conta;
    }

    public LiveData<Result<Carteira>> carteira() { return carteira; }
    public LiveData<Event<String>> mensagem() { return mensagem; }

    public void carregar() {
        carteira.setValue(Result.loading());
        conta.carteira(carteira::setValue);
    }

    public void remover(String metodoId) {
        conta.removerMetodo(metodoId, resultado -> {
            if (resultado.isError()) {
                mensagem.setValue(new Event<>(resultado.message));
            }
            carregar();
        });
    }
}
