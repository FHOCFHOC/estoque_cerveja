package com.corinto.estoquecerveja.service;

import lombok.AllArgsConstructor;
import com.corinto.estoquecerveja.dto.CervejaDTO;
import com.corinto.estoquecerveja.entity.Cerveja;
import com.corinto.estoquecerveja.exception.CervejaAlreadyRegisteredException;
import com.corinto.estoquecerveja.exception.CervejaNotFoundException;
import com.corinto.estoquecerveja.exception.CervejaStockExceededException;
import com.corinto.estoquecerveja.mapper.CervejaMapper;
import com.corinto.estoquecerveja.repository.CervejaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaService {

    private final CervejaRepository cervejaRepository;
    private final CervejaMapper cervejaMapper = CervejaMapper
            .INSTANCE;

    public CervejaDTO createCerveja(CervejaDTO cervejaDTO) throws CervejaAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(cervejaDTO.getNome());
        Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
        Cerveja savedCerveja = cervejaRepository.save(cerveja);
        return cervejaMapper.toDTO(savedCerveja);
    }

    public CervejaDTO findByNome(String nome) throws CervejaNotFoundException {
        Cerveja foundCerveja = cervejaRepository.findByNome(nome)
                .orElseThrow(() -> new CervejaNotFoundException(nome));
        return cervejaMapper.toDTO(foundCerveja);
    }

    public List<CervejaDTO> listAll() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws CervejaNotFoundException {
        verifyIfExists(id);
        cervejaRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String nome) throws CervejaAlreadyRegisteredException {
        Optional<Cerveja> optSavedBeer = cervejaRepository.findByNome(nome);
        if (optSavedBeer.isPresent()) {
            throw new CervejaAlreadyRegisteredException(nome);
        }
    }

    private Cerveja verifyIfExists(Long id) throws CervejaNotFoundException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new CervejaNotFoundException(id));
    }

    public CervejaDTO increment(Long id, int quantityToIncrement) throws CervejaNotFoundException, CervejaStockExceededException {
        Cerveja beerToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantidade();
        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantidade(beerToIncrementStock.getQuantidade() + quantityToIncrement);
            Cerveja incrementedBeerStock = cervejaRepository.save(beerToIncrementStock);
            return cervejaMapper.toDTO(incrementedBeerStock);
        }
        throw new CervejaStockExceededException(id, quantityToIncrement);
    }
}
