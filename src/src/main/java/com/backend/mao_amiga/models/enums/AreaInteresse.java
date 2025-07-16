package com.backend.mao_amiga.models.enums;

public enum AreaInteresse {
    EDUCACAO("Educação"),
    MEIO_AMBIENTE("Meio Ambiente"),
    SAUDE("Saúde"),
    ASSISTENCIA_SOCIAL("Assistência Social"),
    CULTURA("Cultura"),
    ESPORTE("Esporte"),
    DIREITOS_HUMANOS("Direitos Humanos"),
    TECNOLOGIA("Tecnologia"),
    ANIMAIS("Proteção Animal"),
    IDOSOS("Cuidado com Idosos"),
    CRIANCAS("Cuidado com Crianças");

    private final String descricao;

    AreaInteresse(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
