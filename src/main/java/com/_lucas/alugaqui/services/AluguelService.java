package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.AluguelRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Collection;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluguel não encontrado.")
        );
    }

    public AluguelResponseDTO create(AluguelCreateDTO createDTO, String userEmail){
        try{
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null || user.getRole() != Role.LOCADOR) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o LOCADOR pode criar um contrato de aluguel.");
            }

            Usuario locador = this.usuarioRepository.findById(createDTO.getLocadorId()).orElse(null);
            Usuario locatario = this.usuarioRepository.findById(createDTO.getLocatarioId()).orElse(null);
            Casa casa = this.casaService.getCasaEntity(createDTO.getCasaId());

            if (locatario == null || locador == null || casa == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Um dos elementos principais (locador, locatario ou casa) não foi encontrado.");

            if (!locador.getEmail().equals(userEmail) || !casa.getLocador().getId().equals(locador.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O locador autenticado não é o dono da casa ou o locador especificado no contrato.");
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

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar aluguel: " + e.getMessage());
        }
    }

    public AluguelResponseDTO get(Long id){
        Aluguel aluguel = getAluguelEntity(id);
        return modelMapper.map(aluguel, AluguelResponseDTO.class);
    }

    public Page<AluguelResponseDTO> getAll(String userEmail, StatusAluguel status, Pageable pageable){
        try {
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            Page<Aluguel> alugueis = this.aluguelRepository.findAllByLocadorOrLocatarioAndOptionalFilters(user, user, status, pageable);

            return alugueis.map(aluguel -> modelMapper.map(aluguel, AluguelResponseDTO.class));

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar aluguéis: " + e.getMessage());
        }
    }

    public AluguelResponseDTO update(Long id, AluguelUpdateDTO updateDTO, String userEmail){
        try{
            Aluguel aluguel = this.getAluguelEntity(id);
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

            boolean isLocador = aluguel.getLocador().getEmail().equals(userEmail);
            boolean isLocatario = aluguel.getLocatario().getEmail().equals(userEmail);

            if (!isLocador && !isLocatario) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para atualizar este aluguel.");
            }

            if (updateDTO.getDataFim() != null)
                aluguel.setDataFim(updateDTO.getDataFim());

            if (updateDTO.getStatus() != null)
                aluguel.setStatus(updateDTO.getStatus());

            if (updateDTO.getValor() != null)
                aluguel.setValor(updateDTO.getValor());

            return modelMapper.map(this.aluguelRepository.save(aluguel), AluguelResponseDTO.class);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar aluguel: " + e.getMessage());
        }
    }

    public void delete(Long id, String userEmail){
        Aluguel aluguel = this.getAluguelEntity(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

        if (!aluguel.getLocador().getEmail().equals(userEmail) || user.getRole() != Role.LOCADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este aluguel.");
        }

        this.aluguelRepository.deleteById(id);
    }
}