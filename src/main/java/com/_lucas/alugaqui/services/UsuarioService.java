package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario create(UsuarioCreateDTO createDTO){
        try{
            Usuario novoUsuario = new Usuario(
                    createDTO.getNome(),
                    createDTO.getEmail(),
                    createDTO.getTelefone(),
                    createDTO.getRole()
            );

            return this.usuarioRepository.save(novoUsuario);
        } catch (Exception error){
            return null;
        }
    }

    public Usuario get(Long id){
        return this.usuarioRepository.findById(id).orElse(null);
    }

    public Collection<Usuario> getAll(){
        return this.usuarioRepository.findAll();
    }

    public Usuario update(Long id, UsuarioUpdateDTO updateDTO){
        try{

            Usuario usuario = this.get(id);

            if (usuario == null)
                throw new Exception("Usuário não encontrado no sistema.");

            if (updateDTO.getNome() != null)
                usuario.setNome(updateDTO.getNome());

            if (updateDTO.getEmail() != null)
                usuario.setEmail(updateDTO.getEmail());

            if (updateDTO.getTelefone() != null)
                usuario.setTelefone(updateDTO.getTelefone());

            return this.usuarioRepository.save(usuario);

        } catch (Exception e){
            return null;
        }
    }

    public boolean delete (Long id){
        this.usuarioRepository.deleteById(id);
        return true;
    }
}
