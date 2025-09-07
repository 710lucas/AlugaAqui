package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.InteresseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class InteresseService {

    private final InteresseRepository interesseRepository;
    private final CasaService casaService;
    private final UsuarioService usuarioService;

    public InteresseService(
            InteresseRepository interesseRepository,
            CasaService casaService,
            UsuarioService usuarioService
    ){
        this.interesseRepository = interesseRepository;
        this.casaService = casaService;
        this.usuarioService = usuarioService;
    }

    public Interesse create (InteresseCreateDTO createDTO){
        try{

            Casa casa = this.casaService.get(createDTO.getCasaId());
            Usuario locatario = this.usuarioService.get(createDTO.getLocatarioId());

            if (casa == null || locatario == null)
                throw new Exception("Um dos dados obrigatórios não foi preenchido, tente novamente");

            Interesse interesse = new Interesse(
                    casa,
                    locatario,
                    createDTO.getStatus(),
                    LocalDateTime.now()
            );


            return interesseRepository.save(interesse);

        } catch (Exception e) {
            return null;
        }
    }

    public Interesse get (Long id){
        return this.interesseRepository.findById(id).orElse(null);
    }

    public Collection<Interesse> getAll () {
        return this.interesseRepository.findAll();
    }

    public Interesse update (Long id, InteresseUpdateDTO updateDTO) {
        try{
            Interesse interesse = this.get(id);

            if(interesse == null)
                throw new Exception("Não foi possível encontrar um interesse com o id: " + id);

            if(updateDTO.getStatus() != null)
                interesse.setStatus(updateDTO.getStatus());

            return this.interesseRepository.save(interesse);
        } catch (Exception e) {
            return null;
        }
    }

    public void delete (Long id) {
        this.interesseRepository.deleteById(id);
    }

}
