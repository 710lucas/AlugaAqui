package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.exceptions.ForbiddenOperationException;
import com._lucas.alugaqui.exceptions.ResourceNotFoundException;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.AluguelRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CasaService casaService;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public AluguelService(
            AluguelRepository aluguelRepository,
            CasaService casaService,
            UsuarioRepository usuarioRepository,
            ModelMapper modelMapper
    ){
        this.aluguelRepository = aluguelRepository;
        this.casaService = casaService;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    public boolean isParticipant(Long aluguelId) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Aluguel aluguel = getAluguelEntity(aluguelId);
        return aluguel.getLocador().getEmail().equals(authenticatedEmail) || aluguel.getLocatario().getEmail().equals(authenticatedEmail);
    }

    public boolean isLocador(Long aluguelId) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Aluguel aluguel = getAluguelEntity(aluguelId);
        return aluguel.getLocador().getEmail().equals(authenticatedEmail);
    }

    public Aluguel getAluguelEntity(Long id){
        return this.aluguelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Aluguel", id.toString())
        );
    }

    @PreAuthorize("hasRole('LOCADOR') and @casaService.isLocadorOfCasa(#createDTO.casaId)")
    public AluguelResponseDTO create(AluguelCreateDTO createDTO, String userEmail){
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(userEmail); // Locador é o usuário autenticado

        if (locador == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        Optional<Usuario> locatarioOpt = this.usuarioRepository.findById(createDTO.getLocatarioId());
        Casa casa = this.casaService.getCasaEntity(createDTO.getCasaId());

        Usuario locatario = locatarioOpt.orElseThrow(() -> new ResourceNotFoundException("Locatário", createDTO.getLocatarioId().toString()));

        if (casa.getStatus() != Status.DISPONIVEL) {
            throw new ForbiddenOperationException("Não é possível alugar uma casa com status: " + casa.getStatus());
        }

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

        novoAluguel = this.aluguelRepository.save(novoAluguel);

        locador.getLocadorAlugueis().add(novoAluguel);
        this.usuarioRepository.save(locador);

        locatario.getLocatarioAlugueis().add(novoAluguel);
        this.usuarioRepository.save(locatario);

        return modelMapper.map(novoAluguel, AluguelResponseDTO.class);
    }

    @PreAuthorize("@aluguelService.isParticipant(#id)")
    public AluguelResponseDTO get(Long id){
        Aluguel aluguel = getAluguelEntity(id);
        return modelMapper.map(aluguel, AluguelResponseDTO.class);
    }

    public Page<AluguelResponseDTO> getAll(String userEmail, StatusAluguel status, Pageable pageable){
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        Page<Aluguel> alugueis = this.aluguelRepository.findAllByLocadorOrLocatarioAndOptionalFilters(user, user, status, pageable);

        return alugueis.map(aluguel -> modelMapper.map(aluguel, AluguelResponseDTO.class));
    }

    // PATCH: Somente Locador pode editar o SEU aluguel
    @PreAuthorize("hasRole('LOCADOR') and @aluguelService.isLocador(#id)")
    public AluguelResponseDTO update(Long id, AluguelUpdateDTO updateDTO, String userEmail){
        Aluguel aluguel = this.getAluguelEntity(id);

        if (updateDTO.getDataFim() != null)
            aluguel.setDataFim(updateDTO.getDataFim());

        if (updateDTO.getStatus() != null)
            aluguel.setStatus(updateDTO.getStatus());

        if (updateDTO.getValor() != null)
            aluguel.setValor(updateDTO.getValor());

        return modelMapper.map(this.aluguelRepository.save(aluguel), AluguelResponseDTO.class);
    }

    @PreAuthorize("hasRole('LOCADOR') and @aluguelService.isLocador(#id)")
    public void delete(Long id, String userEmail){
        this.aluguelRepository.deleteById(id);
    }
}