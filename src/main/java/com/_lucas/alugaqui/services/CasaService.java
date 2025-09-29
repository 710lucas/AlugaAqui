package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.CasaRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CasaService {

    private final CasaRepository casaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public CasaService (CasaRepository casaRepository, UsuarioRepository usuarioRepository, ModelMapper modelMapper){
        this.casaRepository = casaRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    public Casa getCasaEntity(Long id){
        return this.casaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casa não encontrada.")
        );
    }

    public CasaResponseDTO create(CasaCreateDTO createDTO, String locadorEmail){
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

            novaCasa = this.casaRepository.save(novaCasa);
            return modelMapper.map(novaCasa, CasaResponseDTO.class);
        } catch(ResponseStatusException e){
            throw e;
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar casa: " + e.getMessage());
        }
    }

    public CasaResponseDTO get(Long id){
        Casa casa = getCasaEntity(id);
        return modelMapper.map(casa, CasaResponseDTO.class);
    }

    public Page<CasaResponseDTO> getAll(String userEmail, String tipo, Integer minQuartos, Status status, Pageable pageable){
        try{
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            Page<Casa> casas;
            if (user.getRole() == Role.LOCADOR) {
                casas = this.casaRepository.findAllByLocadorAndOptionalFilters(user, tipo, minQuartos, status, pageable);
            }
            else {
                casas = this.casaRepository.findAllByOptionalFilters(tipo, minQuartos, status, pageable);
            }

            return casas.map(casa -> modelMapper.map(casa, CasaResponseDTO.class));

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar casas: " + e.getMessage());
        }
    }

    public CasaResponseDTO update(Long id, CasaUpdateDTO updateDTO, String locadorEmail){
        try{
            Casa casa = this.getCasaEntity(id);
            Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

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
                casa.setRestricoes(updateDTO.getRestricoes());

            if(updateDTO.getTipo() != null)
                casa.setTipo(updateDTO.getTipo());

            return modelMapper.map(this.casaRepository.save(casa), CasaResponseDTO.class);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar casa: " + e.getMessage());
        }
    }

    public void delete(Long id, String locadorEmail){
        Casa casa = this.getCasaEntity(id);
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        if (locador == null || !casa.getLocador().getEmail().equals(locadorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir esta casa.");
        }

        this.casaRepository.deleteById(id);
    }
}