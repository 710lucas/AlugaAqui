package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.AluguelRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CasaService casaService;
    private final UsuarioRepository usuarioRepository;

    public AluguelService(
            AluguelRepository aluguelRepository,
            CasaService casaService,
            UsuarioRepository usuarioRepository
    ){
        this.aluguelRepository = aluguelRepository;
        this.casaService = casaService;
        this.usuarioRepository = usuarioRepository;
    }

    public Aluguel create(AluguelCreateDTO createDTO, String userEmail){
        try{
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            // Verifica se o usuário autenticado é um LOCADOR
            if (user == null || user.getRole() != Role.LOCADOR) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o LOCADOR pode criar um contrato de aluguel.");
            }

            Usuario locador = this.usuarioRepository.findById(createDTO.getLocadorId()).orElse(null);
            Usuario locatario = this.usuarioRepository.findById(createDTO.getLocatarioId()).orElse(null);
            Casa casa = this.casaService.get(createDTO.getCasaId());

            if (locatario == null || locador == null || casa == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Um dos elementos principais (locador, locatario ou casa) não foi encontrado.");

            // Adicional: O Locador autenticado deve ser o dono da casa e o locador especificado no DTO.
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

            return this.aluguelRepository.save(novoAluguel);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar aluguel: " + e.getMessage());
        }
    }

    public Aluguel get(Long id){
        return this.aluguelRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluguel não encontrado.")
        );
    }

    public Collection<Aluguel> getAll(String userEmail){
        try {
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            // Retorna aluguéis onde o usuário é o Locador OU o Locatário
            return this.aluguelRepository.findAllByLocadorOrLocatario(user, user);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar aluguéis: " + e.getMessage());
        }
    }

    public Aluguel update(Long id, AluguelUpdateDTO updateDTO, String userEmail){
        try{
            Aluguel aluguel = this.get(id);
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

            // Verificação de permissão: Apenas o locador ou o locatário do contrato podem atualizar.
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

            return this.aluguelRepository.save(aluguel);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar aluguel: " + e.getMessage());
        }
    }

    public void delete(Long id, String userEmail){
        Aluguel aluguel = this.get(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

        // Verificação de permissão: Apenas o locador pode deletar o registro do aluguel
        if (!aluguel.getLocador().getEmail().equals(userEmail) || user.getRole() != Role.LOCADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este aluguel.");
        }

        this.aluguelRepository.deleteById(id);
    }

}