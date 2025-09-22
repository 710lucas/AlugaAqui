package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UsuarioResponseDTO create(UsuarioCreateDTO createDTO){
        try{

            String senha = bCryptPasswordEncoder.encode(createDTO.getSenha());

            Usuario novoUsuario = new Usuario(
                    createDTO.getNome(),
                    createDTO.getEmail(),
                    createDTO.getTelefone(),
                    createDTO.getRole(),
                    senha
            );

            novoUsuario = this.usuarioRepository.save(novoUsuario);
            return UsuarioResponseDTO.fromModel(novoUsuario);
        } catch (Exception error){
            return null;
        }
    }

    public UsuarioResponseDTO get(Long id){
        Usuario usuario = this.usuarioRepository.findById(id).orElse(null);
        return (usuario != null) ? UsuarioResponseDTO.fromModel(usuario) : null;
    }

    public Collection<UsuarioResponseDTO> getAll(){
        Collection<Usuario> usuarios = this.usuarioRepository.findAll();

        Collection<UsuarioResponseDTO> usuariosResponse = new ArrayList<>();

        usuarios.forEach(usuario -> {
            usuariosResponse.add(UsuarioResponseDTO.fromModel(usuario));
        });

        return usuariosResponse;
    }

    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO updateDTO){
        try{

            Usuario usuario = this.usuarioRepository.findById(id).orElse(null);

            if (usuario == null)
                throw new Exception("Usuário não encontrado no sistema.");

            if (updateDTO.getNome() != null)
                usuario.setNome(updateDTO.getNome());

            if (updateDTO.getEmail() != null)
                usuario.setEmail(updateDTO.getEmail());

            if (updateDTO.getTelefone() != null)
                usuario.setTelefone(updateDTO.getTelefone());

            if (updateDTO.getSenha() != null){
                String senha = bCryptPasswordEncoder.encode(updateDTO.getSenha());
                usuario.setSenha(senha);
            }

            return UsuarioResponseDTO.fromModel(this.usuarioRepository.save(usuario));

        } catch (Exception e){
            return null;
        }
    }

    public boolean delete (Long id){
        this.usuarioRepository.deleteById(id);
        return true;
    }
}
