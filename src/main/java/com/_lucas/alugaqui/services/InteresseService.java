package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.DTOs.InteresseResponseDTO;
import com._lucas.alugaqui.exceptions.ForbiddenOperationException;
import com._lucas.alugaqui.exceptions.ResourceNotFoundException;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.InteresseRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class InteresseService {

    private final InteresseRepository interesseRepository;
    private final CasaService casaService;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

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

    public Interesse getInteresseEntity (Long id){
        return this.interesseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Interesse", id.toString())
        );
    }

    public InteresseResponseDTO create (InteresseCreateDTO createDTO, String locatarioEmail){
        Usuario locatario = this.usuarioRepository.findUsuarioByEmail(locatarioEmail);

        if (locatario == null) {
            throw new ResourceNotFoundException("Usuário", locatarioEmail);
        }

        if (locatario.getRole() != Role.LOCATARIO) {
            throw new ForbiddenOperationException("Somente um LOCATARIO pode registrar interesse em uma casa.");
        }

        Casa casa = this.casaService.getCasaEntity(createDTO.getCasaId());

        Interesse interesse = new Interesse(
                casa,
                locatario,
                StatusInteresse.ATIVO,
                LocalDateTime.now()
        );

        interesse = interesseRepository.save(interesse);
        return modelMapper.map(interesse, InteresseResponseDTO.class);
    }

    public InteresseResponseDTO get (Long id){
        Interesse interesse = getInteresseEntity(id);
        return modelMapper.map(interesse, InteresseResponseDTO.class);
    }

    public Page<InteresseResponseDTO> getAll(String userEmail, StatusInteresse status, Pageable pageable) {
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        Page<Interesse> interesses;
        if (user.getRole() == Role.LOCATARIO) {
            interesses = this.interesseRepository.findAllByLocatarioAndOptionalFilters(user, status, pageable);
        }
        else if (user.getRole() == Role.LOCADOR) {
            interesses = this.interesseRepository.findAllByCasaLocadorAndOptionalFilters(user, status, pageable);
        }
        else {
            interesses = Page.empty(pageable);
        }

        return interesses.map(interesse -> modelMapper.map(interesse, InteresseResponseDTO.class));
    }

    public InteresseResponseDTO update (Long id, InteresseUpdateDTO updateDTO, String userEmail) {
        Interesse interesse = this.getInteresseEntity(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if(user == null)
            throw new ResourceNotFoundException("Usuário", userEmail);

        if (!interesse.getCasa().getLocador().getEmail().equals(userEmail) || user.getRole() != Role.LOCADOR) {
            throw new ForbiddenOperationException("Somente o locador da casa pode atualizar o status do interesse.");
        }

        if(updateDTO.getStatus() != null)
            interesse.setStatus(updateDTO.getStatus());

        return modelMapper.map(this.interesseRepository.save(interesse), InteresseResponseDTO.class);
    }

    public void delete (Long id, String userEmail) {
        Interesse interesse = this.getInteresseEntity(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null)
            throw new ResourceNotFoundException("Usuário", userEmail);

        boolean isLocatario = interesse.getLocatario().getEmail().equals(userEmail);
        boolean isLocador = interesse.getCasa().getLocador().getEmail().equals(userEmail);

        if (!isLocatario && !isLocador) {
            throw new ForbiddenOperationException("Você não tem permissão para excluir este interesse.");
        }

        this.interesseRepository.deleteById(id);
    }
}