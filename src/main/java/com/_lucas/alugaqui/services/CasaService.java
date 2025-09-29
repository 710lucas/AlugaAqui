package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO; // Adicionado
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.CasaRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper; // Adicionado

import java.util.Collection;
import java.util.stream.Collectors; // Adicionado

@Service
public class CasaService {

    private final CasaRepository casaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper; // Adicionado

    public CasaService (CasaRepository casaRepository, UsuarioRepository usuarioRepository, ModelMapper modelMapper){
        this.casaRepository = casaRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    // Método auxiliar para uso interno, retorna a entidade Casa.
    public Casa getCasaEntity(Long id){
        return this.casaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casa não encontrada.")
        );
    }

    public CasaResponseDTO create(CasaCreateDTO createDTO, String locadorEmail){ // Tipo de retorno alterado
        // ... (Lógica de permissão inalterada)
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
            // Uso de ModelMapper
            return modelMapper.map(novaCasa, CasaResponseDTO.class);
        } catch(ResponseStatusException e){
            throw e;
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar casa: " + e.getMessage());
        }
    }

    public CasaResponseDTO get(Long id){ // Tipo de retorno alterado
        Casa casa = getCasaEntity(id);
        // Uso de ModelMapper
        return modelMapper.map(casa, CasaResponseDTO.class);
    }

    public Collection<CasaResponseDTO> getAll(String userEmail){ // Tipo de retorno alterado
        try{
            // ... (Lógica de busca inalterada)
            Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }

            Collection<Casa> casas;
            if (user.getRole() == Role.LOCADOR) {
                casas = this.casaRepository.findAllByLocador(user);
            }
            else {
                casas = this.casaRepository.findAll();
            }

            // Uso de ModelMapper para mapear a coleção
            return casas.stream()
                    .map(casa -> modelMapper.map(casa, CasaResponseDTO.class))
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar casas: " + e.getMessage());
        }
    }

    public CasaResponseDTO update(Long id, CasaUpdateDTO updateDTO, String locadorEmail){ // Tipo de retorno alterado
        try{
            Casa casa = this.getCasaEntity(id);
            // ... (Lógica de permissão e atualização de campos inalterada)
            Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

            if (locador == null || !casa.getLocador().getEmail().equals(locadorEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para editar esta casa.");
            }

            if(updateDTO.isMobiliada() != null)
                casa.setMobiliada(updateDTO.isMobiliada());
            // ... (outras atualizações)
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

            // Uso de ModelMapper
            return modelMapper.map(this.casaRepository.save(casa), CasaResponseDTO.class);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar casa: " + e.getMessage());
        }
    }

    public void delete(Long id, String locadorEmail){
        // ... (Lógica de delete inalterada)
        Casa casa = this.getCasaEntity(id);
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        if (locador == null || !casa.getLocador().getEmail().equals(locadorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir esta casa.");
        }

        this.casaRepository.deleteById(id);
    }

}