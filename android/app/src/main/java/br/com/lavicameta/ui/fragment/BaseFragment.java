package br.com.lavicameta.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import br.com.lavicameta.LavicametaApp;
import br.com.lavicameta.viewmodel.ViewModelFactory;

public abstract class BaseFragment extends Fragment {

    protected <T extends ViewModel> T viewModel(Class<T> classe) {
        LavicametaApp app = (LavicametaApp) requireActivity().getApplication();
        return new ViewModelProvider(this, new ViewModelFactory(app.container())).get(classe);
    }

    protected String nomeUsuario() {
        return ((LavicametaApp) requireActivity().getApplication()).container().sessao.nome();
    }
}
