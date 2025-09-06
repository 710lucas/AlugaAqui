package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.AluguelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CasaService casaService;
    private final UsuarioService usuarioService;

    public AluguelService(
            AluguelRepository aluguelRepository,
            CasaService casaService,
            UsuarioService usuarioService
    ){
        this.aluguelRepository = aluguelRepository;
        this.casaService = casaService;
        this.usuarioService = usuarioService;
    }

    public Aluguel create(AluguelCreateDTO createDTO){
        try{

            Usuario locador = this.usuarioService.get(createDTO.getLocadorId());
            Usuario locatario = this.usuarioService.get(createDTO.getLocatarioId());
            Casa casa = this.casaService.get(createDTO.getCasaId());

            if (locatario == null || locador == null || casa == null)
                throw new Exception("Algum dos elementos principais (locador, locatario ou casa) não foi informado");

            Aluguel novoAluguel = new Aluguel(
                    casa,
                    locador,
                    locatario,
                    LocalDate.now(),
                    null,
                    createDTO.getValor(),
                    createDTO.getStatus(),
                    createDTO.getContratoUrl()
            );

            return this.aluguelRepository.save(novoAluguel);

        } catch (Exception e) {
            return null;
        }
    }

    public Aluguel get(Long id){
        return this.aluguelRepository.findById(id).orElseThrow();
    }

    public Collection<Aluguel> getAll(){
        return this.aluguelRepository.findAll();
    }

    public Aluguel update(Long id, AluguelUpdateDTO updateDTO){
        try{

            Aluguel aluguel = this.get(id);

            if (aluguel == null)
                throw new Exception("O aluguel com id " + id + " não foi encontrado");

            if (updateDTO.getDataFim() != null)
                aluguel.setDataFim(updateDTO.getDataFim());

            if (updateDTO.getStatus() != null)
                aluguel.setStatus(updateDTO.getStatus());

            if (updateDTO.getValor() != null)
                aluguel.setValor(updateDTO.getValor());

            return this.aluguelRepository.save(aluguel);

        } catch (Exception e){
            return null;
        }
    }

    public void delete(Long id){
        this.aluguelRepository.deleteById(id);
    }

}
