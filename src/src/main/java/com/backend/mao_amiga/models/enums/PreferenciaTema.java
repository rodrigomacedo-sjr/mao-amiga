package com.backend.mao_amiga.models.enums;

public enum PreferenciaTema {
    CLARO("Tema Claro"),
    ESCURO("Tema Escuro");

    private final String descricao;

    PreferenciaTema(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
