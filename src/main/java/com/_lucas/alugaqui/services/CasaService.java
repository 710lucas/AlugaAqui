package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.CasaRepository;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CasaService {

    private final CasaRepository casaRepository;
    private final UsuarioRepository usuarioRepository;

    public CasaService (CasaRepository casaRepository, UsuarioRepository usuarioRepository){
        this.casaRepository = casaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Casa create(CasaCreateDTO createDTO){
        try{
            Usuario locador = this.usuarioRepository.findById(createDTO.getLocadorId()).orElseThrow();

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
            return null;
        }
    }

    public Casa get(Long id){
        try{
            return this.casaRepository.findById(id).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<Casa> getAll(){
        try{
            return this.casaRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    public Casa update(Long id, CasaUpdateDTO updateDTO){
        try{

            Casa casa = this.get(id);

            if(casa == null)
                throw new Exception("A casa informada não existe");

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
                casa.setRestricoes(casa.getRestricoes());

            if(updateDTO.getTipo() != null)
                casa.setTipo(casa.getTipo());

            return this.casaRepository.save(casa);


        } catch (Exception e) {
            return null;
        }
    }

    public void delete(Long id){
        this.casaRepository.deleteById(id);
    }

}
