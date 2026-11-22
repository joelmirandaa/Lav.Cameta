package br.com.lavicameta.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import br.com.lavicameta.AppContainer;

/** Cria os ViewModels com os repositórios do {@link AppContainer}. */
public final class ViewModelFactory implements ViewModelProvider.Factory {

    private final AppContainer c;

    public ViewModelFactory(AppContainer container) {
        this.c = container;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> classe) {
        if (classe == LoginViewModel.class) return (T) new LoginViewModel(c.auth);
        if (classe == RegisterViewModel.class) return (T) new RegisterViewModel(c.auth);
        if (classe == RecoverPasswordViewModel.class) return (T) new RecoverPasswordViewModel(c.auth);
        if (classe == MachinesViewModel.class) return (T) new MachinesViewModel(c.catalogo);
        if (classe == MachineDetailViewModel.class) return (T) new MachineDetailViewModel(c.catalogo, c.pedidos);
        if (classe == PaymentViewModel.class) return (T) new PaymentViewModel(c.pedidos);
        if (classe == CycleViewModel.class) return (T) new CycleViewModel(c.pedidos);
        if (classe == HistoryViewModel.class) return (T) new HistoryViewModel(c.pedidos);
        if (classe == WalletViewModel.class) return (T) new WalletViewModel(c.conta);
        if (classe == ProfileViewModel.class) return (T) new ProfileViewModel(c.conta, c.auth);
        if (classe == AdminReportsViewModel.class) return (T) new AdminReportsViewModel(c.admin);
        if (classe == AdminMachinesViewModel.class) return (T) new AdminMachinesViewModel(c.admin);
        if (classe == AdminMachineFormViewModel.class) return (T) new AdminMachineFormViewModel(c.admin);
        if (classe == AdminPricesViewModel.class) return (T) new AdminPricesViewModel(c.admin);
        throw new IllegalArgumentException("ViewModel desconhecido: " + classe.getName());
    }
}
