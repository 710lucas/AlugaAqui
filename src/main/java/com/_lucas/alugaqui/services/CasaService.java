package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.CasaRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
public class CasaService {

    private final CasaRepository casaRepository;
    private final UsuarioRepository usuarioRepository;

    public CasaService (CasaRepository casaRepository, UsuarioRepository usuarioRepository){
        this.casaRepository = casaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Casa create(CasaCreateDTO createDTO, String locadorEmail){
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        if (locador == null || locador.getRole() != Role.LOCADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente um LOCADOR pode cadastrar uma casa.");
        }

        try{
            Casa novaCasa = new Casa(
                    locador,
                    createDTO.getNome(),
                    createDTO.getDescricao(),
                    createDTO.getEndereco(),
                    createDTO.getTipo(),
                    createDTO.getQuartos(),
                    createDTO.isMobiliada(),
                    Status.DISPONIVEL,
                    createDTO.getRestricoes()
            );

            return this.casaRepository.save(novaCasa);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar casa: " + e.getMessage());
        }
    }

    public Casa get(Long id){
        return this.casaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casa não encontrada.")
        );
    }

    public Collection<Casa> getAll(String userEmail){
        try{
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            // Se for LOCADOR, retorna apenas as casas que ele cadastrou.
            if (user.getRole() == Role.LOCADOR) {
                return this.casaRepository.findAllByLocador(user);
            }

            // Se for LOCATARIO, o comportamento é retornar todas as casas disponíveis
            // (Assumindo que LOCATARIO pode navegar no catálogo completo).
            return this.casaRepository.findAll();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar casas: " + e.getMessage());
        }
    }

    public Casa update(Long id, CasaUpdateDTO updateDTO, String locadorEmail){
        try{
            Casa casa = this.get(id);
            Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

            // 1. Verificar se o usuário existe e se é o locador (dono) da casa
            if (locador == null || !casa.getLocador().getEmail().equals(locadorEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para editar esta casa.");
            }

            if(updateDTO.isMobiliada() != null)
                casa.setMobiliada(updateDTO.isMobiliada());

            if(updateDTO.getDescricao() != null)
                casa.setDescricao(updateDTO.getDescricao());

            if(updateDTO.getEndereco() != null)
                casa.setEndereco(updateDTO.getEndereco());

            if(updateDTO.getNome() != null)
                casa.setNome(updateDTO.getNome());

            if(updateDTO.getQuartos() != null)
                casa.setQuartos(updateDTO.getQuartos());

            if(updateDTO.getRestricoes() != null)
                casa.setRestricoes(updateDTO.getRestricoes()); // Correção de bug no código original

            if(updateDTO.getTipo() != null)
                casa.setTipo(updateDTO.getTipo()); // Correção de bug no código original

            return this.casaRepository.save(casa);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar casa: " + e.getMessage());
        }
    }

    public void delete(Long id, String locadorEmail){
        Casa casa = this.get(id);
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        // 1. Verificar se o usuário existe e se é o locador (dono) da casa
        if (locador == null || !casa.getLocador().getEmail().equals(locadorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir esta casa.");
        }

        this.casaRepository.deleteById(id);
    }

}