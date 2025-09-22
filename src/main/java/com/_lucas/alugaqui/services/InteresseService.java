package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.InteresseRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class InteresseService {

    private final InteresseRepository interesseRepository;
    private final CasaService casaService;
    private final UsuarioRepository usuarioRepository;

    public InteresseService(
            InteresseRepository interesseRepository,
            CasaService casaService,
            UsuarioRepository usuarioRepository
    ){
        this.interesseRepository = interesseRepository;
        this.casaService = casaService;
        this.usuarioRepository = usuarioRepository;
    }

    public Interesse create (InteresseCreateDTO createDTO){
        try{

            Casa casa = this.casaService.get(createDTO.getCasaId());
            Usuario locatario = this.usuarioRepository.findById(createDTO.getLocatarioId()).orElse(null);

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
