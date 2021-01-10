package com.corinto.estoquecerveja.builder;

import com.corinto.estoquecerveja.dto.CervejaDTO;
import com.corinto.estoquecerveja.enums.Tipo;
import lombok.Builder;

@Builder
public class CervejaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String nome = "Brahma";

    @Builder.Default
    private String marca = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantidade = 10;

    @Builder.Default
    private Tipo tipo = Tipo.LAGER;

    public CervejaDTO toCervejaDTO() {
        return new CervejaDTO(id,
                nome,
                marca,
                max,
                quantidade,
                tipo);
    }
}
