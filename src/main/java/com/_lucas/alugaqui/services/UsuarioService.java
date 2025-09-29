package com._lucas.alugaqui.services;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper; // Adicionado

import java.util.Collection;
import java.util.stream.Collectors; // Adicionado

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper; // Adicionado

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
    }

    public UsuarioResponseDTO create(UsuarioCreateDTO createDTO){
        try{
            // ... (Lógica de criação inalterada)
            String senha = bCryptPasswordEncoder.encode(createDTO.getSenha());

            Usuario novoUsuario = new Usuario(
                    createDTO.getNome(),
                    createDTO.getEmail(),
                    createDTO.getTelefone(),
                    createDTO.getRole(),
                    senha
            );

            novoUsuario = this.usuarioRepository.save(novoUsuario);
            // Uso de ModelMapper
            return modelMapper.map(novoUsuario, UsuarioResponseDTO.class);
        } catch (Exception error){
            return null;
        }
    }

    public UsuarioResponseDTO get(Long id){
        Usuario usuario = this.usuarioRepository.findById(id).orElse(null);
        // Uso de ModelMapper
        return (usuario != null) ? modelMapper.map(usuario, UsuarioResponseDTO.class) : null;
    }

    public Collection<UsuarioResponseDTO> getAll(){
        Collection<Usuario> usuarios = this.usuarioRepository.findAll();

        // Uso de ModelMapper para mapear a coleção
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO updateDTO){
        try{
            // ... (Lógica de update inalterada)
            Usuario usuario = this.usuarioRepository.findById(id).orElse(null);

            if (usuario == null)
                throw new Exception("Usuário não encontrado no sistema.");
            // ... (Lógica de atualização de campos)

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

            // Uso de ModelMapper
            return modelMapper.map(this.usuarioRepository.save(usuario), UsuarioResponseDTO.class);

        } catch (Exception e){
            return null;
        }
    }

    public boolean delete (Long id){
        this.usuarioRepository.deleteById(id);
        return true;
    }
}