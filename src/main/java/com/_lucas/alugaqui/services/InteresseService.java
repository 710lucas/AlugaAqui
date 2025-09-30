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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

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

    public boolean isLocatarioOfInteresse(Long interesseId) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Interesse interesse = getInteresseEntity(interesseId);
        return interesse.getLocatario().getEmail().equals(authenticatedEmail);
    }

    public boolean isLocadorOfInteresseCasa(Long interesseId) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Interesse interesse = getInteresseEntity(interesseId);
        return interesse.getCasa().getLocador().getEmail().equals(authenticatedEmail);
    }

    public Interesse getInteresseEntity (Long id){
        return this.interesseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Interesse", id.toString())
        );
    }

    @PreAuthorize("hasRole('LOCATARIO')")
    public InteresseResponseDTO create (InteresseCreateDTO createDTO, String locatarioEmail){
        Usuario locatario = this.usuarioRepository.findUsuarioByEmail(locatarioEmail);

        if (locatario == null) {
            throw new ResourceNotFoundException("Usuário", locatarioEmail);
        }

        Casa casa = this.casaService.getCasaEntity(createDTO.getCasaId());

        if (interesseRepository.existsByLocatarioAndCasaIdAndStatus(locatario, casa.getId(), StatusInteresse.ATIVO)) {
            throw new ForbiddenOperationException("Você já possui um interesse ATIVO para esta casa.");
        }

        Interesse interesse = new Interesse(
                casa,
                locatario,
                StatusInteresse.ATIVO,
                LocalDateTime.now()
        );

        interesse = interesseRepository.save(interesse);
        return modelMapper.map(interesse, InteresseResponseDTO.class);
    }

    @PreAuthorize("@interesseService.isLocatarioOfInteresse(#id) or @interesseService.isLocadorOfInteresseCasa(#id)")
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

    @PreAuthorize("hasRole('LOCATARIO') and @interesseService.isLocatarioOfInteresse(#id)")
    public InteresseResponseDTO update (Long id, InteresseUpdateDTO updateDTO, String userEmail) {
        Interesse interesse = this.getInteresseEntity(id);

        if(updateDTO.getStatus() != null)
            interesse.setStatus(updateDTO.getStatus());

        return modelMapper.map(this.interesseRepository.save(interesse), InteresseResponseDTO.class);
    }

    @PreAuthorize("@interesseService.isLocatarioOfInteresse(#id) or @interesseService.isLocadorOfInteresseCasa(#id)")
    public void delete (Long id, String userEmail) {
        this.interesseRepository.deleteById(id);
    }
}