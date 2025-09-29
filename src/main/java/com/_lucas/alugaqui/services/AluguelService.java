package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO; // Adicionado
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.AluguelRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper; // Adicionado

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors; // Adicionado

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CasaService casaService;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper; // Adicionado

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

    // Método auxiliar
    public Aluguel getAluguelEntity(Long id){
        return this.aluguelRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluguel não encontrado.")
        );
    }

    public AluguelResponseDTO create(AluguelCreateDTO createDTO, String userEmail){ // Tipo de retorno alterado
        try{
            // ... (Lógica de criação inalterada)
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
            // Uso de ModelMapper
            return modelMapper.map(novoAluguel, AluguelResponseDTO.class);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar aluguel: " + e.getMessage());
        }
    }

    public AluguelResponseDTO get(Long id){ // Tipo de retorno alterado
        Aluguel aluguel = getAluguelEntity(id);
        // Uso de ModelMapper
        return modelMapper.map(aluguel, AluguelResponseDTO.class);
    }

    public Collection<AluguelResponseDTO> getAll(String userEmail){ // Tipo de retorno alterado
        try {
            // ... (Lógica de busca inalterada)
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            Collection<Aluguel> alugueis = this.aluguelRepository.findAllByLocadorOrLocatario(user, user);

            // Uso de ModelMapper para mapear a coleção
            return alugueis.stream()
                    .map(aluguel -> modelMapper.map(aluguel, AluguelResponseDTO.class))
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar aluguéis: " + e.getMessage());
        }
    }

    public AluguelResponseDTO update(Long id, AluguelUpdateDTO updateDTO, String userEmail){ // Tipo de retorno alterado
        try{
            Aluguel aluguel = this.getAluguelEntity(id);
            // ... (Lógica de atualização de campos inalterada)
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

            // Uso de ModelMapper
            return modelMapper.map(this.aluguelRepository.save(aluguel), AluguelResponseDTO.class);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar aluguel: " + e.getMessage());
        }
    }

    // ... (Lógica de delete inalterada)
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