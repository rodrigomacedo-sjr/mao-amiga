package com.backend.mao_amiga.models.enums;

public enum StatusEvento {
    PLANEJADO("Planejado"),
    ABERTO_INSCRICOES("Aberto para Inscrições"),
    INSCRICOES_FECHADAS("Inscrições Fechadas"),
    EM_ANDAMENTO("Em Andamento"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusEvento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
