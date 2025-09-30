package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.exceptions.ResourceNotFoundException;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
    }

    public Usuario getUsuarioEntity(Long id){
        return this.usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário", id.toString())
        );
    }

    public UsuarioResponseDTO create(UsuarioCreateDTO createDTO){
        String senha = bCryptPasswordEncoder.encode(createDTO.getSenha());

        Usuario novoUsuario = new Usuario(
                createDTO.getNome(),
                createDTO.getEmail(),
                createDTO.getTelefone(),
                createDTO.getRole(),
                senha
        );

        novoUsuario = this.usuarioRepository.save(novoUsuario);
        return modelMapper.map(novoUsuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO get(Long id){
        Usuario usuario = getUsuarioEntity(id);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public Collection<UsuarioResponseDTO> getAll(){
        Collection<Usuario> usuarios = this.usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO updateDTO){
        Usuario usuario = getUsuarioEntity(id);

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

        return modelMapper.map(this.usuarioRepository.save(usuario), UsuarioResponseDTO.class);
    }

    public boolean delete (Long id){
        Usuario usuario = getUsuarioEntity(id);
        this.usuarioRepository.deleteById(id);
        return true;
    }
}