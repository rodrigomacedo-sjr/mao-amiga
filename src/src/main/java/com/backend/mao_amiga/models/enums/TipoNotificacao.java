package com.backend.mao_amiga.models.enums;

public enum TipoNotificacao {
    NOVA_AVALIACAO("Nova avaliação recebida"),
    EVENTO_CRIADO("Novo evento criado"),
    INSCRICAO_EVENTO("Inscrição em evento"),
    EVENTO_CANCELADO("Evento cancelado"),
    LEMBRETE_EVENTO("Lembrete de evento"),
    NOVO_SEGUIDOR("Novo seguidor"),
    NOVO_POST("Nova postagem"),
    CURTIDA_POST("Curtida em post"),
    COMENTARIO_POST("Novo comentário");

    private final String descricao;

    TipoNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
