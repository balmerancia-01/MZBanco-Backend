package mz.banco.wsd.service;

import mz.banco.wsd.entity.Auditoria;
import mz.banco.wsd.repository.AuditoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registarAccao(
            Integer idFuncionario,
            Integer idCliente,
            String accao) {

        Auditoria auditoria = new Auditoria();

        auditoria.setIdFuncionario(idFuncionario);
        auditoria.setIdCliente(idCliente);
        auditoria.setAccao(accao);
        auditoria.setDataHora(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
    }
}
