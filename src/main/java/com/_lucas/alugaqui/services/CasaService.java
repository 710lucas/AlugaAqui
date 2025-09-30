package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO;
import com._lucas.alugaqui.exceptions.ForbiddenOperationException;
import com._lucas.alugaqui.exceptions.ResourceNotFoundException;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.CasaRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

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
                () -> new ResourceNotFoundException("Casa", id.toString())
        );
    }

    public CasaResponseDTO create(CasaCreateDTO createDTO, String locadorEmail){
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        if (locador == null) {
            throw new ResourceNotFoundException("Usuário", locadorEmail);
        }

        if (locador.getRole() != Role.LOCADOR) {
            throw new ForbiddenOperationException("Somente um LOCADOR pode cadastrar uma casa.");
        }

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
    }

    public CasaResponseDTO get(Long id){
        Casa casa = getCasaEntity(id);
        return modelMapper.map(casa, CasaResponseDTO.class);
    }

    public Page<CasaResponseDTO> getAll(String userEmail, String tipo, Integer minQuartos, Status status, Pageable pageable){
        Usuario user = this.usuarioRepository.findUsuarioByEmail(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário", userEmail);
        }

        Page<Casa> casas;
        if (user.getRole() == Role.LOCADOR) {
            casas = this.casaRepository.findAllByLocadorAndOptionalFilters(user, tipo, minQuartos, status, pageable);
        }
        else {
            casas = this.casaRepository.findAllByOptionalFilters(tipo, minQuartos, status, pageable);
        }

        return casas.map(casa -> modelMapper.map(casa, CasaResponseDTO.class));
    }

    public CasaResponseDTO update(Long id, CasaUpdateDTO updateDTO, String locadorEmail){
        Casa casa = this.getCasaEntity(id);
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        if (locador == null) {
            throw new ResourceNotFoundException("Usuário", locadorEmail);
        }

        if (!casa.getLocador().getEmail().equals(locadorEmail)) {
            throw new ForbiddenOperationException("Você não tem permissão para editar esta casa.");
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
    }

    public void delete(Long id, String locadorEmail){
        Casa casa = this.getCasaEntity(id);
        Usuario locador = this.usuarioRepository.findUsuarioByEmail(locadorEmail);

        if (locador == null) {
            throw new ResourceNotFoundException("Usuário", locadorEmail);
        }

        if (!casa.getLocador().getEmail().equals(locadorEmail)) {
            throw new ForbiddenOperationException("Você não tem permissão para excluir esta casa.");
        }

        this.casaRepository.deleteById(id);
    }
}