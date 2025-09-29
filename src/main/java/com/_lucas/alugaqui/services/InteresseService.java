package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

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

    public Interesse create (InteresseCreateDTO createDTO, String locatarioEmail){
        Usuario locatario = this.usuarioRepository.findUsuarioByEmail(locatarioEmail);

        if (locatario == null || locatario.getRole() != Role.LOCATARIO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente um LOCATARIO pode registrar interesse em uma casa.");
        }

        try{
            Casa casa = this.casaService.get(createDTO.getCasaId());

            if (casa == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Casa não encontrada.");

            // O locatario é pego do JWT
            Interesse interesse = new Interesse(
                    casa,
                    locatario,
                    StatusInteresse.ATIVO, // Forçando o status para ATIVO na criação
                    LocalDateTime.now()
            );

            return interesseRepository.save(interesse);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao registrar interesse: " + e.getMessage());
        }
    }

    public Interesse get (Long id){
        return this.interesseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interesse não encontrado.")
        );
    }

    public Collection<Interesse> getAll(String userEmail) {
        try {
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            // LOCATARIO: Vê apenas os interesses que ele criou
            if (user.getRole() == Role.LOCATARIO) {
                return this.interesseRepository.findAllByLocatario(user);
            }

            // LOCADOR: Vê apenas os interesses nas casas que ele possui
            if (user.getRole() == Role.LOCADOR) {
                return this.interesseRepository.findAllByCasa_Locador(user);
            }

            return Collections.emptyList();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar interesses: " + e.getMessage());
        }
    }

    // Apenas o Locador da casa pode alterar o status de um Interesse (Aceitar/Recusar)
    public Interesse update (Long id, InteresseUpdateDTO updateDTO, String userEmail) {
        try{
            Interesse interesse = this.get(id);
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if(user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

            // Verificação de permissão: Apenas o locador da casa pode atualizar o interesse
            if (!interesse.getCasa().getLocador().getEmail().equals(userEmail) || user.getRole() != Role.LOCADOR) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o locador da casa pode atualizar o status do interesse.");
            }

            if(updateDTO.getStatus() != null)
                interesse.setStatus(updateDTO.getStatus());

            return this.interesseRepository.save(interesse);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar interesse: " + e.getMessage());
        }
    }

    // O locatário que criou pode apagar, e o locador da casa também.
    public void delete (Long id, String userEmail) {
        Interesse interesse = this.get(id);
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");

        // Permissão: Locatário que criou OU Locador da casa
        boolean isLocatario = interesse.getLocatario().getEmail().equals(userEmail);
        boolean isLocador = interesse.getCasa().getLocador().getEmail().equals(userEmail);

        if (!isLocatario && !isLocador) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este interesse.");
        }

        this.interesseRepository.deleteById(id);
    }
}