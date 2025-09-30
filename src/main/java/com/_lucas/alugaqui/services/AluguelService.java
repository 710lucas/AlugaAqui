package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.exceptions.ForbiddenOperationException;
import com._lucas.alugaqui.exceptions.ResourceNotFoundException;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.AluguelRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Aluguel getAluguelEntity(Long id){
        return this.aluguelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Aluguel", id.toString())
        );
    }

    public AluguelResponseDTO create(AluguelCreateDTO createDTO, String userEmail){
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        if (user.getRole() != Role.LOCADOR) {
            throw new ForbiddenOperationException("Somente o LOCADOR pode criar um contrato de aluguel.");
        }

        Optional<Usuario> locadorOpt = this.usuarioRepository.findById(createDTO.getLocadorId());
        Optional<Usuario> locatarioOpt = this.usuarioRepository.findById(createDTO.getLocatarioId());
        Casa casa = this.casaService.getCasaEntity(createDTO.getCasaId());

        Usuario locador = locadorOpt.orElseThrow(() -> new ResourceNotFoundException("Locador", createDTO.getLocadorId().toString()));
        Usuario locatario = locatarioOpt.orElseThrow(() -> new ResourceNotFoundException("Locatário", createDTO.getLocatarioId().toString()));


        if (!locador.getEmail().equals(userEmail) || !casa.getLocador().getId().equals(locador.getId())) {
            throw new ForbiddenOperationException("O locador autenticado não é o dono da casa ou o locador especificado no contrato.");
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
        return modelMapper.map(novoAluguel, AluguelResponseDTO.class);
    }

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

    public AluguelResponseDTO update(Long id, AluguelUpdateDTO updateDTO, String userEmail){
        Aluguel aluguel = this.getAluguelEntity(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        boolean isLocador = aluguel.getLocador().getEmail().equals(userEmail);
        boolean isLocatario = aluguel.getLocatario().getEmail().equals(userEmail);

        if (!isLocador && !isLocatario) {
            throw new ForbiddenOperationException("Você não tem permissão para atualizar este aluguel.");
        }

        if (updateDTO.getDataFim() != null)
            aluguel.setDataFim(updateDTO.getDataFim());

        if (updateDTO.getStatus() != null)
            aluguel.setStatus(updateDTO.getStatus());

        if (updateDTO.getValor() != null)
            aluguel.setValor(updateDTO.getValor());

        return modelMapper.map(this.aluguelRepository.save(aluguel), AluguelResponseDTO.class);
    }

    public void delete(Long id, String userEmail){
        Aluguel aluguel = this.getAluguelEntity(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        if (!aluguel.getLocador().getEmail().equals(userEmail) || user.getRole() != Role.LOCADOR) {
            throw new ForbiddenOperationException("Você não tem permissão para excluir este aluguel.");
        }

        this.aluguelRepository.deleteById(id);
    }
}