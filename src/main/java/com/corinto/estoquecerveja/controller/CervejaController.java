package com.corinto.estoquecerveja.controller;

import com.corinto.estoquecerveja.dto.CervejaDTO;
import com.corinto.estoquecerveja.dto.QuantidadeDTO;
import com.corinto.estoquecerveja.exception.CervejaAlreadyRegisteredException;
import com.corinto.estoquecerveja.exception.CervejaNotFoundException;
import com.corinto.estoquecerveja.exception.CervejaStockExceededException;
import com.corinto.estoquecerveja.service.CervejaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cervejas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaController implements CervejaControllerDocs {

    private final CervejaService cervejaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CervejaDTO createCerveja(@RequestBody @Valid CervejaDTO cervejaDTO) throws CervejaAlreadyRegisteredException {
        return cervejaService.createCerveja(cervejaDTO);
    }

    @GetMapping("/{nome}")
    public CervejaDTO findByNome(@PathVariable String nome) throws CervejaNotFoundException {
        return cervejaService.findByNome(nome);
    }

    @GetMapping
    public List<CervejaDTO> listCervejas() {
        return cervejaService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws CervejaNotFoundException {
        cervejaService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public CervejaDTO increment(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws CervejaNotFoundException, CervejaStockExceededException {
        return cervejaService.increment(id, quantidadeDTO.getQuantidade());
    }
}
