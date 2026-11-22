package br.com.lavicameta.service;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Envia o código de recuperação por e-mail (RF04). Exige SMTP configurado por variáveis de ambiente. */
@Component
public class NotificadorRecuperacaoSenha {

    private static final Logger LOG = LoggerFactory.getLogger(NotificadorRecuperacaoSenha.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remetente;
    private final long validadeMinutos;

    public NotificadorRecuperacaoSenha(ObjectProvider<JavaMailSender> mailSender, LavicametaProperties propriedades) {
        this.mailSender = mailSender;
        this.remetente = propriedades.recuperacaoSenha().remetente();
        this.validadeMinutos = propriedades.recuperacaoSenha().validadeMinutos();
    }

    public boolean disponivel() {
        return remetente != null && !remetente.isBlank() && mailSender.getIfAvailable() != null;
    }

    public void enviar(String email, String nome, String codigo) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || !disponivel()) {
            throw indisponivel();
        }
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(email);
        mensagem.setSubject("lav.cametá — código para redefinir sua senha");
        mensagem.setText("Olá, " + nome + ".\n\n"
                + "Use o código abaixo no aplicativo lav.cametá para criar uma nova senha:\n\n"
                + "    " + codigo + "\n\n"
                + "O código vale por " + validadeMinutos + " minutos. "
                + "Se você não pediu a troca de senha, ignore este e-mail.");
        try {
            sender.send(mensagem);
        } catch (MailException ex) {
            LOG.error("Falha ao enviar e-mail de recuperação: {}", ex.getClass().getSimpleName());
            throw ApiException.indisponivel("EMAIL_FALHOU", "Não foi possível enviar o e-mail agora. Tente novamente.");
        }
    }

    public static ApiException indisponivel() {
        return ApiException.indisponivel("RECUPERACAO_INDISPONIVEL",
                "Recuperação de senha indisponível no momento. Procure o atendimento da lavanderia.");
    }
}
