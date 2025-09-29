package com._lucas.alugaqui;

import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO;
import com._lucas.alugaqui.DTOs.InteresseResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies; // Adicionado
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class AlugaAquiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlugaAquiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		// Configurações gerais para ModelMapper
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setAmbiguityIgnored(true);

		// 1. Usuario -> UsuarioResponseDTO
		modelMapper.addMappings(new PropertyMap<Usuario, UsuarioResponseDTO>() {
			@Override
			protected void configure() {
				// Mapeamento de coleções para List<Long> usando with().map() para evitar ConfigurationException.
				using(context -> ((List<Casa>) context.getSource()).stream().map(Casa::getId).collect(Collectors.toList()))
						.map(source.getCasas(), destination.getCasasIds());

				using(context -> ((List<Aluguel>) context.getSource()).stream().map(Aluguel::getId).collect(Collectors.toList()))
						.map(source.getLocadorAlugueis(), destination.getLocadorAlugueisIds());

				using(context -> ((List<Interesse>) context.getSource()).stream().map(Interesse::getId).collect(Collectors.toList()))
						.map(source.getInteresses(), destination.getInteressesIds());

				// Mapeamento de entidade 1:1 (Aluguel) para Long (ID)
				map(source.getLocatarioAluguel() != null ? source.getLocatarioAluguel().getId() : null, destination.getLocatarioAluguelId());
			}
		});

		// 2. Casa -> CasaResponseDTO
		modelMapper.addMappings(new PropertyMap<Casa, CasaResponseDTO>() {
			@Override
			protected void configure() {
				// Mapeamento de coleções para List<Long>
				using(context -> ((List<Aluguel>) context.getSource()).stream().map(Aluguel::getId).collect(Collectors.toList()))
						.map(source.getAlugueis(), destination.getAlugueisIds());

				using(context -> ((List<Interesse>) context.getSource()).stream().map(Interesse::getId).collect(Collectors.toList()))
						.map(source.getInteresses(), destination.getInteressesIds());
			}
		});

		// 3. Aluguel -> AluguelResponseDTO (Mapeia a entidade Casa para CasaId)
		modelMapper.addMappings(new PropertyMap<Aluguel, AluguelResponseDTO>() {
			@Override
			protected void configure() {
				map(source.getCasa().getId(), destination.getCasaId());
			}
		});

		// 4. Interesse -> InteresseResponseDTO (Mapeia a entidade Casa para CasaId)
		modelMapper.addMappings(new PropertyMap<Interesse, InteresseResponseDTO>() {
			@Override
			protected void configure() {
				map(source.getCasa().getId(), destination.getCasaId());
			}
		});

		return modelMapper;
	}
}