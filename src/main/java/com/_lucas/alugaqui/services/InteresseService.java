package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.DTOs.InteresseResponseDTO; // Adicionado
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.InteresseRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper; // Adicionado

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors; // Adicionado

@Service
public class InteresseService {

    private final InteresseRepository interesseRepository;
    private final CasaService casaService;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper; // Adicionado

    public InteresseService(
            InteresseRepository interesseRepository,
            CasaService casaService,
            UsuarioRepository usuarioRepository,
            ModelMapper modelMapper
    ){
        this.interesseRepository = interesseRepository;
        this.casaService = casaService;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    // Método auxiliar
    public Interesse getInteresseEntity (Long id){
        return this.interesseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interesse não encontrado.")
        );
    }

    public InteresseResponseDTO create (InteresseCreateDTO createDTO, String locatarioEmail){ // Tipo de retorno alterado
        // ... (Lógica de criação inalterada)
        Usuario locatario = this.usuarioRepository.findUsuarioByEmail(locatarioEmail);

        if (locatario == null || locatario.getRole() != Role.LOCATARIO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente um LOCATARIO pode registrar interesse em uma casa.");
        }

        try{
            Casa casa = this.casaService.getCasaEntity(createDTO.getCasaId());

            if (casa == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Casa não encontrada.");

            Interesse interesse = new Interesse(
                    casa,
                    locatario,
                    StatusInteresse.ATIVO,
                    LocalDateTime.now()
            );

            interesse = interesseRepository.save(interesse);
            // Uso de ModelMapper
            return modelMapper.map(interesse, InteresseResponseDTO.class);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao registrar interesse: " + e.getMessage());
        }
    }

    public InteresseResponseDTO get (Long id){ // Tipo de retorno alterado
        Interesse interesse = getInteresseEntity(id);
        // Uso de ModelMapper
        return modelMapper.map(interesse, InteresseResponseDTO.class);
    }

    public Collection<InteresseResponseDTO> getAll(String userEmail) { // Tipo de retorno alterado
        try {
            // ... (Lógica de busca inalterada)
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            Collection<Interesse> interesses;
            if (user.getRole() == Role.LOCATARIO) {
                interesses = this.interesseRepository.findAllByLocatario(user);
            }
            else if (user.getRole() == Role.LOCADOR) {
                interesses = this.interesseRepository.findAllByCasa_Locador(user);
            }
            else {
                interesses = Collections.emptyList();
            }

            // Uso de ModelMapper para mapear a coleção
            return interesses.stream()
                    .map(interesse -> modelMapper.map(interesse, InteresseResponseDTO.class))
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar interesses: " + e.getMessage());
        }
    }

    public InteresseResponseDTO update (Long id, InteresseUpdateDTO updateDTO, String userEmail) { // Tipo de retorno alterado
        try{
            // ... (Lógica de atualização inalterada)
            Interesse interesse = this.getInteresseEntity(id);
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if(user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

            if (!interesse.getCasa().getLocador().getEmail().equals(userEmail) || user.getRole() != Role.LOCADOR) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o locador da casa pode atualizar o status do interesse.");
            }

            if(updateDTO.getStatus() != null)
                interesse.setStatus(updateDTO.getStatus());

            // Uso de ModelMapper
            return modelMapper.map(this.interesseRepository.save(interesse), InteresseResponseDTO.class);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar interesse: " + e.getMessage());
        }
    }

    // ... (Lógica de delete inalterada)
    public void delete (Long id, String userEmail) {
        Interesse interesse = this.getInteresseEntity(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

        boolean isLocatario = interesse.getLocatario().getEmail().equals(userEmail);
        boolean isLocador = interesse.getCasa().getLocador().getEmail().equals(userEmail);

        if (!isLocatario && !isLocador) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este interesse.");
        }

        this.interesseRepository.deleteById(id);
    }
}