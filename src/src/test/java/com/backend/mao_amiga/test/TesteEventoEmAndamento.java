package com.backend.mao_amiga.test;

import com.backend.mao_amiga.models.Evento;
import com.backend.mao_amiga.models.enums.StatusEvento;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Teste manual simples para verificar se evento em andamento funciona
 */
public class TesteEventoEmAndamento {
    
    public static void main(String[] args) {
        System.out.println("=== Teste de Evento Em Andamento ===");
        
        // Teste 1: Evento futuro
        Evento eventoFuturo = new Evento(
            "Evento Futuro",
            "Descrição",
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(4),
            UUID.randomUUID(),
            "Local",
            10
        );
        
        System.out.println("Evento Futuro:");
        System.out.println("- Está no futuro: " + eventoFuturo.estaNoFuturo());
        System.out.println("- Está em andamento: " + eventoFuturo.estaEmAndamento());
        System.out.println("- Está finalizado: " + eventoFuturo.estaFinalizado());
        System.out.println("- Status: " + eventoFuturo.getStatus());
        
        // Teste 2: Evento em andamento
        Evento eventoEmAndamento = new Evento(
            "Evento Em Andamento",
            "Descrição",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(2),
            UUID.randomUUID(),
            "Local",
            10
        );
        
        System.out.println("\nEvento Em Andamento:");
        System.out.println("- Está no futuro: " + eventoEmAndamento.estaNoFuturo());
        System.out.println("- Está em andamento: " + eventoEmAndamento.estaEmAndamento());
        System.out.println("- Está finalizado: " + eventoEmAndamento.estaFinalizado());
        System.out.println("- Status: " + eventoEmAndamento.getStatus());
        
        // Teste 3: Evento finalizado
        Evento eventoFinalizado = new Evento(
            "Evento Finalizado",
            "Descrição",
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1),
            UUID.randomUUID(),
            "Local",
            10
        );
        
        System.out.println("\nEvento Finalizado:");
        System.out.println("- Está no futuro: " + eventoFinalizado.estaNoFuturo());
        System.out.println("- Está em andamento: " + eventoFinalizado.estaEmAndamento());
        System.out.println("- Está finalizado: " + eventoFinalizado.estaFinalizado());
        System.out.println("- Status: " + eventoFinalizado.getStatus());
        
        // Teste 4: Mudança de status manual
        System.out.println("\n=== Teste de Mudança de Status ===");
        Evento evento = new Evento(
            "Evento Teste",
            "Descrição",
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(3),
            UUID.randomUUID(),
            "Local",
            10
        );
        
        System.out.println("Status inicial: " + evento.getStatus());
        
        evento.abrirInscricoes();
        System.out.println("Após abrir inscrições: " + evento.getStatus());
        
        evento.fecharInscricoes();
        System.out.println("Após fechar inscrições: " + evento.getStatus());
        
        evento.iniciarEvento();
        System.out.println("Após iniciar evento: " + evento.getStatus());
        
        evento.finalizarEvento();
        System.out.println("Após finalizar evento: " + evento.getStatus());
        
        System.out.println("\n=== Teste Completo ===");
    }
}
