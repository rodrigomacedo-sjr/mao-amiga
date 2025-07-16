package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.enums.AreaInteresse;
import com.backend.mao_amiga.models.enums.PreferenciaTema;
import com.backend.mao_amiga.models.enums.StatusEvento;
import com.backend.mao_amiga.models.enums.TipoNotificacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sistema")
public class SistemaController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> verificarSaude() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        status.put("versao", "1.0.0");
        status.put("servico", "Mão Amiga API");
        
        return ResponseEntity.ok(status);
    }

    @GetMapping("/areas-interesse")
    public ResponseEntity<List<Map<String, String>>> listarAreasInteresse() {
        List<Map<String, String>> areas = Arrays.stream(AreaInteresse.values())
                .map(area -> {
                    Map<String, String> areaMap = new HashMap<>();
                    areaMap.put("codigo", area.name());
                    areaMap.put("descricao", area.getDescricao());
                    return areaMap;
                })
                .toList();
        
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/status-evento")
    public ResponseEntity<List<Map<String, String>>> listarStatusEvento() {
        List<Map<String, String>> status = Arrays.stream(StatusEvento.values())
                .map(s -> {
                    Map<String, String> statusMap = new HashMap<>();
                    statusMap.put("codigo", s.name());
                    statusMap.put("descricao", s.getDescricao());
                    return statusMap;
                })
                .toList();
        
        return ResponseEntity.ok(status);
    }

    @GetMapping("/tipos-notificacao")
    public ResponseEntity<List<Map<String, String>>> listarTiposNotificacao() {
        List<Map<String, String>> tipos = Arrays.stream(TipoNotificacao.values())
                .map(tipo -> {
                    Map<String, String> tipoMap = new HashMap<>();
                    tipoMap.put("codigo", tipo.name());
                    tipoMap.put("descricao", tipo.getDescricao());
                    return tipoMap;
                })
                .toList();
        
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/preferencias-tema")
    public ResponseEntity<List<Map<String, String>>> listarPreferenciasTema() {
        List<Map<String, String>> temas = Arrays.stream(PreferenciaTema.values())
                .map(tema -> {
                    Map<String, String> temaMap = new HashMap<>();
                    temaMap.put("codigo", tema.name());
                    temaMap.put("descricao", tema.getDescricao());
                    return temaMap;
                })
                .toList();
        
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> obterInformacoesSistema() {
        Map<String, Object> info = new HashMap<>();
        info.put("nome", "Mão Amiga");
        info.put("descricao", "API para conectar ONGs e Voluntários através de eventos sociais");
        info.put("versao", "1.0.0");
        info.put("endpoints", Map.of(
            "voluntarios", "/api/voluntarios",
            "ongs", "/api/ongs", 
            "eventos", "/api/eventos",
            "posts", "/api/posts",
            "comentarios", "/api/comentarios",
            "notificacoes", "/api/notificacoes",
            "avaliacoes", "/api/avaliacoes"
        ));
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/estatisticas/dashboard")
    public ResponseEntity<Map<String, Object>> obterDashboard() {
        // Em uma implementação real, estes dados viriam dos services
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalUsuarios", 0);
        dashboard.put("totalVoluntarios", 0);
        dashboard.put("totalOngs", 0);
        dashboard.put("totalEventos", 0);
        dashboard.put("totalPosts", 0);
        dashboard.put("totalComentarios", 0);
        dashboard.put("totalAvaliacoes", 0);
        dashboard.put("totalNotificacoes", 0);
        dashboard.put("areasInteresseDisponiveis", AreaInteresse.values().length);
        dashboard.put("statusEventoDisponiveis", StatusEvento.values().length);
        dashboard.put("tiposNotificacaoDisponiveis", TipoNotificacao.values().length);
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/configuracoes")
    public ResponseEntity<Map<String, Object>> obterConfiguracoes() {
        Map<String, Object> config = new HashMap<>();
        config.put("limitePosts", 100);
        config.put("limiteComentarios", 50);
        config.put("limiteNotificacoes", 200);
        config.put("notaMinima", 1.0);
        config.put("notaMaxima", 5.0);
        config.put("tempoSessao", 3600); // segundos
        config.put("tamMaximoConteudoPost", 2000);
        config.put("tamMaximoConteudoComentario", 1000);
        
        return ResponseEntity.ok(config);
    }

    @PostMapping("/limpar-dados-teste")
    public ResponseEntity<Map<String, String>> limparDadosTeste() {
        // Este endpoint seria usado apenas em desenvolvimento para limpar dados de teste
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Dados de teste limpos com sucesso");
        resposta.put("aviso", "Este endpoint só deve ser usado em desenvolvimento");
        
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/versao")
    public ResponseEntity<Map<String, String>> obterVersao() {
        Map<String, String> versao = new HashMap<>();
        versao.put("api", "1.0.0");
        versao.put("java", System.getProperty("java.version"));
        versao.put("spring", "3.x");
        versao.put("build", "2024-12-01");
        
        return ResponseEntity.ok(versao);
    }
}
