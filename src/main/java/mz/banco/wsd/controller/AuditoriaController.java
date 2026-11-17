package mz.banco.wsd.controller;

import mz.banco.wsd.entity.Auditoria;
import mz.banco.wsd.repository.AuditoriaRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping
    public List<Auditoria> listarAuditoria() {
        return auditoriaRepository.findAll();
    }
}